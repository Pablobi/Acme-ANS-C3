
package acme.constraints;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.aircrafts.Aircraft;
import acme.entities.aircrafts.AircraftRepository;

@Validator
public class RegistrationNumberValidatorAircraft extends AbstractValidator<ValidRegistrationNumberAircraft, Aircraft> {

	@Autowired
	AircraftRepository repository;


	@Override
	protected void initialise(final ValidRegistrationNumberAircraft constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Aircraft aircraft, final javax.validation.ConstraintValidatorContext context) {
		assert context != null;
		boolean result;
		Aircraft notUnique = this.repository.findAircraftByRegistrationNumber(aircraft.getRegistrationNumber());
		if (notUnique != null && !notUnique.equals(aircraft))
			super.state(context, false, "registrationNumber", "validation.customer.uniqueIdentifier.message");
		result = !super.hasErrors(context);
		return result;
	}

}
