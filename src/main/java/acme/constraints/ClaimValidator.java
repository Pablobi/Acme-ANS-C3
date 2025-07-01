
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.claims.Claim;

@Validator
public class ClaimValidator extends AbstractValidator<ValidClaim, Claim> {

	// Internal state ---------------------------------------------------------

	// ConstraintValidator interface ------------------------------------------

	@Override
	protected void initialise(final ValidClaim annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Claim claim, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		if (claim == null)
			super.state(context, true, "*", "javax.validation.constraints.NotNull.message");
		else if (claim.getLeg() != null)
			if (claim.getLeg().getFlight().isDraftMode() == true) {
				super.state(context, false, "leg", "validation.claim.leg.not-published-flight-message");
				if (MomentHelper.isBefore(claim.getMoment(), claim.getLeg().getScheduledArrival()))
					super.state(context, false, "leg", "validation.claim.leg.arrival-message");
			}
		result = !super.hasErrors(context);
		return result;
	}

}
