
package acme.features.administrator.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claims.Claim;
import acme.entities.flights.Leg;

@Repository
public interface AdministratorClaimRepository extends AbstractRepository {

	@Query("select c from Claim c where c.id = :id")
	Claim findAnnouncementById(int id);

	@Query("select c from Claim c where c.draftMode = true")
	Collection<Claim> findAllPublishedClaims();

	@Query("SELECT l FROM Leg l")
	Collection<Leg> findAllLegs();

}
