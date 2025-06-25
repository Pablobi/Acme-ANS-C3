
package acme.features.authenticated.agent;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.realms.agents.Agent;

@GuiService
public class AuthenticatedAgentCreateService extends AbstractGuiService<Authenticated, Agent> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AuthenticatedAgentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = !super.getRequest().getPrincipal().hasRealmOfType(Agent.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Agent agent;
		int userAccountId;
		UserAccount userAccount;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		userAccount = this.repository.findOneUserAccountById(userAccountId);

		agent = new Agent();
		agent.setUserAccount(userAccount);

		super.getBuffer().addData(agent);
	}

	@Override
	public void bind(final Agent agent) {
		super.bindObject(agent, "employeeCode", "languages", "moment", "bio", "salary", "photo", "airline");
	}

	@Override
	public void validate(final Agent agent) {
		;
	}

	@Override
	public void perform(final Agent agent) {
		this.repository.save(agent);
	}

	@Override
	public void unbind(final Agent agent) {
		Dataset dataset;
		SelectChoices choices;
		Collection<Airline> airlines;

		airlines = this.repository.findAllAirlines();
		choices = SelectChoices.from(airlines, "name", agent.getAirline());

		dataset = super.unbindObject(agent, "employeeCode", "languages", "moment", "bio", "salary", "photo");
		dataset.put("airline", choices.getSelected().getKey());
		dataset.put("airlines", choices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
