
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
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
		Leg leg;

		masterId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(masterId);
		agent = claim == null ? null : claim.getAgent();
		status = claim != null && claim.isDraftMode() && super.getRequest().getPrincipal().hasRealm(agent);

		String cType;
		if (super.getRequest().getMethod().equals("GET"))
			status = true;
		else {
			cType = super.getRequest().getData("type", String.class);
			status = false;

			for (ClaimType ct : ClaimType.values())
				if (cType.toLowerCase().trim().equals(ct.toString().toLowerCase().trim()) || cType.equals("0")) {
					status = true;
					break;
				}
		}

		if (status && super.getRequest().getMethod().equals("POST")) {
			Integer legId = super.getRequest().getData("leg", Integer.class);
			if (legId != null) {
				if (legId != 0) {
					leg = this.repository.findLegById(legId);
					status = leg != null && !leg.isDraftMode() && leg.getScheduledArrival().before(MomentHelper.getCurrentMoment());
					super.getResponse().setAuthorised(status);
				} else
					status = true;
				super.getResponse().setAuthorised(status);
			} else
				status = false;
			super.getResponse().setAuthorised(status);
		} else
			super.getResponse().setAuthorised(false);
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

		super.bindObject(claim, "email", "description", "type");
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

		legs = this.repository.findAllLegs(MomentHelper.getCurrentMoment());
		choicesLegs = SelectChoices.from(legs, "flightNumber", claim.getLeg());

		dataset = super.unbindObject(claim, "moment", "email", "description", "type", "draftMode");
		dataset.put("status", choicesStatus.getSelected().getKey());
		dataset.put("types", choicesType);
		dataset.put("leg", choicesLegs.getSelected().getKey());
		dataset.put("legs", choicesLegs);

		super.getResponse().addData(dataset);
	}

}
