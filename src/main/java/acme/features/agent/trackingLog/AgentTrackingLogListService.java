
package acme.features.agent.trackingLog;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.TrackingLog;
import acme.realms.agents.Agent;

@GuiService
public class AgentTrackingLogListService extends AbstractGuiService<Agent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);
		status = claim != null && (!claim.isDraftMode() || super.getRequest().getPrincipal().hasRealm(claim.getAgent()));

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<TrackingLog> trackingLog;
		int masterId;

		masterId = super.getRequest().getData("masterId", int.class);
		trackingLog = this.repository.findTrackingLogsByMasterId(masterId);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;

		dataset = super.unbindObject(trackingLog, "step", "percentage");
		super.addPayload(dataset, trackingLog, "creationMoment", "updateMoment", "status", "resolution");

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<TrackingLog> trackingLog) {
		int masterId;
		Claim claim;
		final boolean showCreate;
		List<TrackingLog> completedTrackingLogs;
		List<TrackingLog> completedPublishedTrackingLogs;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);

		completedPublishedTrackingLogs = this.repository.findTrackingLogsByClaimIdWith100Percentage(masterId);
		completedTrackingLogs = this.repository.findTrackingLogsByClaimIdCompleted(masterId);

		boolean whenNotPublished = claim.isDraftMode() && completedTrackingLogs.size() < 1;

		boolean whenPublished = claim.isDraftMode() == false && (completedTrackingLogs.size() < 1 || completedPublishedTrackingLogs.size() == 1 && completedTrackingLogs.size() < 2);
		showCreate = (whenPublished || whenNotPublished) && super.getRequest().getPrincipal().hasRealm(claim.getAgent());

		super.getResponse().addGlobal("masterId", masterId);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
