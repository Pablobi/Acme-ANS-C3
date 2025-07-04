
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.entities.airlines.AirlineType;
import acme.features.administrator.aircraft.AdministratorAircraftRepository;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String airlineType;

		if (super.getRequest().getMethod().equals("GET"))
			status = true;
		else {
			airlineType = super.getRequest().getData("type", String.class);
			status = false;

			for (AirlineType aType : AirlineType.values())
				if (airlineType.toLowerCase().trim().equals(aType.toString().toLowerCase().trim()) || airlineType.equals("0")) {
					status = true;
					break;
				}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline;
		int id;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(id);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");
	}

	@Override
	public void perform(final Airline airline) {
		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		Dataset dataset;
		SelectChoices typeChoices;

		typeChoices = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationMoment", "email", "phoneNumber");
		dataset.put("types", typeChoices);

		super.getResponse().addData(dataset);
	}

}
