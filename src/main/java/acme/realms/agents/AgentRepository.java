
package acme.realms.agents;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AgentRepository extends AbstractRepository {

	@Query("select a from Agent a where a.employeeCode = :employeeCode")
	Agent findAgentByCode(String employeeCode);

}
