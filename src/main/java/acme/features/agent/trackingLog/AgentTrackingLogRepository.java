
package acme.features.agent.trackingLog;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.claims.TrackingLog;

@Repository
public interface AgentTrackingLogRepository extends AbstractRepository {

	@Query("select t from TrackingLog t where t.id = :id")
	TrackingLog findTrackingLogById(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :masterId")
	Collection<TrackingLog> findTrackingLogsByMasterId(int masterId);

	@Query("select c from Claim c where c.id = :id")
	Claim findClaimById(int id);

	@Query("select t.claim from TrackingLog t where t.id = :id")
	Claim findClaimByTrackingLogId(int id);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.percentage = 100.00")
	List<TrackingLog> findTrackingLogsByClaimIdCompleted(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.percentage = 100.00 AND t.draftMode = false")
	List<TrackingLog> findTrackingLogsByClaimIdWith100Percentage(int claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId ORDER BY t.percentage DESC")
	List<TrackingLog> findTrackingLogsOrderedByPercentage(int claimId);

}
