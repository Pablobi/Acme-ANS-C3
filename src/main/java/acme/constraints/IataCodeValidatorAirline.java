
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airlines.Airline;
import acme.entities.airlines.AirlineRepository;

@Validator
public class IataCodeValidatorAirline extends AbstractValidator<ValidIATACodeAirline, Airline> {

	@Autowired
	private AirlineRepository repository;


	@Override
	protected void initialise(final ValidIATACodeAirline constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;
		Airline notUnique = this.repository.findAirlineByCode(airline.getCode());
		if (notUnique != null && !notUnique.equals(airline))
			super.state(context, false, "locatorCode", "validation.customer.uniqueIdentifier.message");
		result = !super.hasErrors(context);
		return result;
	}

}
