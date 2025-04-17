
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
			boolean emptyResolution;
			emptyResolution = trackingLog.getResolution() != null;
			super.state(context, emptyResolution, "resolution", "acme.validation.trackingLog.mandatory-Resolution.message");

			boolean onlyAcceptedOrRejected;
			onlyAcceptedOrRejected = trackingLog.getStatus().equals(ClaimStatus.ACCEPTED) || trackingLog.getStatus().equals(ClaimStatus.REJECTED);
			super.state(context, onlyAcceptedOrRejected, "status", "acme.validation.trackingLog.resolution-Not-Taken.message");

		} else if (trackingLog.getPercentage() < 100.00) {
			boolean onlyPending;
			onlyPending = trackingLog.getStatus().equals(ClaimStatus.PENDING);
			super.state(context, onlyPending, "status", "acme.validation.trackingLog.resolution-Taken.message");

			/*
			 * boolean ascending;
			 * TrackingLog actualMax = this.repository.findTrackingLogsOrderedByPercentage(trackingLog.getClaim().getId()).get(0);
			 * if (trackingLog.getPercentage() == 100.00)
			 * ascending = trackingLog.getPercentage() == actualMax.getPercentage();
			 * else
			 * ascending = trackingLog.getPercentage() > actualMax.getPercentage();
			 * super.state(context, ascending, "percentage", "acme.validation.trackingLog.ascending-percentage.message");
			 */
		}
		result = !super.hasErrors(context);
		return result;
	}

}
