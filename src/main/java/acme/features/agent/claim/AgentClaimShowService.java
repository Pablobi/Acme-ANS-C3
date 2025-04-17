
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
import acme.entities.claims.ClaimType;
import acme.entities.flights.Leg;
import acme.realms.agents.Agent;

@GuiService
public class AgentClaimShowService extends AbstractGuiService<Agent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		Agent agent;

		masterId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(masterId);
		agent = claim == null ? null : claim.getAgent();
		status = super.getRequest().getPrincipal().hasRealm(agent) || claim != null && !claim.isDraftMode();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int id;

		id = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(id);

		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		SelectChoices choicesType;
		SelectChoices choicesStatus;
		SelectChoices choicesLegs;
		Collection<Leg> legs;

		Dataset dataset;

		choicesStatus = SelectChoices.from(ClaimStatus.class, claim.getStatus());
		choicesType = SelectChoices.from(ClaimType.class, claim.getType());

		legs = this.repository.findAllLegs();
		choicesLegs = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "moment", "email", "description", "type", "draftMode");
		dataset.put("status", choicesStatus.getSelected().getKey());
		dataset.put("types", choicesType);
		dataset.put("leg", choicesLegs.getSelected().getKey());
		dataset.put("legs", choicesLegs);

		super.getResponse().addData(dataset);
	}

}
