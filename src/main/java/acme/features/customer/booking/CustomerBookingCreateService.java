
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
		Integer fligthId;
		Integer id;
		Flight flight;
		boolean status;

		if (super.getRequest().getMethod().equals("POST")) {
			fligthId = super.getRequest().getData("flight", Integer.class);
			id = super.getRequest().getData("id", Integer.class);
			if (id != 0)
				super.getResponse().setAuthorised(false);
			else if (fligthId != null) {
				if (fligthId != 0) {
					flight = this.repository.findFlightById(fligthId);
					status = flight != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
					super.getResponse().setAuthorised(status);
				} else
					super.getResponse().setAuthorised(true);
			} else
				super.getResponse().setAuthorised(false);
		} else
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

		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		Integer flightId;
		Flight flight;
		Date purchaseMoment;

		purchaseMoment = MomentHelper.getCurrentMoment();

		flightId = super.getRequest().getData("flight", int.class);
		flight = this.repository.findFlightById(flightId);

		super.bindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits");
		booking.setFlight(flight);
		booking.setPurchaseMoment(purchaseMoment);
	}

	@Override
	public void validate(final Booking booking) {
		;
	}

	@Override
	public void perform(final Booking booking) {
		booking.setDraftMode(true);

		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		Dataset dataset;

		SelectChoices classChoices;
		SelectChoices flightChoices;
		Collection<Flight> fligths;

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		fligths = this.repository.findAllFlights().stream().filter(f -> !f.isDraftMode() && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())).toList();
		flightChoices = SelectChoices.from(fligths, "tag", booking.getFlight());

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits", "draftMode");
		dataset.put("classes", classChoices);
		dataset.put("flights", flightChoices);

		super.getResponse().addData(dataset);
	}

}
