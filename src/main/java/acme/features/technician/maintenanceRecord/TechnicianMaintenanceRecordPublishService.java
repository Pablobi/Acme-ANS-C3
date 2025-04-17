
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.maintenanceRecords.MaintenanceStatus;
import acme.entities.task.Task;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordId;
		int technicianId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;

		maintenanceRecordId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		technician = this.repository.findTechnicianById(technicianId);

		status = maintenanceRecord != null && maintenanceRecord.getDraftMode().equals(true) && super.getRequest().getPrincipal().hasRealm(technician);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		int aircraftId;
		int technicianId;
		Aircraft aircraft;
		Technician technician;

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);
		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		technician = this.repository.findTechnicianById(technicianId);
		super.bindObject(maintenanceRecord, "maintenanceMoment", "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes");
		maintenanceRecord.setAircraft(aircraft);
		maintenanceRecord.setTechnician(technician);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		boolean hasUnpublishedTasks;
		boolean hasOnePublicTask;
		boolean isPublishable;
		boolean isCompleted;
		Collection<Task> tasks;

		tasks = this.repository.findTasksByMaintenanceRecord(maintenanceRecord.getId());

		hasUnpublishedTasks = tasks.stream().anyMatch(t -> t.getDraftMode().equals(true));
		hasOnePublicTask = tasks.stream().count() >= 1;
		isPublishable = !hasUnpublishedTasks && hasOnePublicTask;

		super.state(isPublishable, "*", "validation.maintenanceRecords.isPublishable");

		isCompleted = maintenanceRecord.getMaintenanceStatus().equals(MaintenanceStatus.COMPLETED);

		super.state(isCompleted, "*", "validation.maintenanceRecords.isCompleted");
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		maintenanceRecord.setDraftMode(false);
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;
		SelectChoices statusChoices;
		SelectChoices aircraftChoices;
		Collection<Aircraft> aircrafts;

		aircrafts = this.repository.findAllAircrafts();

		aircraftChoices = SelectChoices.from(aircrafts, "registrationNumber", maintenanceRecord.getAircraft());
		statusChoices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getMaintenanceStatus());

		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "nextInspectionDate", "estimatedCost", "draftMode", "notes", "technician");
		dataset.put("statuses", statusChoices);
		dataset.put("aircrafts", aircraftChoices);

		super.getResponse().addData(dataset);
	}

}
