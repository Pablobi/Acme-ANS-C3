
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.technicians.Technician;
import acme.realms.technicians.TechnicianRepository;

@Validator
public class TechnicianValidator extends AbstractValidator<ValidTechnician, Technician> {

	@Autowired
	private TechnicianRepository repository;


	@Override
	protected void initialise(final ValidTechnician annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Technician technician, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		String licenseNumber = technician.getLicenseNumber();
		String phoneNumber = technician.getPhoneNumber();
		Technician sameLicenseNumber = this.repository.findTechnicianSameLicenseNumber(licenseNumber);

		if (!licenseNumber.matches("^[A-Z]{2,3}\\d{6}$") && licenseNumber != "")
			super.state(context, false, "licenseNumber", "validation.technicians.licenseNumber");
		else if (sameLicenseNumber != null && !sameLicenseNumber.equals(technician))
			super.state(context, false, "licenseNumber", "acme.validation.technician.licenseNumberNotUnique");

		if (!phoneNumber.matches("^\\+?\\d{6,15}$") && phoneNumber != "")
			super.state(context, false, "phoneNumber", "validation.technicians.phoneNumber");

		result = !super.hasErrors(context);

		return result;
	}

}
