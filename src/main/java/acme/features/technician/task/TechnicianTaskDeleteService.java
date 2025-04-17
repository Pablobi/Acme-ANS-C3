
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
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		int technicianId;
		Technician technician;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		technician = this.repository.findTechnicianById(technicianId);
		super.bindObject(task, "taskType", "description", "priority", "estimatedDuration");
		task.setTechnician(technician);
		task.setDraftMode(task.getDraftMode());
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
