
package acme.features.authenticated.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;

@GuiService
public class AuthenticatedFlightAssignmentListService extends AbstractGuiService<Authenticated, FlightAssignment> {

	@Autowired
	private AuthenticatedFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().isAuthenticated();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Collection<FlightAssignment> flightAssignments = this.repository.findAllPublishedFlightAssignments();

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdate", "leg.scheduledArrival", "flightCrewMember.identity.fullName", "currentStatus");

		super.getResponse().addData(dataset);
	}

}
