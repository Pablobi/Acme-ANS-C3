
package acme.features.administrator.dashboard;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claims.ClaimStatus;
import acme.forms.AgentDashboard;

@GuiService
public class AdministratorAgentDashboardShowService extends AbstractGuiService<Administrator, AgentDashboard> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAgentDashboardRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		AgentDashboard dashboard;
		Double averageLogs;
		Double deviationLogs;
		Double maxLogs;
		Double minLogs;
		Double ratioOfResolvedClaims;
		Double ratioOfRejectedClaims;

		double totalClaims = 1. * this.repository.findAllClaims().size();
		double resolvedClaims = 1. * this.repository.findAllClaims().stream().filter(c -> c.getStatus() == ClaimStatus.ACCEPTED || c.getStatus() == ClaimStatus.REJECTED).collect(Collectors.toList()).size();
		double rejectedClaims = 1. * this.repository.findAllClaims().stream().filter(c -> c.getStatus() == ClaimStatus.REJECTED).collect(Collectors.toList()).size();

		averageLogs = this.repository.averageLogs();
		deviationLogs = this.repository.deviationLogs();
		minLogs = this.repository.minLogs();
		maxLogs = this.repository.maxLogs();

		ratioOfResolvedClaims = resolvedClaims / totalClaims;
		ratioOfRejectedClaims = rejectedClaims / totalClaims;

		dashboard = new AgentDashboard();
		dashboard.setAverageLogs(averageLogs);
		dashboard.setDeviationLogs(deviationLogs);
		dashboard.setMaximumLogs(maxLogs);
		dashboard.setMinimumLogs(minLogs);
		dashboard.setRatioOfResolvedClaims(ratioOfResolvedClaims);
		dashboard.setRatioOfRejectedClaims(ratioOfRejectedClaims);

		super.getBuffer().addData(dashboard);
	}

	@Override
	public void unbind(final AgentDashboard dashboard) {
		Dataset dataset;

		dataset = super.unbindObject(dashboard, //
			"averageLogs", "DeviationLogs", // 
			"minimumLogs", "maximumLogs", //
			"ratioOfResolvedClaims", "ratioOfRejectedClaims");

		super.getResponse().addData(dataset);
	}

}
