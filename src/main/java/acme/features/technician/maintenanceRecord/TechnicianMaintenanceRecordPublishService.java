
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
		boolean mStatusIsValid;
		int maintenanceRecordId;
		int technicianId;
		int aircraftId;
		MaintenanceRecord maintenanceRecord;
		Technician technician;
		String maintenanceStatus;
		Aircraft aircraft;

		if (super.getRequest().getMethod().equals("GET"))
			status = false;
		else {
			maintenanceRecordId = super.getRequest().getData("id", int.class);
			maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

			technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
			technician = this.repository.findTechnicianById(technicianId);

			aircraftId = super.getRequest().getData("aircraft", int.class);
			aircraft = this.repository.findAircraftById(aircraftId);
			maintenanceStatus = super.getRequest().getData("maintenanceStatus", String.class);
			mStatusIsValid = false;

			for (MaintenanceStatus mStatus : MaintenanceStatus.values())
				if (maintenanceStatus.toLowerCase().trim().equals(mStatus.toString().toLowerCase().trim()) || maintenanceStatus.equals("0")) {
					mStatusIsValid = true;
					break;
				}

			status = maintenanceRecord != null && maintenanceRecord.getDraftMode().equals(true) && super.getRequest().getPrincipal().hasRealm(technician) && (aircraft != null || aircraftId == 0) && mStatusIsValid;
		}

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
		super.bindObject(maintenanceRecord, "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes");
		maintenanceRecord.setMaintenanceMoment(maintenanceRecord.getMaintenanceMoment());
		maintenanceRecord.setAircraft(aircraft);
		maintenanceRecord.setTechnician(technician);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		boolean hasUnpublishedTasks;
		boolean hasOnePublicTask;
		boolean isPublishable;
		boolean isCompleted;
		String maintenanceStatus;
		Collection<Task> tasks;

		tasks = this.repository.findTasksByMaintenanceRecord(maintenanceRecord.getId());

		hasUnpublishedTasks = tasks.stream().anyMatch(t -> t.getDraftMode().equals(true));
		hasOnePublicTask = tasks.stream().count() >= 1;
		isPublishable = !hasUnpublishedTasks && hasOnePublicTask;

		super.state(isPublishable, "*", "validation.maintenanceRecords.isPublishable");

		maintenanceStatus = super.getRequest().getData("maintenanceStatus", String.class);
		isCompleted = maintenanceStatus.toLowerCase().trim().equals(MaintenanceStatus.COMPLETED.toString().toLowerCase().trim());

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
