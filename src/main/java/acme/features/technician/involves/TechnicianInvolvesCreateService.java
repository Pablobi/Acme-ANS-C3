
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
		super.getResponse().setAuthorised(true);
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
		Collection<Task> tasks = this.repository.findAllTasks();
		Collection<Task> tasksInMR = this.repository.findTasksInMaintenanceRecord(maintenanceRecordId);

		tasks = tasks.stream().filter(t -> !tasksInMR.contains(t)).toList();

		taskChoices = SelectChoices.from(tasks, "id", involve.getTask());

		dataset = super.unbindObject(involve);
		dataset.put("task", taskChoices);
		dataset.put("maintenanceRecordId", maintenanceRecordId);

		super.getResponse().addData(dataset);
	}
}
