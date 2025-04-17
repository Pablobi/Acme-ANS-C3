
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.activityLog.ActivityLog;

@Validator
public class ActivityLogValidator extends AbstractValidator<ValidActivityLog, ActivityLog> {

	// Internal state ---------------------------------------------------------------------

	// ConstraintValidator interface ------------------------------------------------------

	@Override
	protected void initialise(final ValidActivityLog annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final ActivityLog activityLog, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		if (activityLog == null || activityLog.getRegistrationMoment() == null || activityLog.getAssignment() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else if (!MomentHelper.isAfter(activityLog.getRegistrationMoment(), activityLog.getAssignment().getLeg().getScheduledArrival()))
			super.state(context, false, "registrationMoment", "validation.activityLog.legHasNotEnded");
		result = !super.hasErrors(context);

		return result;
	}
}
