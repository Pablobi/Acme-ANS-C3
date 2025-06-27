
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.entities.airlines.Airline;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface AuthenticatedFlightCrewMemberRepository extends AbstractRepository {

	@Query("select u from UserAccount u where u.id = :id")
	UserAccount findUserById(int id);

	@Query("select f from FlightCrewMember f where f.userAccount.id = :id")
	FlightCrewMember findFlightCrewMemberByUserId(int id);

	@Query("select f from FlightCrewMember f where f.employeeCode = :employeeCode")
	FlightCrewMember findFlightCrewMemberByEmployeeCode(String employeeCode);

	@Query("select a from Airline a where a.id = :airlineId")
	Airline findAirlineById(int airlineId);

	@Query("select a from Airline a")
	Collection<Airline> findAllAirlines();

}
