
package acme.features.agent.trackingLog;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
import acme.entities.claims.TrackingLog;
import acme.realms.agents.Agent;

@GuiService
public class AgentTrackingLogCreateService extends AbstractGuiService<Agent, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentTrackingLogRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		List<TrackingLog> completedTrackingLogs;
		List<TrackingLog> completedPublishedTrackingLogs;

		masterId = super.getRequest().getData("masterId", int.class);

		completedPublishedTrackingLogs = this.repository.findTrackingLogsByClaimIdWith100Percentage(masterId);
		completedTrackingLogs = this.repository.findTrackingLogsByClaimIdCompleted(masterId);
		claim = this.repository.findClaimById(masterId);

		boolean whenNotPublished = claim.isDraftMode() && completedTrackingLogs.size() < 1 && completedTrackingLogs.size() < 2;
		boolean whenPublished = claim.isDraftMode() == false && completedPublishedTrackingLogs.size() == 1;

		status = claim != null && (whenPublished || whenNotPublished) && super.getRequest().getPrincipal().hasRealm(claim.getAgent());

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		TrackingLog trackingLog;
		int masterId;
		Date currentDate;
		List<TrackingLog> completedTrackingLogs;

		masterId = super.getRequest().getData("masterId", int.class);
		claim = this.repository.findClaimById(masterId);
		completedTrackingLogs = this.repository.findTrackingLogsByClaimIdWith100Percentage(masterId);

		currentDate = MomentHelper.getCurrentMoment();

		trackingLog = new TrackingLog();
		if (completedTrackingLogs.size() == 1) {
			trackingLog.setStatus(completedTrackingLogs.get(0).getStatus());
			trackingLog.setPercentage(100.00);
		} else
			trackingLog.setPercentage(0.00);

		trackingLog.setDraftMode(true);
		trackingLog.setCreationMoment(currentDate);
		trackingLog.setUpdateMoment(currentDate);
		trackingLog.setStep("");
		trackingLog.setResolution("");
		trackingLog.setClaim(claim);

		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		trackingLog.setCreationMoment(MomentHelper.getCurrentMoment());
		trackingLog.setUpdateMoment(MomentHelper.getCurrentMoment());
		super.bindObject(trackingLog, "step", "percentage", "updateMoment", "status", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		;
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
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
		dataset.put("masterId", super.getRequest().getData("masterId", int.class));
		dataset.put("id", trackingLog.getId());
		dataset.put("isLastOne", isLastOne);
		dataset.put("draftMode", trackingLog.isDraftMode());
		dataset.put("draftModeMaster", trackingLog.getClaim().isDraftMode());

		super.getResponse().addData(dataset);
	}

}
