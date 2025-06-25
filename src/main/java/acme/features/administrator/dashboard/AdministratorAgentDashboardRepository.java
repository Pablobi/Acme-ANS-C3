
package acme.features.administrator.dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;

@Repository
public interface AdministratorAgentDashboardRepository extends AbstractRepository {

	@Query("select avg(select count(t) from TrackingLog t where t.claim.id = c.id) from Claim c")
	Double averageLogs();

	@Query("select avg(select count(t) from TrackingLog t where t.claim.id = c.id) from Claim c")
	Double deviationLogs();

	@Query("select min(select count(t) from TrackingLog t where t.claim.id = c.id) from Claim c")
	Double minLogs();

	@Query("select max(select count(t) from TrackingLog t where t.claim.id = c.id) from Claim c")
	Double maxLogs();

	@Query("select c from Claim c")
	Collection<Claim> findAllClaims();

}
