
package acme.entities.flights;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface LegRepository extends AbstractRepository {

	@Query("select l from Leg l where l.flightNumber = :fligthNumber")
	Leg findLegByCode(String fligthNumber);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId and l.id != :legId")
	List<Leg> findByFlightId(int flightId, int legId);

	@Query("SELECT COUNT(l) from Leg l WHERE l.flight.id = :flightId")
	Integer numberOfLavoyers(int flightId);

	@Query(value = "SELECT l.scheduled_departure FROM Leg l WHERE l.flight_id = :flightId ORDER BY l.scheduled_departure ASC LIMIT 1", nativeQuery = true)
	Optional<Date> findScheduledDeparture(int flightId);

	@Query(value = "SELECT l.scheduled_arrival FROM Leg l WHERE l.flight_id = :flightId ORDER BY l.scheduled_arrival DESC LIMIT 1", nativeQuery = true)
	Optional<Date> findScheduledArrival(int flightId);

	@Query(value = "SELECT a.city FROM Leg l JOIN Airport a ON l.departure_airport_id = a.id WHERE l.flight_id = :flightId ORDER BY l.scheduled_departure ASC LIMIT 1", nativeQuery = true)
	Optional<String> findOriginCity(int flightId);

	@Query(value = "SELECT a.city FROM Leg l JOIN Airport a ON l.arrival_airport_id = a.id WHERE l.flight_id = :flightId ORDER BY l.scheduled_arrival DESC LIMIT 1", nativeQuery = true)
	Optional<String> findDestinationCity(int flightId);

}
