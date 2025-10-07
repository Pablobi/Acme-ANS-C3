
package acme.features.agent.claim;

import java.util.Collection;
import java.util.Date;

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
public class AgentClaimCreateService extends AbstractGuiService<Agent, Claim> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AgentClaimRepository repository;

	// ConstraintValidator interface ------------------------------------------


	@Override
	public void authorise() {
		Integer legId;
		Integer id;
		Leg leg;
		boolean status;

		if (super.getRequest().getMethod().equals("POST")) {
			legId = super.getRequest().getData("leg", Integer.class);
			id = super.getRequest().getData("id", Integer.class);
			if (id != 0)
				super.getResponse().setAuthorised(false);
			else if (legId != null) {
				if (legId != 0) {
					leg = this.repository.findLegById(legId);
					status = leg != null && !leg.getFlight().isDraftMode() && !leg.isDraftMode() && leg.getScheduledArrival().before(MomentHelper.getCurrentMoment());
					super.getResponse().setAuthorised(status);
				} else
					super.getResponse().setAuthorised(true);
			} else
				super.getResponse().setAuthorised(false);
		} else
			super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Claim claim;
		Agent agent;
		Date currentDate;

		agent = (Agent) super.getRequest().getPrincipal().getActiveRealm();
		currentDate = MomentHelper.getCurrentMoment();

		claim = new Claim();
		claim.setDraftMode(true);
		claim.setMoment(currentDate);
		claim.setAgent(agent);

		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		int legId;
		Leg leg;

		legId = super.getRequest().getData("leg", int.class);
		leg = this.repository.findLegById(legId);

		claim.setLeg(leg);
		super.bindObject(claim, "email", "description", "type");

	}

	@Override
	public void validate(final Claim claim) {
		;
	}

	@Override
	public void perform(final Claim claim) {

		claim.setMoment(MomentHelper.getCurrentMoment());
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
