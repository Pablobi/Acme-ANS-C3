
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
public class AgentTrackingLogPublishService extends AbstractGuiService<Agent, TrackingLog> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		boolean status2;
		int trackingLogId;
		Claim claim;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimByTrackingLogId(trackingLogId);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);
		status = claim != null && trackingLog.isDraftMode() && !claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(claim.getAgent());

		String cStatus;
		if (super.getRequest().getMethod().equals("GET"))
			status2 = true;
		else {
			cStatus = super.getRequest().getData("status", String.class);
			status2 = false;

			for (ClaimStatus st : ClaimStatus.values())
				if (cStatus.toLowerCase().trim().equals(st.toString().toLowerCase().trim()) || cStatus.equals("0")) {
					status2 = true;
					break;
				}
		}

		super.getResponse().setAuthorised(status && status2);
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
		List<TrackingLog> completedTrackingLogs;

		completedTrackingLogs = this.repository.findTrackingLogsByClaimIdWith100Percentage(trackingLog.getClaim().getId());

		if (completedTrackingLogs.size() >= 1) {
			trackingLog.setStatus(completedTrackingLogs.get(0).getStatus());
			trackingLog.setPercentage(100.00);
			super.bindObject(trackingLog, "step", "resolution");

		} else
			super.bindObject(trackingLog, "step", "percentage", "status", "resolution");

	}

	@Override
	public void validate(final TrackingLog trackingLog) {

		if (this.repository.findPublishedTrackingLogsByMasterId(trackingLog.getClaim().getId()).size() > 0) {
			boolean ascending;
			TrackingLog actualMax = this.repository.findPublishedTrackingLogsOrderedByPercentage(trackingLog.getClaim().getId()).get(0);
			if (trackingLog.getPercentage() == 100.00)
				ascending = trackingLog.getPercentage() >= actualMax.getPercentage();
			else
				ascending = trackingLog.getPercentage() > actualMax.getPercentage();
			super.state(ascending, "percentage", "validation.trackingLog.ascending-percentage.message");
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setDraftMode(false);
		this.repository.save(trackingLog);
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
