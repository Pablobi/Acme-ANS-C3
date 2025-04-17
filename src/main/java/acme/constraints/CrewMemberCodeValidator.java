
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.flightCrewMembers.FlightCrewMember;
import acme.realms.flightCrewMembers.FlightCrewMemberRepository;

@Validator
public class CrewMemberCodeValidator extends AbstractValidator<ValidCrewMemberCode, FlightCrewMember> {

	@Autowired
	private FlightCrewMemberRepository repository;


	@Override
	protected void initialise(final ValidCrewMemberCode annotation) {
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
			String code = crewMember.getEmployeeCode();
			FlightCrewMember sameCode = this.repository.findMemberSameCode(code);

			if (code == null)
				super.state(context, false, "code", "javax.validation.constraints.NotNull.message");
			else if (!code.startsWith(initials))
				super.state(context, false, "code", "validation.flightCrewMember.codeFormat");
			else if (sameCode != null && !sameCode.equals(crewMember))
				super.state(context, false, "code", "validation.flightCrewMember.codeNotUnique");

		}
		result = !super.hasErrors(context);
		return result;
	}

	private String getInitials(final FlightCrewMember crewMember) {

		String initials = "";
		String name = crewMember.getUserAccount().getIdentity().getName().trim();
		String surname = crewMember.getUserAccount().getIdentity().getSurname().trim();

		if (name != null && surname != null)
			initials = name.substring(0, 1) + surname.substring(0, 1);

		return initials;
	}

}
