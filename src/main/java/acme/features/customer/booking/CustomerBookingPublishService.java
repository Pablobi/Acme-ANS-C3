
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.bookings.Booking;
import acme.entities.bookings.TravelClass;
import acme.entities.flights.Flight;
import acme.entities.passengers.Passenger;
import acme.realms.customers.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		Flight flight;

		int bookingId = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findBookingById(bookingId);
		Customer customer = booking.getCustomer();

		boolean status = super.getRequest().getPrincipal().hasRealm(customer) && booking.isDraftMode();

		if (status && super.getRequest().getMethod().equals("POST")) {
			Integer flightId = super.getRequest().getData("flight", Integer.class);
			if (flightId != null) {
				if (flightId != 0) {
					flight = this.repository.findFlightById(flightId);
					status = flight != null && !flight.isDraftMode() && flight.getScheduledDeparture().after(MomentHelper.getCurrentMoment());
					super.getResponse().setAuthorised(status);
				} else
					status = true;
				super.getResponse().setAuthorised(status);
			} else
				status = false;
			super.getResponse().setAuthorised(status);
		} else
			super.getResponse().setAuthorised(false);

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
		super.bindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits", "flight");
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
