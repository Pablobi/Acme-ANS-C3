
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.airports.Airport;
import acme.entities.airports.AirportRepository;

@Validator
public class IataCodeValidatorAirport extends AbstractValidator<ValidIATACodeAirport, Airport> {

	@Autowired
	private AirportRepository repository;


	@Override
	protected void initialise(final ValidIATACodeAirport constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Airport airport, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;
		Airport notUnique = this.repository.findAirportByIataCode(airport.getIataCode());
		if (notUnique != null && !notUnique.equals(airport))
			super.state(context, false, "iataCode", "validation.customer.uniqueIdentifier");
		result = !super.hasErrors(context);
		return result;
	}

}
