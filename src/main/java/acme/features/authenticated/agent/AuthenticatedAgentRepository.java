
package acme.features.authenticated.agent;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airlines.Airline;
import acme.realms.agents.Agent;

@Repository
public interface AuthenticatedAgentRepository extends AbstractRepository {

	@Query("select a from Agent a where a.userAccount.id = :id")
	Agent findAgentByUserAccountId(int id);

	@Query("select ua from UserAccount ua where ua.id = :id")
	UserAccount findOneUserAccountById(int id);

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

}
