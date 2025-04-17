
package acme.features.customer.booking;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.realms.customers.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

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

		Date purchaseMoment;

		purchaseMoment = booking.getPurchaseMoment();

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		customer = this.repository.findCustomerById(customerId);

		super.bindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits");
		booking.setCustomer(customer);
		booking.setPurchaseMoment(purchaseMoment);
	}

	@Override
	public void validate(final Booking booking) {
		boolean status;

		status = booking.isDraftMode();

		super.state(status, "*", "customer.booking.update.published-error");
		//boolean status;
		//Flight flight;

		//flight = booking.getFlight();

		//status = flight.getScheduledDeparture() < MomentHelper.getCurrentMoment() && flight.getDraftMode() == false;
		//super.state(status, "*", "customer.booking.update.invalid-flight");
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
