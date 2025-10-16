
package acme.features.flightCrewMember.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.forms.FlightCrewMemberDashboard;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiController
public class FlightCrewMemberDashboardController extends AbstractGuiController<FlightCrewMember, FlightCrewMemberDashboard> {

	@Autowired
	private FlightCrewMemberDashboardShowService showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
	}

}
