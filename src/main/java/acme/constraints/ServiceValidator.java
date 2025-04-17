
package acme.constraints;

import java.util.Objects;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.service.Service;
import acme.entities.service.ServiceRepository;

@Validator
public class ServiceValidator extends AbstractValidator<ValidService, Service> {

	@Autowired
	private ServiceRepository repository;


	@Override
	protected void initialise(final ValidService annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Service service, final ConstraintValidatorContext context) {

		assert context != null;

		if (service == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");

		String promotionCode = service.getPromotionCode();
		Service samePromotionCode = this.repository.findServiceSamePromotionCode(promotionCode);
		boolean result;

		if (promotionCode.length() != 0) {
			String lastTwoDigits = promotionCode.substring(5);
			String currentYear = String.valueOf(MomentHelper.getCurrentMoment());
			currentYear = currentYear.substring(currentYear.length() - 2);
			boolean promotionCodeHasCurrentYear = Objects.equals(lastTwoDigits, currentYear);
			if (promotionCodeHasCurrentYear == false)
				super.state(context, false, "promotionCode", "validation.service.promotionCode");
		} else if (samePromotionCode != null && !samePromotionCode.equals(service))
			super.state(context, false, "promotionCode", "validation.service.promotionCode3");
		else
			super.state(context, true, "promotionCode", "validation.service.promotionCode");

		result = !super.hasErrors(context);

		return result;

	}

}
