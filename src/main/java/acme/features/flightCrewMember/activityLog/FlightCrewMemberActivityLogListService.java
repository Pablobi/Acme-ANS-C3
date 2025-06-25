/*
 * FlightCrewMemberActivityLogListCompletedService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int assignmentId;
		FlightAssignment assignment;

		if (super.getRequest().hasData("assignmentId", int.class)) {
			assignmentId = super.getRequest().getData("assignmentId", int.class);
			assignment = this.repository.findAssignmentById(assignmentId);
			//el creador del assignment podrá listar los activity Logs siempre y cuando el assignment esté publicado
			status = assignment != null && !assignment.getDraftMode();
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<ActivityLog> activityLogs;
		int assignmentId;
		FlightAssignment assignment;

		assignmentId = super.getRequest().getData("assignmentId", int.class);
		assignment = this.repository.findAssignmentById(assignmentId);
		if (super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember()))
			activityLogs = this.repository.findActivityLogsByAssignmentId(assignmentId);
		else
			activityLogs = this.repository.findPublishedActivityLogsByAssignmentId(assignmentId);

		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");
		super.addPayload(dataset, activityLog, "registrationMoment", "typeOfIncident");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<ActivityLog> activityLogs) {
		int assignmentId;
		FlightAssignment assignment;
		final boolean showCreate;

		assignmentId = super.getRequest().getData("assignmentId", int.class);
		assignment = this.repository.findAssignmentById(assignmentId);
		showCreate = !assignment.getDraftMode() && super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember());

		super.getResponse().addGlobal("assignmentId", assignmentId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
