
package acme.realms.customers;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface CustomerRepository extends AbstractRepository {

	@Query("select a from Customer a where a.identifier = :id")
	Customer findAllCustomerIdentifiers(String id);
}
