
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.flightCrewMembers.FlightCrewMember;
import acme.realms.flightCrewMembers.FlightCrewMemberRepository;

@Validator
public class FlightCrewMemberValidator extends AbstractValidator<ValidFlightCrewMember, FlightCrewMember> {

	@Autowired
	private FlightCrewMemberRepository repository;


	@Override
	protected void initialise(final ValidFlightCrewMember annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final FlightCrewMember crewMember, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (crewMember.getUserAccount() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = this.getInitials(crewMember);
			String phoneNumber = crewMember.getPhoneNumber();
			String code = crewMember.getEmployeeCode();
			FlightCrewMember sameCode = this.repository.findMemberSameCode(code);

			if (phoneNumber == null)
				super.state(context, false, "phoneNumber", "javax.validation.constraints.NotNull.message");
			else if (!phoneNumber.matches("^\\+?\\d{6,15}$"))
				super.state(context, false, "phoneNumber", "validation.flightCrewMember.phoneNumberPattern");

			if (code == null)
				super.state(context, false, "employeeCode", "javax.validation.constraints.NotNull.message");
			else if (!code.startsWith(initials))
				super.state(context, false, "employeeCode", "validation.flightCrewMember.codeInitials");
			else if (sameCode != null && !sameCode.equals(crewMember))
				super.state(context, false, "employeeCode", "validation.flightCrewMember.codeNotUnique");
			else if (!code.matches("^[A-Z]{2,3}\\d{6}$"))
				super.state(context, false, "employeeCode", "validation.flightCrewMember.codePattern");

		}
		result = !super.hasErrors(context);
		return result;
	}

	private String getInitials(final FlightCrewMember crewMember) {

		String initials = "";
		String name = crewMember.getUserAccount().getIdentity().getName().trim();
		String surname = crewMember.getUserAccount().getIdentity().getSurname().trim();

		if (name != null && !name.isBlank())
			initials = initials + name.trim().charAt(0);

		if (surname != null && !surname.isBlank()) {
			// Divide el apellido en palabras y toma la primera letra de cada una
			String[] surnameParts = surname.trim().split("\\s+");
			for (String part : surnameParts)
				if (!part.isEmpty())
					initials = initials + part.charAt(0);
		}

		initials = initials.toString().toUpperCase();

		return initials;
	}

}
