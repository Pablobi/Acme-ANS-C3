
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airlines.Airline;
import acme.entities.airlines.AirlineRepository;

@Validator
public class ValidatorAirline extends AbstractValidator<ValidAirline, Airline> {

	@Autowired
	private AirlineRepository repository;


	@Override
	protected void initialise(final ValidAirline constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Airline airline, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;
		Airline notUnique = this.repository.findAirlineByCode(airline.getIataCode());
		String iataCode = airline.getIataCode();
		String phoneNumber = airline.getPhoneNumber();

		if (!iataCode.matches("^[A-Z]{3}$") && iataCode != "")
			super.state(context, false, "iataCode", "validation.airline.code");
		else if (notUnique != null && !notUnique.equals(airline))
			super.state(context, false, "iataCode", "validation.customer.uniqueIdentifier");

		if (!phoneNumber.matches("^\\+?\\d{6,15}$") && phoneNumber != "")
			super.state(context, false, "phoneNumber", "validation.airline.phoneNumber");

		result = !super.hasErrors(context);

		return result;
	}

}
