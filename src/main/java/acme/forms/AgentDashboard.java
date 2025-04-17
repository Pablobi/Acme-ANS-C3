
package acme.forms;

import acme.client.components.basis.AbstractForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentDashboard extends AbstractForm {

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	Double						topThreeMonthsWithhighestNumberOfClaims;
	Double						ratioOfResolvedClaims;
	Double						ratioOfAcceptedApplications;

	Double						averageLogs;
	Double						deviationLogs;
	Double						maximumLogs;
	Double						minimumLogs;

	Double						averageClaimsLastMonth;
	Double						deviationClaimsLastMonth;
	Double						maximumClaimsLastMonth;
	Double						minimumClaimsLastMonth;

}
