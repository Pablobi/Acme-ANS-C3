
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
public class AgentClaimUpdateService extends AbstractGuiService<Agent, Claim> {

	@Autowired
	private AgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int masterId;
		Claim claim;
		Agent agent;

		masterId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(masterId);
		agent = claim == null ? null : claim.getAgent();
		status = claim != null && super.getRequest().getPrincipal().hasRealm(agent);

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
	public void bind(final Claim claim) {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		super.bindObject(claim, "moment", "email", "description", "type", "status");
		claim.setLeg(leg);
	}

	@Override
	public void validate(final Claim claim) {
		;
	}

	@Override
	public void perform(final Claim claim) {
		this.repository.save(claim);
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
