
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianInvolvesListService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TechnicianInvolvesRepository repository;


	@Override
	public void authorise() {

		super.getResponse().setAuthorised(true);

	}

	@Override
	public void load() {
		Collection<Involves> involves;
		int maintenanceRecordId;

		maintenanceRecordId = super.getRequest().getData("masterId", int.class);

		involves = this.repository.findInvolvesByMaintenanceRecordId(maintenanceRecordId);

		super.getBuffer().addData(involves);
	}

	@Override
	public void unbind(final Involves involves) {
		Dataset dataset;

		dataset = super.unbindObject(involves, "task.taskType", "task.estimatedDuration", "task.priority");
		super.addPayload(dataset, involves, "task.technician.id", "task.technician.licenseNumber");

		super.getResponse().addData(dataset);
	}

}
