/*
 * FlightCrewMemberActivityLogCreateService.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flightAssignment.Duties;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flights.Leg;
import acme.realms.flightCrewMembers.AvailabilityStatus;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberFlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		FlightCrewMember member;
		int crewMemberId;

		crewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		member = this.repository.findCrewMemberById(crewMemberId);
		super.getResponse().setAuthorised(member.getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE));
	}

	@Override
	public void load() {
		FlightAssignment flightAssignment;
		FlightCrewMember member;
		Date moment;

		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		moment = MomentHelper.getCurrentMoment();

		flightAssignment = new FlightAssignment();
		flightAssignment.setDraftMode(true);
		flightAssignment.setFlightCrewMember(member);
		flightAssignment.setLastUpdate(moment);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void bind(final FlightAssignment flightAssignment) {
		super.bindObject(flightAssignment, "duty", "lastUpdate", "currentStatus", "remarks", "leg");
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		boolean hasNoSimultaneousLegs;
		//they cannot be assigned to multiple legs simultaneously
		Leg leg1 = flightAssignment.getLeg();
		hasNoSimultaneousLegs = this.repository.findSimultaneousLegs(flightAssignment.getId(), leg1.getId(), leg1.getScheduledDeparture(), leg1.getScheduledArrival(), flightAssignment.getFlightCrewMember().getId()).isEmpty();
		super.state(hasNoSimultaneousLegs, "hasNoSimultaneousLegs", "validation.flightAssignment.memberWithSimultaneousLegs");

		boolean legExists;
		Leg leg = this.repository.findLegById(flightAssignment.getLeg().getId());
		legExists = leg != null;
		super.state(legExists, "legExists", "validation.flightAssignment.legNotExists");

		boolean hasNoPilot;
		boolean hasNoCopilot;
		//each leg can only have one pilot and one co-pilot
		if (flightAssignment.getDuty() != null && flightAssignment.getDuty().equals(Duties.PILOT)) {
			hasNoPilot = !this.repository.findPresentRolesInLeg(flightAssignment.getLeg()).contains(Duties.PILOT);
			super.state(hasNoPilot, "hasNoPilot", "validation.flightAssignment.alreadyHasPilot");
		} else if (flightAssignment.getDuty() != null && flightAssignment.getDuty().equals(Duties.CO_PILOT)) {
			hasNoCopilot = !this.repository.findPresentRolesInLeg(flightAssignment.getLeg()).contains(Duties.CO_PILOT);
			super.state(hasNoCopilot, "hasNoCopilot", "validation.flightAssignment.alreadyHasCopilot");
		}

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		SelectChoices dutiesChoice;
		SelectChoices statusChoice;
		SelectChoices legChoices;
		Collection<Leg> legs;

		legs = this.repository.findAllLegs();

		legChoices = SelectChoices.from(legs, "scheduledArrival", flightAssignment.getLeg());
		dutiesChoice = SelectChoices.from(Duties.class, flightAssignment.getDuty());
		statusChoice = SelectChoices.from(AssignmentStatus.class, flightAssignment.getCurrentStatus());

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdate", "currentStatus", "remarks", "leg");
		dataset.put("duties", dutiesChoice);
		dataset.put("assignmentStatuses", statusChoice);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

}
