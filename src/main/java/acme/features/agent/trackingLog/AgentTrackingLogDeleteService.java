
package acme.features.agent.trackingLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
import acme.entities.claims.TrackingLog;
import acme.realms.agents.Agent;

@GuiService
public class AgentTrackingLogDeleteService extends AbstractGuiService<Agent, TrackingLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int trackingLogId;
		Claim claim;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimByTrackingLogId(trackingLogId);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);
		status = claim != null && trackingLog.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAgent());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog;
		int id;

		id = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(id);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		super.bindObject(trackingLog, "step", "percentage", "updateMoment", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		;
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		this.repository.delete(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset;
		SelectChoices choicesStatus;
		List<TrackingLog> completedTrackingLogs;

		completedTrackingLogs = this.repository.findTrackingLogsByClaimIdWith100Percentage(trackingLog.getClaim().getId());
		boolean isLastOne = completedTrackingLogs.size() == 1;

		choicesStatus = SelectChoices.from(ClaimStatus.class, trackingLog.getStatus());

		dataset = super.unbindObject(trackingLog, "step", "percentage", "updateMoment", "resolution", "creationMoment", "status");
		dataset.put("statuses", choicesStatus);
		dataset.put("masterId", trackingLog.getClaim().getId());
		dataset.put("id", trackingLog.getId());
		dataset.put("isLastOne", isLastOne);
		dataset.put("draftMode", trackingLog.isDraftMode());
		dataset.put("draftModeMaster", trackingLog.getClaim().isDraftMode());

		super.getResponse().addData(dataset);
	}

}
