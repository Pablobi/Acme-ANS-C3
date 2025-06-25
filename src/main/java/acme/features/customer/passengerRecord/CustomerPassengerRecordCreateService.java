
package acme.features.customer.passengerRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.entities.passengers.PassengerRecord;
import acme.realms.customers.Customer;

@GuiService
public class CustomerPassengerRecordCreateService extends AbstractGuiService<Customer, PassengerRecord> {

	@Autowired
	private CustomerPassengerRecordRepository repository;


	@Override
	public void authorise() {
		Passenger passenger;
		Integer bookingId;
		Booking booking;
		Customer customer;
		Collection<Passenger> passengers;
		Collection<Passenger> passengersInBooking;

		bookingId = super.getRequest().getData("masterId", Integer.class);
		if (bookingId != null) {
			booking = this.repository.findBookingById(bookingId);
			if (booking == null)
				super.getResponse().setAuthorised(false);
			else {
				customer = booking.getCustomer();
				passengersInBooking = this.repository.findAllPassengersByBookingId(booking.getId());
				passengers = this.repository.findAllPublishedPassengersByCustomerId(customer.getId()).stream().filter(x -> !passengersInBooking.contains(x)).toList();

				boolean status = super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();

				if (status && super.getRequest().getMethod().equals("POST")) {
					Integer passengerId = super.getRequest().getData("passenger", Integer.class);
					Integer id = super.getRequest().getData("id", Integer.class);
					if (id == null)
						status = false;
					else if (id != 0)
						status = false;
					else if (passengerId != null) {
						if (passengerId != 0) {
							passenger = this.repository.findPassengerById(passengerId);
							status = passengers.contains(passenger);
						} else
							status = true;
					} else
						status = false;
				}

				super.getResponse().setAuthorised(status);
			}
		} else
			super.getResponse().setAuthorised(false);
	}

	@Override
	public void load() {
		PassengerRecord takes;
		Booking booking;
		booking = this.repository.findBookingById(super.getRequest().getData("masterId", Integer.class));

		takes = new PassengerRecord();
		takes.setBooking(booking);
		super.getBuffer().addData(takes);
	}

	@Override
	public void bind(final PassengerRecord takes) {
		super.bindObject(takes, "passenger");
	}

	@Override
	public void validate(final PassengerRecord takes) {
		;
	}

	@Override
	public void perform(final PassengerRecord takes) {
		this.repository.save(takes);
	}

	@Override
	public void unbind(final PassengerRecord takes) {
		Dataset dataset;
		SelectChoices passengerChoices;
		Collection<Passenger> passengers;
		Collection<Passenger> passengersInBooking;
		Integer customerId;
		Integer bookingId;
		Booking booking;
		String locatorCode;

		bookingId = super.getRequest().getData("masterId", Integer.class);
		booking = this.repository.findBookingById(bookingId);

		locatorCode = booking.getLocatorCode();
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		passengersInBooking = this.repository.findAllPassengersByBookingId(booking.getId());
		passengers = this.repository.findAllPublishedPassengersByCustomerId(customerId).stream().filter(x -> !passengersInBooking.contains(x)).toList();
		passengerChoices = SelectChoices.from(passengers, "name", takes.getPassenger());

		dataset = super.unbindObject(takes, "passenger", "booking");
		dataset.put("passengers", passengerChoices);
		dataset.put("locatorCode", locatorCode);
		dataset.put("booking", booking);

		super.getResponse().addData(dataset);
	}

}
