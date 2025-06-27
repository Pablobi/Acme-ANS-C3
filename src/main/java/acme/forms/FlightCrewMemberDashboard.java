
package acme.forms;

import java.util.List;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightCrewMemberDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	List<String>				lastFiveDestinations;

	Integer						legsSeverityLevel03;
	Integer						legsSeverityLevel47;
	Integer						legsSeverityLevel810;

	List<String>				crewMembersLastLeg;

	List<String>				confirmedFlightAssignments;
	List<String>				pendingFlightAssignments;
	List<String>				cancelledFlightAssignments;

	Double						averageLast10MonthsAssignments;
	Integer						minimumLast10MonthsAssignments;
	Integer						maximumLast10MonthsAssignments;
	Double						standardDeviationLast10MonthsAssignments;

}
