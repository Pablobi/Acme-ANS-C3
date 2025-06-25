
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.passengers.PassengerRecord;
import acme.realms.customers.Customer;

@GuiService
public class CustomerBookingDeleteService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		Integer bookingId;
		Booking booking;
		Customer customer;
		boolean status;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		customer = booking.getCustomer();
		status = super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();

		if (super.getRequest().getMethod().equals("GET"))
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking booking;
		int id;

		id = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(id);

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		;
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		Collection<PassengerRecord> passengerRecords;
		passengerRecords = this.repository.findPassengerRecordsByBookingId(booking.getId());
		for (final PassengerRecord passengerRecord : passengerRecords)
			this.repository.delete(passengerRecord);
		this.repository.delete(booking);

	}

}
