
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftStatus;
import acme.entities.airlines.Airline;

@GuiService
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	@Autowired
	private AdministratorAircraftRepository repository;


	@Override
	public void authorise() {
		Integer airlineId;
		Airline airline;
		boolean status;
		Collection<Airline> airlines;

		airlines = this.repository.findAllAirlines();

		if (super.getRequest().getMethod().equals("POST")) {
			airlineId = super.getRequest().getData("airline", Integer.class);
			if (airlineId != 0) {
				airline = this.repository.findAirlineById(airlineId);
				status = airlines.contains(airline) && airline != null;
				super.getResponse().setAuthorised(status);
			} else
				super.getResponse().setAuthorised(true);
		} else
			super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Aircraft aircraft;

		aircraft = new Aircraft();
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		Airline airline = null;

		Integer airlineId = super.getRequest().getData("airline", Integer.class);

		if (airlineId != null)
			airline = this.repository.findAirlineById(airlineId);

		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeigth", "status", "details");
		aircraft.setAirline(airline);
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices statusChoices;
		SelectChoices airlinesChoices;
		Collection<Airline> airlines;

		airlines = this.repository.findAllAirlines();
		airlinesChoices = SelectChoices.from(airlines, "name", aircraft.getAirline());

		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeigth", "status", "details");
		dataset.put("statuses", statusChoices);
		dataset.put("airlines", airlinesChoices);

		super.getResponse().addData(dataset);
	}

}
