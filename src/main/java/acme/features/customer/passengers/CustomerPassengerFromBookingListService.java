
package acme.features.customer.passengers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.realms.customers.Customer;

@GuiService
public class CustomerPassengerFromBookingListService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		Integer bookingId;
		Booking booking;
		Customer customer;
		boolean status;

		bookingId = super.getRequest().getData("masterId", Integer.class);
		booking = this.repository.findBookingById(bookingId);
		if (booking == null) {
			status = false;
			super.getResponse().setAuthorised(status);
		} else {
			customer = booking.getCustomer();
			status = super.getRequest().getPrincipal().hasRealm(customer);
			super.getResponse().setAuthorised(status);
		}
	}

	@Override
	public void load() {
		Collection<Passenger> passengers;
		int bookingId;

		bookingId = super.getRequest().getData("masterId", Integer.class);
		passengers = this.repository.findAllPassengersByBookingId(bookingId);

		super.getBuffer().addData(passengers);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;

		dataset = super.unbindObject(passenger, "name", "email", "passport", "dateOfBirth");
		super.addPayload(dataset, passenger, "specialNeeds");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Passenger> passengers) {
		int masterId;
		Booking booking;
		final boolean showCreate;

		masterId = super.getRequest().getData("masterId", Integer.class);
		booking = this.repository.findBookingById(masterId);
		showCreate = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}
}
