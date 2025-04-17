
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.technicians.Technician;
import acme.realms.technicians.TechnicianRepository;

@Validator
public class TechnicianLicenseNumberValidator extends AbstractValidator<ValidTechnicianLicenseNumber, Technician> {

	@Autowired
	private TechnicianRepository repository;


	@Override
	protected void initialise(final ValidTechnicianLicenseNumber annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Technician technician, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (technician.getUserAccount() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String licenseNumber = technician.getLicenseNumber();
			Technician sameLicenseNumber = this.repository.findTechnicianSameLicenseNumber(licenseNumber);

			if (licenseNumber == null)
				super.state(context, false, "code", "javax.validation.constraints.NotNull.message");
			else if (sameLicenseNumber != null && !sameLicenseNumber.equals(technician))
				super.state(context, false, "code", "acme.validation.technician.licenseNumberNotUnique");

		}
		result = !super.hasErrors(context);
		return result;
	}

}
