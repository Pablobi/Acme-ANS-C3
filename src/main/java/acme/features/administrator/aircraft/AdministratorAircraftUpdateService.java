
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
public class AdministratorAircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

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
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		int airlineId;
		Airline airline;

		airlineId = super.getRequest().getData("airline", Integer.class);
		airline = this.repository.findAirlineById(airlineId);

		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeigth", "status", "details");
		aircraft.setAirline(airline);
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean status;
		Integer airlineId;
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		airlineId = super.getRequest().getData("airline", Integer.class);
		status = airlineId != null;
		super.state(status, "*", "administrator.aircraft.create.null-airline");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices statusChoices;
		SelectChoices airlineChoices;
		Collection<Airline> airlines;

		airlines = this.repository.findAllAirlines();
		airlineChoices = SelectChoices.from(airlines, "name", aircraft.getAirline());
		statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeigth", "status", "details");
		dataset.put("statuses", statusChoices);
		dataset.put("airlines", airlineChoices);

		super.getResponse().addData(dataset);
	}
}
