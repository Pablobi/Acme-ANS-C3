
package acme.features.flightCrewMember.dashboard;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flights.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface FlightCrewMemberDashboardRepository extends AbstractRepository {

	@Query("select fa.leg.arrivalAirport.city from FlightAssignment fa where fa.flightCrewMember.id = :crewMemberId and fa.draftMode = false order by fa.leg.scheduledDeparture desc")
	List<String> findLastFiveDestinations(int crewMemberId);

	@Query("select count(distinct al.assignment.leg) from ActivityLog al where al.draftMode = false and al.severityLevel >= :a and al.severityLevel <= :b")
	Integer countLegsBetweenSeverityLevels(int a, int b);

	@Query("select fa.flightCrewMember from FlightAssignment fa where fa.draftMode = false and fa.leg.id = :legId and fa.flightCrewMember.id != :crewMemberId")
	List<FlightCrewMember> findCrewMembersFromLastLeg(int legId, int crewMemberId);

	@Query("select fa.leg from FlightAssignment fa where fa.flightCrewMember.id = :crewMemberId and fa.draftMode = false and fa.leg.scheduledArrival < :now order by fa.leg.scheduledArrival desc")
	List<Leg> findLastLegs(int crewMemberId, Date now);

	@Query("select fa.leg.flightNumber from FlightAssignment fa where fa.flightCrewMember.id = :crewMemberId and fa.currentStatus = :status")
	List<String> findFlightAssignmentsByStatus(int crewMemberId, AssignmentStatus status);

	@Query("select count(fa) * 1.0 / 10.0 from FlightAssignment fa where fa.flightCrewMember.id = :flightCrewMemberId and fa.lastUpdate >= :hace10Meses and fa.draftMode = false")
	Double findAverageAssignmentsLast10Months(int flightCrewMemberId, Date hace10Meses);

	@Query("select count(fa) from FlightAssignment fa where fa.flightCrewMember.id = :flightCrewMemberId and fa.lastUpdate >= :hace10Meses and fa.draftMode = false group by function('YEAR', fa.lastUpdate), function('MONTH', fa.lastUpdate)")
	List<Integer> findNumberFlightAssignmentsLast10Months(int flightCrewMemberId, Date hace10Meses);

}
