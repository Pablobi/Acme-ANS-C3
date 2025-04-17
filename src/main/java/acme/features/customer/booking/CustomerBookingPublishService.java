
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.passengers.Passenger;
import acme.realms.customers.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		int customerId;
		Booking booking;
		Customer customer;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findBookingById(bookingId);

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		customer = this.repository.findCustomerById(customerId);

		status = booking != null && booking.isDraftMode() && super.getRequest().getPrincipal().hasRealm(customer);
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
		Integer customerId;
		Customer customer;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		customer = this.repository.findCustomerById(customerId);

		super.bindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits", "purchaseMoment");
		booking.setCustomer(customer);
	}

	@Override
	public void validate(final Booking booking) {
		boolean statusNoPassengers;
		boolean statusPassengersNotPublished;
		boolean statusCreditCardNibble;

		Collection<Passenger> passengers;
		Integer nibble;

		boolean passengersPublished = true;
		passengers = this.repository.findAllPassengersByBookingId(booking.getId());
		nibble = this.repository.findCreditCardNibbleByBookingId(booking.getId());

		for (Passenger p : passengers) {
			passengersPublished = !p.isDraftMode();
			if (!passengersPublished)
				break;
		}

		statusNoPassengers = !passengers.isEmpty();
		statusPassengersNotPublished = passengersPublished;
		statusCreditCardNibble = nibble != null;

		super.state(statusNoPassengers, "*", "customer.booking.publish.no-passengers");
		super.state(statusPassengersNotPublished, "*", "customer.booking.publish.no-passengers-published");
		super.state(statusCreditCardNibble, "*", "customer.booking.publish.no-card-nibble-stored");

		//flight = booking.getFlight();
		//status = flight.getScheduledDeparture() < MomentHelper.getCurrentMoment() && flight.getDraftMode() == false;
		//super.state(status, "*", "customer.booking.create.no-passe");
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(false);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;
		SelectChoices classChoices;
		String tag = booking.getFlight().getTag();
		Money price = booking.getPrice();

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits", "purchaseMoment", "draftMode");
		dataset.put("classes", classChoices);
		dataset.put("tag", tag);
		dataset.put("price", price);

		super.getResponse().addData(dataset);
	}

}
