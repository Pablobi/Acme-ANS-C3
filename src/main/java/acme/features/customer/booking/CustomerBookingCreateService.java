
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.realms.customers.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Booking booking;
		Customer customer;
		Integer customerId;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		customer = this.repository.findCustomerById(customerId);
		booking = new Booking();
		booking.setCustomer(customer);
		booking.setDraftMode(true);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		Integer customerId;
		Customer customer;
		Integer flightId;
		Flight flight;
		Date purchaseMoment;

		purchaseMoment = MomentHelper.getCurrentMoment();

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		customer = this.repository.findCustomerById(customerId);

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits");
		booking.setCustomer(customer);
		booking.setFlight(flight);
		booking.setPurchaseMoment(purchaseMoment);
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;

		SelectChoices classChoices;
		SelectChoices flightChoices;
		Collection<Flight> fligths;

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		fligths = this.repository.findAllFlights();
		flightChoices = SelectChoices.from(fligths, "tag", booking.getFlight());

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits", "draftMode");
		dataset.put("classes", classChoices);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
