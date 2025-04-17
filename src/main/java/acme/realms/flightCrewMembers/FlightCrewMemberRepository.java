
package acme.realms.flightCrewMembers;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightCrewMemberRepository extends AbstractRepository {

	@Query("SELECT f FROM FlightCrewMember f WHERE f.employeeCode= :employeeCode")
	FlightCrewMember findMemberSameCode(String employeeCode);

}
