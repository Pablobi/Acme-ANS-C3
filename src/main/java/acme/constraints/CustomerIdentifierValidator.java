
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.customers.Customer;
import acme.realms.customers.CustomerRepository;

@Validator
public class CustomerIdentifierValidator extends AbstractValidator<ValidCustomerIdentifier, Customer> {

	@Autowired
	private CustomerRepository repository;


	@Override
	protected void initialise(final ValidCustomerIdentifier constraintAnnotation) {
		assert constraintAnnotation != null;
	}

	@Override
	public boolean isValid(final Customer customer, final ConstraintValidatorContext context) {

		assert context != null;
		String id = "identifier";
		boolean result;

		if (customer.getUserAccount() == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			String initials = this.getInitials(customer);
			String identifier = customer.getIdentifier();
			Customer notUnique = this.repository.findAllCustomerIdentifiers(customer.getIdentifier());

			if (notUnique != null && !notUnique.equals(customer))
				super.state(context, false, id, "validation.customer.uniqueIdentifier.message");
			else if (identifier == null)
				super.state(context, false, id, "javax.validation.constraints.NotNull.message");
			else if (!identifier.startsWith(initials))
				super.state(context, false, id, "acme.constraints.ValidCustomerIdentifier.message");

		}
		result = !super.hasErrors(context);
		return result;
	}

	private String getInitials(final Customer customer) {

		String initials = "";
		String name = customer.getUserAccount().getIdentity().getName().trim();
		String surname = customer.getUserAccount().getIdentity().getSurname().trim();

		if (name != null && surname != null)
			initials = name.substring(0, 1) + surname.substring(0, 1);

		return initials;
	}

}
