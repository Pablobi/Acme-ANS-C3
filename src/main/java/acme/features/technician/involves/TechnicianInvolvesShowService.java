
package acme.features.technician.involves;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.task.TaskType;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianInvolvesShowService extends AbstractGuiService<Technician, Involves> {

	@Autowired
	private TechnicianInvolvesRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int involvesId;
		Involves involves;
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		if (super.getRequest().getDataEntries().stream().anyMatch(e -> e.getKey().equals("id"))) {
			involvesId = super.getRequest().getData("id", int.class);
			involves = this.repository.findInvolvesById(involvesId);
			maintenanceRecord = involves == null ? null : involves.getMaintenanceRecord();
			technician = maintenanceRecord == null ? null : maintenanceRecord.getTechnician();
			status = technician != null && super.getRequest().getPrincipal().hasRealm(technician) && maintenanceRecord != null;
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Involves involves;
		int id;

		id = super.getRequest().getData("id", int.class);
		involves = this.repository.findInvolvesById(id);

		super.getBuffer().addData(involves);
	}

	@Override
	public void unbind(final Involves involves) {
		Dataset dataset;
		SelectChoices choices;

		choices = SelectChoices.from(TaskType.class, involves.getTask().getTaskType());

		dataset = super.unbindObject(involves);
		dataset.put("types", choices);
		dataset.put("taskType", involves.getTask().getTaskType());
		dataset.put("description", involves.getTask().getDescription());
		dataset.put("priority", involves.getTask().getPriority());
		dataset.put("estimatedDuration", involves.getTask().getEstimatedDuration());
		dataset.put("draftMode", involves.getTask().getDraftMode());
		dataset.put("task", involves.getTask());
		dataset.put("version", involves.getTask().getVersion());
		//		dataset.put("taskId", involves.getTask().getId());

		super.getResponse().addData(dataset);
	}

}
