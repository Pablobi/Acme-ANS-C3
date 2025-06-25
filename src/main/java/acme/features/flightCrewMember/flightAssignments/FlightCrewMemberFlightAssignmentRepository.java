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

	@Query("select a from FlightAssignment a where a.flightCrewMember.id = :crewMemberId")
	Collection<FlightAssignment> findFlightAssignmentsByCrewMemberId(int crewMemberId);

	@Query("select a from FlightAssignment a where a.leg.scheduledArrival<:now and a.flightCrewMember.id = :crewMemberId")
	Collection<FlightAssignment> findCompletedAssignmentsByCrewMemberId(Date now, int crewMemberId);

	@Query("select a from FlightAssignment a where a.leg.scheduledArrival>:now and a.flightCrewMember.id = :crewMemberId")
	Collection<FlightAssignment> findUncompletedAssignmentsByCrewMemberId(Date now, int crewMemberId);

	@Query("select a from FlightAssignment a")
	Collection<FlightAssignment> findAllAssignments();

	@Query("select f from FlightCrewMember f where f.id = :crewMemberId")
	FlightCrewMember findCrewMemberById(int crewMemberId);

	@Query("select l from Leg l where l.draftMode = false")
	Collection<Leg> findAllPublishedLegs();

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select count(fa) > 0 from FlightAssignment fa where fa.id != :faId and fa.leg.id = :legId and fa.duty = :duty")
	boolean legHasDuty(int legId, int faId, Duties duty);

	//1 que no se trate de la misma assignment 2 que pertenezca a ese crew member 3 que no se tenga en cuenta la misma leg que se le pasa 4 que no se solapen los horarios
	@Query("select f from FlightAssignment f where f.id != :assignmentId and f.flightCrewMember.id = :crewMemberId and f.leg.id != :legId and f.leg.scheduledDeparture < :scheduledArrival and f.leg.scheduledArrival > :scheduledDeparture")
	Collection<FlightAssignment> findSimultaneousLegs(int assignmentId, int legId, Date scheduledDeparture, Date scheduledArrival, int crewMemberId);

}
