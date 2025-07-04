
package acme.features.authenticated.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;

@GuiService
public class AuthenticatedFlightAssignmentShowService extends AbstractGuiService<Authenticated, FlightAssignment> {

	@Autowired
	private AuthenticatedFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		Integer flightAssignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		Boolean status = super.getRequest().getPrincipal().isAuthenticated() && flightAssignment != null && !flightAssignment.getDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Integer id = super.getRequest().getData("id", int.class);
		FlightAssignment flightAssignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {

		Dataset dataset = super.unbindObject(flightAssignment, "duty", "currentStatus", "leg.scheduledArrival", "flightCrewMember.identity.fullName", "lastUpdate", "remarks");

		super.getResponse().addData(dataset);
	}

}
