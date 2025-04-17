/*
 * FlightCrewMemberActivityLogRepository.java
 *
 * Copyright (C) 2012-2025 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.flightCrewMember.flightAssignments;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.Duties;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flights.Leg;
import acme.realms.flightCrewMembers.FlightCrewMember;

@Repository
public interface FlightCrewMemberFlightAssignmentRepository extends AbstractRepository {

	@Query("select a from FlightAssignment a where a.id = :id")
	FlightAssignment findFlightAssignmentById(int id);

	@Query("select a from FlightAssignment a where a.flightCrewMember = :fcm")
	Collection<FlightAssignment> findFlightAssignmentsByCrewMember(FlightCrewMember fcm);

	@Query("select a from FlightAssignment a where a.leg.scheduledArrival<:now and a.flightCrewMember.id = :crewMemberId")
	Collection<FlightAssignment> findCompletedAssignmentsByCrewMemberId(Date now, int crewMemberId);

	@Query("select a from FlightAssignment a where a.leg.scheduledArrival>:now and a.flightCrewMember.id = :crewMemberId")
	Collection<FlightAssignment> findUncompletedAssignmentsByCrewMemberId(Date now, int crewMemberId);

	@Query("select a from FlightAssignment a")
	Collection<FlightAssignment> findAllAssignments();

	@Query("select f from FlightCrewMember f where f.id = :crewMemberId")
	FlightCrewMember findCrewMemberById(int crewMemberId);

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select f.duty from FlightAssignment f where f.leg.id = :legId")
	Collection<Duties> findPresentRolesInLeg(Leg legId);

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	//1 que no se trate de la misma assignment 2 que pertenezca a ese crew member 3 que no se tenga en cuenta la misma leg que se le pasa 4 que no se solapen los horarios
	@Query("select f from FlightAssignment f where f.id != :assignmentId and f.flightCrewMember.id = :crewMemberId and f.leg.id != :legId and f.leg.scheduledDeparture < :scheduledArrival and f.leg.scheduledArrival > :scheduledDeparture")
	Collection<FlightAssignment> findSimultaneousLegs(int assignmentId, int legId, Date scheduledDeparture, Date scheduledArrival, int crewMemberId);

}
