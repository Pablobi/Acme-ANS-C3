
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
public class TechnicianInvolvesCreateService extends AbstractGuiService<Technician, Involves> {

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

				status = status && (task != null || taskId == 0) && involves == null;
				if (task != null)
					status = status && (task.getTechnician().equals(technician) || task.getDraftMode().equals(false));
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
		;
	}

	@Override
	public void perform(final Involves involve) {
		this.repository.save(involve);
	}

	@Override
	public void unbind(final Involves involve) {
		Dataset dataset;
		SelectChoices taskChoices;

		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
		Technician technician = maintenanceRecord == null ? null : maintenanceRecord.getTechnician();
		Collection<Task> tasks = this.repository.findAllTasks();
		Collection<Task> tasksInMR = this.repository.findTasksInMaintenanceRecord(maintenanceRecordId);
		Collection<Task> taskUnpublished = tasks.stream().filter(t -> t.getTechnician() != technician && t.getDraftMode().equals(true)).toList();

		tasks = tasks.stream().filter(t -> !tasksInMR.contains(t) && !taskUnpublished.contains(t)).toList();

		taskChoices = SelectChoices.from(tasks, "description", involve.getTask());

		dataset = super.unbindObject(involve);
		dataset.put("task", taskChoices);
		dataset.put("maintenanceRecordId", maintenanceRecordId);

		super.getResponse().addData(dataset);
	}
}
