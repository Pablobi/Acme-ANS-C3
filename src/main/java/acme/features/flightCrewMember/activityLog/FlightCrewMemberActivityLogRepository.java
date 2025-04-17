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

package acme.features.flightCrewMember.activityLog;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;

@Repository
public interface FlightCrewMemberActivityLogRepository extends AbstractRepository {

	@Query("select al from ActivityLog al where al.id = :id")
	ActivityLog findActivityLogById(int id);

	@Query("select al from ActivityLog al")
	Collection<ActivityLog> findAllActivityLogs();

	@Query("select a from FlightAssignment a where a.leg.scheduledArrival<:now")
	Collection<FlightAssignment> findCompletedAssignments(Date now);

	@Query("select fa from FlightAssignment fa")
	Collection<FlightAssignment> findAllAssignments();

	@Query("select fa from FlightAssignment fa where fa.id = :masterId")
	FlightAssignment findAssignmentById(int masterId);

	@Query("select al.assignment from ActivityLog al where al.id = :activityLogId")
	FlightAssignment findAssignmentByActivityLogId(int activityLogId);

	@Query("select al from ActivityLog al where al.assignment.id = :masterId")
	Collection<ActivityLog> findActivityLogsByMasterId(int masterId);

	@Query("SELECT CASE WHEN COUNT(fcm) > 0 THEN true ELSE false END FROM FlightCrewMember fcm WHERE fcm.id = :id")
	boolean existsFlightCrewMember(int id);

	@Query("select count(al) > 0 from ActivityLog al where al.id = :activityLogId and al.assignment.flightCrewMember.id = :flightCrewMemberId")
	boolean thatActivityLogIsOf(int activityLogId, int flightCrewMemberId);

}
