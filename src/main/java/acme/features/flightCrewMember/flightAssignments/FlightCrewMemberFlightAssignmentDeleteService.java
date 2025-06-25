
package acme.features.flightCrewMember.flightAssignments;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {

		boolean status;
		int assignmentId;
		FlightAssignment assignment;

		if (super.getRequest().hasData("id", int.class)) {
			assignmentId = super.getRequest().getData("id", int.class);
			assignment = this.repository.findFlightAssignmentById(assignmentId);
			// tiene que estar publicado el assignment y en draftMode el activity log, además tienes que ser el member del assignment
			status = assignment != null && assignment.getDraftMode() && super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember());
		} else
			status = false;
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		FlightAssignment assignment;
		int id;

		id = super.getRequest().getData("id", int.class);
		assignment = this.repository.findFlightAssignmentById(id);

		super.getBuffer().addData(assignment);

	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		//no puede haber activityLogs para un assignment que no esté publicado, por lo que no hará falta hacer el borrado de estos
		this.repository.delete(assignment);
	}

	//	@Override
	//	public void unbind(final FlightAssignment flightAssignment) {
	//		Dataset dataset;
	//		SelectChoices dutiesChoice;
	//		SelectChoices statusChoice;
	//		SelectChoices legChoices;
	//		Collection<Leg> legs;
	//		Collection<FlightAssignment> memberAssignments;
	//
	//		legs = this.repository.findAllPublishedLegs();
	//		memberAssignments = this.repository.findFlightAssignmentsByCrewMemberId(super.getRequest().getPrincipal().getActiveRealm().getId());
	//
	//		legs.removeIf(leg -> leg != flightAssignment.getLeg() && memberAssignments.stream()
	//			.anyMatch(assignment -> assignment != flightAssignment && assignment.getLeg().getScheduledDeparture().before(leg.getScheduledArrival()) && assignment.getLeg().getScheduledArrival().after(leg.getScheduledDeparture())));
	//
	//		legChoices = SelectChoices.from(legs, "scheduledArrival", flightAssignment.getLeg());
	//		dutiesChoice = SelectChoices.from(Duties.class, flightAssignment.getDuty());
	//		statusChoice = SelectChoices.from(AssignmentStatus.class, flightAssignment.getCurrentStatus());
	//
	//		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
	//		dataset.put("duties", dutiesChoice);
	//		dataset.put("assignmentStatuses", statusChoice);
	//		dataset.put("leg", legChoices.getSelected().getKey());
	//		dataset.put("legs", legChoices);
	//
	//		super.getResponse().addData(dataset);
	//	}

}
