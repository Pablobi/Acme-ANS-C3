
package acme.features.customer.booking;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.datatypes.Money;
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
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		Integer bookingId;
		Booking booking;
		Customer customer;
		boolean status;

		bookingId = super.getRequest().getData("id", Integer.class);
		booking = this.repository.findBookingById(bookingId);

		if (booking != null) {
			customer = booking.getCustomer();
			status = super.getRequest().getPrincipal().hasRealm(customer) || !booking.isDraftMode() && super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		} else
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
	public void unbind(final Booking booking) {
		Dataset dataset;
		Money price;
		Date purchaseMoment;
		SelectChoices classChoices;
		SelectChoices validFlightChoices;
		SelectChoices allFlightsChoices;
		Collection<Flight> validFlights;
		Collection<Flight> allFlights;

		price = booking.getPrice();
		purchaseMoment = booking.getPurchaseMoment();

		classChoices = SelectChoices.from(TravelClass.class, booking.getTravelClass());
		validFlights = this.repository.findAllFlights().stream().filter(f -> !f.isDraftMode() && f.getScheduledDeparture().after(MomentHelper.getCurrentMoment())).toList();
		allFlights = this.repository.findAllFlights().stream().filter(f -> !f.isDraftMode()).toList();

		dataset = super.unbindObject(booking, "locatorCode", "travelClass", "lastCreditCardDigits", "draftMode");

		if (!booking.isDraftMode()) {
			allFlightsChoices = SelectChoices.from(allFlights, "tag", booking.getFlight());
			dataset.put("flights", allFlightsChoices);
			dataset.put("price", price);
			dataset.put("purchaseMoment", purchaseMoment);
			dataset.put("classes", classChoices);
			super.getResponse().addData(dataset);
		} else {
			if (validFlights.contains(booking.getFlight())) {
				validFlightChoices = SelectChoices.from(validFlights, "tag", booking.getFlight());
				dataset.put("flights", validFlightChoices);
			} else
				dataset.put("flights", SelectChoices.from(validFlights, "tag", null));

			dataset.put("classes", classChoices);
			super.getResponse().addData(dataset);
		}

	}

}
