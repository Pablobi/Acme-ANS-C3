
package acme.features.agent.claim;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.Claim;
import acme.entities.claims.ClaimStatus;
import acme.realms.agents.Agent;

@GuiService
public class AgentClaimListPendingService extends AbstractGuiService<Agent, Claim> {

	@Autowired
	private AgentClaimRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int agentId;

		agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		claims = this.repository.findAllClaimsByAgentId(agentId).stream().filter(c -> c.getStatus() == ClaimStatus.PENDING).collect(Collectors.toList());
		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;

		dataset = super.unbindObject(claim, "type", "moment", "email");
		super.addPayload(dataset, claim, //
			"description", "agent.employeeCode", //
			"agent.identity.fullName", "agent.airline", "leg.id");

		super.getResponse().addData(dataset);
	}
}
