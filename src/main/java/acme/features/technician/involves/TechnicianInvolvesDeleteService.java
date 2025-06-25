
package acme.features.technician.involves;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.task.Task;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianInvolvesDeleteService extends AbstractGuiService<Technician, Involves> {

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

			status = technician != null && super.getRequest().getPrincipal().hasRealm(technician) && maintenanceRecord != null && maintenanceRecord.getDraftMode().equals(true);

			if (super.getRequest().getMethod().equals("POST")) {
				int taskId;
				taskId = super.getRequest().getData("task", int.class);

				Task task = this.repository.findTaskById(taskId);
				Involves involves = this.repository.findInvolvesByTaskAndMaintenanceRecord(taskId, maintenanceRecordId);

				status = status && (task != null && involves != null || taskId == 0);
			}
		} else
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		Involves involve;

		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
		involve = new Involves();

		involve.setMaintenanceRecord(maintenanceRecord);
		super.getBuffer().addData(involve);
	}

	@Override
	public void bind(final Involves involve) {
		int taskId = super.getRequest().getData("task", int.class);
		Task task = this.repository.findTaskById(taskId);

		super.bindObject(involve);
		involve.setTask(task);
	}

	@Override
	public void validate(final Involves involve) {
		boolean status;

		status = involve.getTask() != null;
		super.state(status, "task", "technician.involves.delete.task.not-null");
	}

	@Override
	public void perform(final Involves involve) {
		Task task = involve.getTask();
		MaintenanceRecord maintenanceRecord = involve.getMaintenanceRecord();
		Involves involveDatabase = this.repository.findInvolvesByMaintenanceRecordId(maintenanceRecord.getId()).stream().filter(i -> i.getTask().equals(task)).findFirst().orElse(null);

		this.repository.delete(involveDatabase);
	}

	@Override
	public void unbind(final Involves involve) {
		Dataset dataset;
		SelectChoices taskChoices;

		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		Collection<Task> tasks = this.repository.findTasksInMaintenanceRecord(maintenanceRecordId);

		taskChoices = SelectChoices.from(tasks, "description", involve.getTask());

		dataset = super.unbindObject(involve);
		dataset.put("task", taskChoices);
		dataset.put("maintenanceRecordId", maintenanceRecordId);

		super.getResponse().addData(dataset);
	}

}
