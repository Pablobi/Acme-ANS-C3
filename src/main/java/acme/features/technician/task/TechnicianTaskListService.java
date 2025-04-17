
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Task;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianTaskListService extends AbstractGuiService<Technician, Task> {

	@Autowired
	private TechnicianTaskRepository repository;


	@Override
	public void authorise() {

		super.getResponse().setAuthorised(true);

	}

	@Override
	public void load() {
		Collection<Task> tasks;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		tasks = this.repository.findTaskByTechnicianId(technicianId);

		super.getBuffer().addData(tasks);
	}

	@Override
	public void unbind(final Task tasks) {
		Dataset dataset;

		dataset = super.unbindObject(tasks, "taskType", "estimatedDuration", "priority");
		super.addPayload(dataset, tasks, "technician.id", "technician.licenseNumber");

		super.getResponse().addData(dataset);
	}
}
