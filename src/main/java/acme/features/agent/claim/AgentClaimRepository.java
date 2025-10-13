
package acme.features.agent.claim;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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

	@Query("SELECT c FROM Claim c WHERE c.agent.id = :agentId")
	Collection<Claim> findAllClaimsByAgentId(int agentId);

	@Query("select c.leg from Claim c where c.id = :id")
	Leg findLegByClaimId(int id);

	@Query("SELECT l FROM Leg l WHERE l.scheduledArrival < :now AND l.draftMode = false")
	Collection<Leg> findAllLegs(Date now);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select t from TrackingLog t where t.claim.id = :claimId")
	Collection<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.percentage = 100.00")
	List<TrackingLog> findTrackingLogsByClaimIdCompleted(int claimId);

}
