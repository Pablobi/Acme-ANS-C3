
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.involves.Involves;
import acme.entities.task.Task;
import acme.entities.task.TaskType;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianTaskDeleteService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {
		boolean status;
		String taskType;

		if (super.getRequest().getMethod().equals("GET"))
			status = false;
		else {
			taskType = super.getRequest().getData("taskType", String.class);
			status = false;

			for (TaskType tType : TaskType.values())
				if (taskType.toLowerCase().trim().equals(tType.toString().toLowerCase().trim()) || taskType.equals("0")) {
					status = true;
					break;
				}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		if (task == null) {
			task = this.repository.findInvolvesById(id).getTask();
			task.setId(this.repository.findInvolvesById(id).getTask().getId());
		}

		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		int technicianId;
		Technician technician;
		int originalTaskId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		technician = this.repository.findTechnicianById(technicianId);
		originalTaskId = task.getId();
		super.bindObject(task, "taskType", "description", "priority", "estimatedDuration");
		task.setTechnician(technician);
		task.setDraftMode(task.getDraftMode());
		task.setId(originalTaskId);
	}

	@Override
	public void validate(final Task task) {
		;
	}

	@Override
	public void perform(final Task task) {
		Collection<Involves> involves = this.repository.findInvolvesByTask(task.getId());
		for (Involves i : involves)
			this.repository.delete(i);

		this.repository.delete(task);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;
		SelectChoices typeChoices;

		typeChoices = SelectChoices.from(TaskType.class, task.getTaskType());

		dataset = super.unbindObject(task, "taskType", "description", "priority", "estimatedDuration", "technician");
		dataset.put("types", typeChoices);
		dataset.put("draftMode", task.getDraftMode());

		super.getResponse().addData(dataset);
	}

}
