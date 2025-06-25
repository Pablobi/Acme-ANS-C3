/*
 * FlightCrewMemberActivityLogUpdateService.java
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
public class FlightCrewMemberFlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightCrewMemberFlightAssignmentRepository repository;

	// AbstractGuiService interfaced ------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int assignmentId, legId;
		FlightAssignment assignment;
		FlightCrewMember member;

		if (super.getRequest().hasData("id", int.class)) {
			assignmentId = super.getRequest().getData("id", int.class);
			assignment = this.repository.findFlightAssignmentById(assignmentId);
			member = assignment == null ? null : assignment.getFlightCrewMember();
			status = assignment != null && assignment.getDraftMode() && super.getRequest().getPrincipal().hasRealm(member) && member.getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE);

			if (status)
				if (super.getRequest().getMethod().equals("GET"))
					status = true;
				else if (super.getRequest().hasData("leg", int.class)) {
					legId = super.getRequest().getData("leg", int.class);
					Leg leg = this.repository.findLegById(legId);
					status = leg != null && !leg.isDraftMode() || legId == 0;
					if (status && leg != null && legId != 0) {
						boolean hasNoSimultaneousLegs = this.repository.findSimultaneousLegs(assignmentId, leg.getId(), leg.getScheduledDeparture(), leg.getScheduledArrival(), member.getId()).isEmpty();
						status = hasNoSimultaneousLegs;
					}
				} else
					status = false;

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
	public void bind(final FlightAssignment flightAssignment) {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);
		super.bindObject(flightAssignment, "duty", "currentStatus", "remarks");
		flightAssignment.setLeg(leg);
	}

	@Override
	public void validate(final FlightAssignment flightAssignment) {

		boolean hasNoSimultaneousLegs;
		//they cannot be assigned to multiple legs simultaneously
		Leg leg1 = flightAssignment.getLeg();
		if (leg1 != null) {
			hasNoSimultaneousLegs = this.repository.findSimultaneousLegs(flightAssignment.getId(), leg1.getId(), leg1.getScheduledDeparture(), leg1.getScheduledArrival(), flightAssignment.getFlightCrewMember().getId()).isEmpty();
			super.state(hasNoSimultaneousLegs, "leg", "validation.flightAssignment.memberWithSimultaneousLegs");

			boolean legIsPublished;
			//un assignment sólo puede ser creado si su leg está publicado
			legIsPublished = !leg1.isDraftMode();
			super.state(legIsPublished, "leg", "validation.flightAssignment.legNotPublished");

			boolean hasNoPilot;
			boolean hasNoCopilot;
			//each leg can only have one pilot and one co-pilot
			if (flightAssignment.getDuty() != null && flightAssignment.getDuty().equals(Duties.PILOT)) {

				hasNoPilot = !this.repository.legHasDuty(flightAssignment.getLeg().getId(), flightAssignment.getId(), Duties.PILOT);
				super.state(hasNoPilot, "duty", "validation.flightAssignment.alreadyHasPilot");

			} else if (flightAssignment.getDuty() != null && flightAssignment.getDuty().equals(Duties.CO_PILOT)) {

				hasNoCopilot = !this.repository.legHasDuty(flightAssignment.getLeg().getId(), flightAssignment.getId(), Duties.CO_PILOT);
				super.state(hasNoCopilot, "duty", "validation.flightAssignment.alreadyHasCopilot");
			}
		}

	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		//al guardar, re asigna el miembro y la ultima actualizacion
		flightAssignment.setFlightCrewMember((FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm());
		flightAssignment.setLastUpdate(MomentHelper.getCurrentMoment());

		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		SelectChoices dutiesChoice;
		SelectChoices statusChoice;
		SelectChoices legChoices;
		Collection<Leg> legs;
		Collection<FlightAssignment> memberAssignments;

		legs = this.repository.findAllPublishedLegs();
		memberAssignments = this.repository.findFlightAssignmentsByCrewMemberId(super.getRequest().getPrincipal().getActiveRealm().getId());

		legs.removeIf(leg -> leg != flightAssignment.getLeg() && memberAssignments.stream()
			.anyMatch(assignment -> assignment != flightAssignment && assignment.getLeg().getScheduledDeparture().before(leg.getScheduledArrival()) && assignment.getLeg().getScheduledArrival().after(leg.getScheduledDeparture())));

		legChoices = SelectChoices.from(legs, "scheduledArrival", flightAssignment.getLeg());
		dutiesChoice = SelectChoices.from(Duties.class, flightAssignment.getDuty());
		statusChoice = SelectChoices.from(AssignmentStatus.class, flightAssignment.getCurrentStatus());

		dataset = super.unbindObject(flightAssignment, "duty", "lastUpdate", "currentStatus", "remarks", "draftMode");
		dataset.put("duties", dutiesChoice);
		dataset.put("assignmentStatuses", statusChoice);
		dataset.put("leg", legChoices.getSelected().getKey());
		dataset.put("legs", legChoices);

		super.getResponse().addData(dataset);
	}

}
