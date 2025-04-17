
package acme.features.agent.trackingLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claims.TrackingLog;
import acme.realms.agents.Agent;

@GuiController
public class AgentTrackingLogController extends AbstractGuiController<Agent, TrackingLog> {

	@Autowired
	private AgentTrackingLogListService		listService;

	@Autowired
	private AgentTrackingLogShowService		showService;

	@Autowired
	private AgentTrackingLogCreateService	createService;

	@Autowired
	private AgentTrackingLogDeleteService	deleteService;

	@Autowired
	private AgentTrackingLogUpdateService	updateService;

	@Autowired
	private AgentTrackingLogPublishService	publishService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);

	}
}
