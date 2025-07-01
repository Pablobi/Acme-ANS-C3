
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.claims.ClaimStatus;
import acme.entities.claims.TrackingLog;
import acme.entities.claims.TrackingLogRepository;

@Validator
public class TrackingLogValidator extends AbstractValidator<ValidTrackingLog, TrackingLog> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TrackingLogRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidTrackingLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final TrackingLog trackingLog, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		if (trackingLog == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		else if (trackingLog.getPercentage() == 100.00) {

			if (trackingLog.getStatus() == null)
				super.state(context, trackingLog.getStatus() != null, "status", "agent.claim.form.error.status-not-null");
			else {
				boolean onlyAcceptedOrRejected;
				onlyAcceptedOrRejected = trackingLog.getStatus().equals(ClaimStatus.ACCEPTED) || trackingLog.getStatus().equals(ClaimStatus.REJECTED);
				super.state(context, onlyAcceptedOrRejected, "status", "validation.trackingLog.resolution-Not-Taken.message");
			}
			boolean emptyResolution;
			emptyResolution = !trackingLog.getResolution().isBlank();
			super.state(context, emptyResolution, "resolution", "validation.trackingLog.mandatory-Resolution.message");

		} else if (trackingLog.getPercentage() < 100.00) {
			if (trackingLog.getStatus() == null)
				super.state(context, trackingLog.getStatus() != null, "status", "agent.claim.form.error.status-not-null");
			else {
				boolean onlyPending;
				onlyPending = trackingLog.getStatus().equals(ClaimStatus.PENDING);
				super.state(context, onlyPending, "status", "validation.trackingLog.resolution-Taken.message");
			}

			if (trackingLog.isDraftMode() == false) {
				boolean ascending;

				if (this.repository.findPublishedTrackingLogsByMasterId(trackingLog.getClaim().getId()).size() > 1) {

					TrackingLog actualMax = this.repository.findPublishedTrackingLogsOrderedByPercentage(trackingLog.getClaim().getId()).get(0);
					if (trackingLog.getPercentage() == 100.00)
						ascending = trackingLog.getPercentage() >= actualMax.getPercentage();
					else
						ascending = trackingLog.getPercentage() > actualMax.getPercentage();
					System.out.print(actualMax.getPercentage());
				} else
					ascending = true;
				super.state(context, ascending, "percentage", "validation.trackingLog.ascending-percentage.message");
			}
		}

		result = !super.hasErrors(context);
		return result;
	}

}
