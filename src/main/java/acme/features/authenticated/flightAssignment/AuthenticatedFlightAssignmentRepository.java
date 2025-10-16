
package acme.features.authenticated.flightAssignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface AuthenticatedFlightAssignmentRepository extends AbstractRepository {

	@Query("select f from FlightAssignment f where f.id = :id")
	FlightAssignment findFlightAssignmentById(Integer id);

	@Query("select f from FlightAssignment f where f.draftMode = false")
	Collection<FlightAssignment> findAllPublishedFlightAssignments();

}
