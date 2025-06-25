/*
 * FlightCrewMemberActivityLogController.java
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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiController
public class FlightCrewMemberFlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	//@Autowired
	//private FlightCrewMemberCompletedFlightAssignmentListService		listPublishedService;

	@Autowired
	private FlightCrewMemberCompletedFlightAssignmentListService	listCompletedService;

	@Autowired
	private FlightCrewMemberUncompletedFlightAssignmentListService	listUncompletedService;

	@Autowired
	private FlightCrewMemberFlightAssignmentShowService				showService;

	@Autowired
	private FlightCrewMemberFlightAssignmentCreateService			createService;

	@Autowired
	private FlightCrewMemberFlightAssignmentDeleteService			deleteService;

	@Autowired
	private FlightCrewMemberFlightAssignmentUpdateService			updateService;

	@Autowired
	private FlightCrewMemberFlightAssignmentPublishService			publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		//super.addCustomCommand("list-published", "list", this.listPublishedService);
		super.addCustomCommand("list-uncompleted", "list", this.listUncompletedService);
		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);

		super.addCustomCommand("publish", "update", this.publishService);
	}

}
