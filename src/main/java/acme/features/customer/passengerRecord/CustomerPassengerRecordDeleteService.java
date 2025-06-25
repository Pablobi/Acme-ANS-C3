
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
public class CustomerPassengerRecordDeleteService extends AbstractGuiService<Customer, PassengerRecord> {

	@Autowired
	protected CustomerPassengerRecordRepository repository;


	@Override
	public void authorise() {
		Passenger passenger;
		Integer bookingId;
		Booking booking;
		Customer customer;
		Collection<Passenger> passengers;

		bookingId = super.getRequest().getData("masterId", int.class);
		booking = this.repository.findBookingById(bookingId);
		customer = booking.getCustomer();
		passengers = this.repository.findAllPassengersByBookingId(bookingId);

		boolean status = super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();

		if (status && super.getRequest().getMethod().equals("POST")) {
			Integer passengerId = super.getRequest().getData("passenger", Integer.class);
			if (passengerId != null) {
				if (passengerId != 0) {
					passenger = this.repository.findPassengerById(passengerId);
					status = passengers.contains(passenger) && passenger != null;
				} else
					status = true;
				super.getResponse().setAuthorised(status);
			} else
				status = false;
			super.getResponse().setAuthorised(status);
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		PassengerRecord passengerRecord;
		Integer passengerId;
		Booking booking;

		if (super.getRequest().getMethod().equals("POST")) {
			passengerId = super.getRequest().getData("passenger", Integer.class);
			if (passengerId == null || passengerId == 0) {
				booking = this.repository.findBookingByLocatorCode(super.getRequest().getData("locatorCode", String.class));
				passengerRecord = new PassengerRecord();
				passengerRecord.setPassenger(null);
				passengerRecord.setBooking(booking);
				super.getBuffer().addData(passengerRecord);
			} else {
				booking = this.repository.findBookingByLocatorCode(super.getRequest().getData("locatorCode", String.class));
				passengerRecord = this.repository.findPassengerRecordByPassengerBooking(passengerId, booking.getId());
				super.getBuffer().addData(passengerRecord);
			}
		} else {
			passengerRecord = new PassengerRecord();
			passengerRecord.setBooking(this.repository.findBookingById(super.getRequest().getData("masterId", Integer.class)));
			super.getBuffer().addData(passengerRecord);
		}
	}

	@Override
	public void bind(final PassengerRecord object) {
		;
	}

	@Override
	public void validate(final PassengerRecord object) {
		boolean status;

		status = object.getPassenger() != null;
		super.state(status, "*", "customer.passenger-record.delete.passenger.not-null");
	}

	@Override
	public void perform(final PassengerRecord pr) {
		this.repository.delete(pr);
	}

	@Override
	public void unbind(final PassengerRecord pr) {
		Dataset dataset;
		SelectChoices passengerChoices;
		Collection<Passenger> passengersInBooking;
		Integer bookingId;
		Booking booking;
		String locatorCode;

		bookingId = super.getRequest().getData("masterId", Integer.class);
		booking = this.repository.findBookingById(bookingId);
		passengersInBooking = this.repository.findAllPassengersByBookingId(bookingId);
		locatorCode = booking.getLocatorCode();

		passengerChoices = SelectChoices.from(passengersInBooking, "name", pr.getPassenger());

		dataset = super.unbindObject(pr, "passenger", "booking");
		dataset.put("passengers", passengerChoices);
		dataset.put("locatorCode", locatorCode);

		super.getResponse().addData(dataset);
	}

}
