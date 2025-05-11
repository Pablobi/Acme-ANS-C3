/*
 * FlightCrewMemberActivityLogShowService.java
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

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		ActivityLog activityLog;
		int id;
		int flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		boolean status;
		FlightAssignment assignment;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		assignment = activityLog.getAssignment();
		//el 'or' de abajo indica que estarás autorizado a listar si eres quien creó el assignment o, si no, que el assignment esté publicado(no esté en draftMode)
		status = activityLog != null && assignment != null && this.repository.existsFlightCrewMember(flightCrewMemberId) && (super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember()) || !activityLog.getDraftMode());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(id);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");
		dataset.put("assignmentId", activityLog.getAssignment().getId());
		dataset.put("id", activityLog.getId());
		dataset.put("draftMode", activityLog.getDraftMode());

		super.getResponse().addData(dataset);
	}

}
