
package acme.features.flightCrewMember.dashboard;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.AssignmentStatus;
import acme.entities.flights.Leg;
import acme.forms.FlightCrewMemberDashboard;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class FlightCrewMemberDashboardShowService extends AbstractGuiService<FlightCrewMember, FlightCrewMemberDashboard> {

	@Autowired
	private FlightCrewMemberDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		FlightCrewMember flightCrewMember = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		int crewMemberId;
		if (flightCrewMember != null)
			crewMemberId = flightCrewMember.getId();
		else
			crewMemberId = 0;
		Integer lastLegId;

		Integer legsSeverityLevel03 = this.repository.countLegsBetweenSeverityLevels(0, 3);
		Integer legsSeverityLevel47 = this.repository.countLegsBetweenSeverityLevels(4, 7);
		Integer legsSeverityLevel810 = this.repository.countLegsBetweenSeverityLevels(8, 10);

		List<String> confirmedFlightAssignments = this.repository.findFlightAssignmentsByStatus(crewMemberId, AssignmentStatus.CONFIRMED);
		List<String> pendingFlightAssignments = this.repository.findFlightAssignmentsByStatus(crewMemberId, AssignmentStatus.PENDING);
		List<String> cancelledFlightAssignments = this.repository.findFlightAssignmentsByStatus(crewMemberId, AssignmentStatus.CANCELLED);

		LocalDate hace10MesesLocal = LocalDate.now().minusMonths(9).withDayOfMonth(1);
		Date hace10Meses = java.sql.Date.valueOf(hace10MesesLocal);

		List<Integer> numberFlightAssignmentsLast10Months = this.repository.findNumberFlightAssignmentsLast10Months(crewMemberId, hace10Meses);

		Double averageLast10MonthsAssignments = this.repository.findAverageAssignmentsLast10Months(crewMemberId, hace10Meses);

		Integer minimumLast10MonthsAssignments;
		Integer maximumLast10MonthsAssignments;

		if (numberFlightAssignmentsLast10Months.isEmpty()) {
			minimumLast10MonthsAssignments = 0;
			maximumLast10MonthsAssignments = 0;
		} else {
			maximumLast10MonthsAssignments = Collections.max(numberFlightAssignmentsLast10Months);
			if (numberFlightAssignmentsLast10Months.size() != 10)
				minimumLast10MonthsAssignments = 0;
			else
				minimumLast10MonthsAssignments = Collections.min(numberFlightAssignmentsLast10Months);
		}

		Double media = numberFlightAssignmentsLast10Months.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
		Double sumaDiferenciasCuadrado = numberFlightAssignmentsLast10Months.stream().mapToDouble(n -> (n - media) * (n - media)).sum();

		Double standardDeviationLast10MonthsAssignments;
		if (numberFlightAssignmentsLast10Months.size() > 0)
			standardDeviationLast10MonthsAssignments = Math.sqrt(sumaDiferenciasCuadrado / numberFlightAssignmentsLast10Months.size());
		else
			standardDeviationLast10MonthsAssignments = 0.0;

		List<Leg> lastLegs = this.repository.findLastLegs(crewMemberId, MomentHelper.getCurrentMoment());

		if (lastLegs.isEmpty())
			lastLegId = 0;
		else
			lastLegId = lastLegs.get(0).getId();

		List<String> crewMembersLastLeg = this.repository.findCrewMembersFromLastLeg(lastLegId, crewMemberId).stream().map(e -> e.getIdentity().getName() + " " + e.getIdentity().getSurname()).toList();

		List<String> lastFiveDestinations = this.repository.findLastFiveDestinations(crewMemberId);

		if (lastFiveDestinations.size() >= 5)
			lastFiveDestinations = lastFiveDestinations.subList(0, 5);
		else
			lastFiveDestinations = lastFiveDestinations.subList(0, lastFiveDestinations.size());

		FlightCrewMemberDashboard dashboard = new FlightCrewMemberDashboard();

		dashboard.setLastFiveDestinations(lastFiveDestinations);
		dashboard.setLegsSeverityLevel03(legsSeverityLevel03);
		dashboard.setLegsSeverityLevel47(legsSeverityLevel47);
		dashboard.setLegsSeverityLevel810(legsSeverityLevel810);
		dashboard.setCrewMembersLastLeg(crewMembersLastLeg);
		dashboard.setConfirmedFlightAssignments(confirmedFlightAssignments);
		dashboard.setPendingFlightAssignments(pendingFlightAssignments);
		dashboard.setCancelledFlightAssignments(cancelledFlightAssignments);
		dashboard.setAverageLast10MonthsAssignments(averageLast10MonthsAssignments);
		dashboard.setMinimumLast10MonthsAssignments(minimumLast10MonthsAssignments);
		dashboard.setMaximumLast10MonthsAssignments(maximumLast10MonthsAssignments);
		dashboard.setStandardDeviationLast10MonthsAssignments(standardDeviationLast10MonthsAssignments);
		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final FlightCrewMemberDashboard dashboard) {

		Dataset dataset = super.unbindObject(dashboard, "lastFiveDestinations", "legsSeverityLevel03", "legsSeverityLevel47", //
			"legsSeverityLevel810", "crewMembersLastLeg", "confirmedFlightAssignments", "pendingFlightAssignments",//
			"cancelledFlightAssignments", "averageLast10MonthsAssignments", "minimumLast10MonthsAssignments", "maximumLast10MonthsAssignments",//
			"standardDeviationLast10MonthsAssignments");

		super.getResponse().addData(dataset);
	}

}
