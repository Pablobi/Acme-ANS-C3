
package acme.features.administrator.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.forms.AgentDashboard;

@GuiController
public class AdministratorAgentDashboardController extends AbstractGuiController<Administrator, AgentDashboard> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAgentDashboardShowService showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.showService);
	}
}
