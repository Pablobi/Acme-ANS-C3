
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.agents.Agent;
import acme.realms.agents.AgentRepository;

@Validator
public class AgentValidator extends AbstractValidator<ValidAgent, Agent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	protected void initialise(final ValidAgent annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Agent agent, final ConstraintValidatorContext context) {

		assert context != null;
		boolean result;

		if (agent == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			{
				boolean uniqueAgent;
				Agent existingAgent;

				existingAgent = this.repository.findAgentByCode(agent.getEmployeeCode());
				uniqueAgent = existingAgent == null || existingAgent.equals(agent);

				super.state(context, uniqueAgent, "ticker", "acme.validation.agent.codeNotUnique");

			}

			String initials = this.getInitials(agent);
			String code = agent.getEmployeeCode();
			boolean initialsLikeName;

			initialsLikeName = code != null && code.startsWith(initials);

			super.state(context, initialsLikeName, "employeeCode", "validation.agent.codeInitials");
		}
		result = !super.hasErrors(context);
		return result;
	}

	private String getInitials(final Agent agent) {

		String initials = "";
		String name = agent.getUserAccount().getIdentity().getName().trim();
		String surname = agent.getUserAccount().getIdentity().getSurname().trim();

		if (name != null && surname != null)
			initials = name.substring(0, 1).toUpperCase() + surname.substring(0, 1).toUpperCase();

		return initials;
	}
}
