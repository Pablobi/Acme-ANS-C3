
package acme.entities.flights;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightRepository extends AbstractRepository {

	@Query("SELECT MIN(l.scheduledDeparture) FROM Leg l WHERE l.flight = :flight")
	Date findScheduledDeparture(Flight flight);

	@Query("SELECT MAX(l.scheduledArrival) FROM Leg l WHERE l.flight = :flight")
	Date findScheduledArrival(Flight flight);

	@Query("SELECT l.departureAirport FROM Leg l WHERE l.flight = :flight ORDER BY l.scheduledDeparture ASC")
	String findOriginCity(Flight flight);

	@Query("SELECT l.arrivalAirport FROM Leg l WHERE l.flight = :flight ORDER BY l.scheduledArrival DESC")
	String findDestinationCity(Flight flight);

	@Query("SELECT COUNT(l) FROM Leg l WHERE l.flight = :flight")
	Integer countLegs(Flight flight);

}
