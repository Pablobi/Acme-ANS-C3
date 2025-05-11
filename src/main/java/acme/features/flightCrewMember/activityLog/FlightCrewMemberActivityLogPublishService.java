/*
 * FlightCrewMemberActivityLogPublishService.java
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

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberActivityLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int activityLogId;
		FlightAssignment assignment;
		ActivityLog activityLog;

		activityLogId = super.getRequest().getData("id", int.class);
		activityLog = this.repository.findActivityLogById(activityLogId);
		assignment = this.repository.findAssignmentByActivityLogId(activityLogId);
		// tiene que estar publicado el assignment y en draftMode el activity log, además tienes que ser el member del assignment
		status = assignment != null && !assignment.getDraftMode() && activityLog.getDraftMode() && super.getRequest().getPrincipal().hasRealm(assignment.getFlightCrewMember());

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
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		boolean legHasEnded;
		boolean assignmentIsPublished;
		Date now;

		now = MomentHelper.getCurrentMoment();

		//They are logged by any of the flight crew members assigned to the corresponding leg and after the leg has taken place.(from D02)
		legHasEnded = MomentHelper.isAfter(now, activityLog.getAssignment().getLeg().getScheduledArrival());
		super.state(legHasEnded, "legHasEnded", "validation.activityLog.legHasNotEnded");

		//They cannot be published if their corresponding flight assignments have not been published yet
		assignmentIsPublished = !activityLog.getAssignment().getDraftMode();
		super.state(assignmentIsPublished, "assignmentIsPublished", "validation.activityLog.assignmentIsNotPublished");

	}

	@Override
	public void perform(final ActivityLog activityLog) {
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setDraftMode(false);
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;
		Date moment;

		moment = MomentHelper.getCurrentMoment();

		dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "description", "severityLevel");
		dataset.put("assignmentId", activityLog.getAssignment().getId());
		dataset.put("registrationMoment", moment);

		super.getResponse().addData(dataset);
	}

}
