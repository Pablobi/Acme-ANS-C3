
package acme.features.agent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claims.Claim;
import acme.realms.agents.Agent;

@GuiController
public class AgentClaimController extends AbstractGuiController<Agent, Claim> {

	@Autowired
	private AgentClaimListCompletedService	listCompletedService;

	@Autowired
	private AgentClaimListPendingService	listPendingService;

	@Autowired
	private AgentClaimShowService			showService;

	@Autowired
	private AgentClaimCreateService			createService;

	@Autowired
	private AgentClaimUpdateService			updateService;

	@Autowired
	private AgentClaimDeleteService			deleteService;

	@Autowired
	private AgentClaimPublishService		publishService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("list-pending", "list", this.listPendingService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
	}

}
