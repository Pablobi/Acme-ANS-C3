
package acme.features.agent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.claims.TrackingLog;
import acme.entities.flights.Leg;

@Repository
public interface AgentClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	//@Query("SELECT c FROM Claim c WHERE c.agent.id = :agentId AND c.status IN ('ACCEPTED', 'REJECTED')")
	//Collection<Claim> findCompletedClaimsByAgentId(int agentId);

	@Query("SELECT c FROM Claim c WHERE c.agent.id = :agentId")
	Collection<Claim> findAllClaimsByAgentId(int agentId);

	@Query("select c.leg from Claim c where c.id = :id")
	Leg findLegByClaimId(int id);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);
}
