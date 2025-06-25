
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianInvolvesListService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TechnicianInvolvesRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		if (super.getRequest().getDataEntries().stream().anyMatch(e -> e.getKey().equals("maintenanceRecordId"))) {
			maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
			technician = maintenanceRecord == null ? null : maintenanceRecord.getTechnician();
			status = technician != null && super.getRequest().getPrincipal().hasRealm(technician) && maintenanceRecord != null;
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Involves> involves;
		int maintenanceRecordId;

		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);

		involves = this.repository.findInvolvesByMaintenanceRecordId(maintenanceRecordId);

		super.getBuffer().addData(involves);
	}

	@Override
	public void unbind(final Involves involves) {
		Dataset dataset;

		dataset = super.unbindObject(involves, "task.taskType", "task.estimatedDuration", "task.priority");
		super.addPayload(dataset, involves, "task.technician.id", "task.technician.licenseNumber", "task.description");

		super.getResponse().addData(dataset);
	}

}
