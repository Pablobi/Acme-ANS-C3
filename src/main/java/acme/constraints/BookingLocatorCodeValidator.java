
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.bookings.Booking;
import acme.entities.bookings.BookingRepository;

@Validator
public class BookingLocatorCodeValidator extends AbstractValidator<ValidBookingLocatorCode, Booking> {

	@Autowired
	private BookingRepository repository;


	@Override
	protected void initialise(final ValidBookingLocatorCode constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {
		assert context != null;
		boolean result;
		Booking notUnique = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
		if (notUnique != null && !notUnique.equals(booking))
			super.state(context, false, "locatorCode", "validation.booking.uniqueIdentifier");
		result = !super.hasErrors(context);
		return result;
	}

}
