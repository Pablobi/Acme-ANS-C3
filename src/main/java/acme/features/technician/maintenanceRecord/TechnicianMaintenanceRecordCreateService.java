
package acme.features.technician.maintenanceRecord;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircrafts.Aircraft;
import acme.entities.maintenanceRecords.MaintenanceRecord;
import acme.entities.maintenanceRecords.MaintenanceStatus;
import acme.realms.technicians.Technician;

@GuiService
public class TechnicianMaintenanceRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;


	@Override
	public void authorise() {
		boolean status;
		boolean mStatusIsValid;
		int aircraftId;
		String maintenanceStatus;
		Aircraft aircraft;

		if (super.getRequest().getMethod().equals("GET"))
			status = true;
		else {
			aircraftId = super.getRequest().getData("aircraft", int.class);
			aircraft = this.repository.findAircraftById(aircraftId);
			maintenanceStatus = super.getRequest().getData("maintenanceStatus", String.class);
			mStatusIsValid = false;

			for (MaintenanceStatus mStatus : MaintenanceStatus.values())
				if (maintenanceStatus.toLowerCase().trim().equals(mStatus.toString().toLowerCase().trim()) || maintenanceStatus.equals("0")) {
					mStatusIsValid = true;
					break;
				}
			status = (aircraft != null || aircraftId == 0) && mStatusIsValid;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;

		maintenanceRecord = new MaintenanceRecord();

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		int aircraftId;
		int technicianId;
		Aircraft aircraft;
		Technician technician;
		Date now;

		now = MomentHelper.getCurrentMoment();

		aircraftId = super.getRequest().getData("aircraft", int.class);
		aircraft = this.repository.findAircraftById(aircraftId);
		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		technician = this.repository.findTechnicianById(technicianId);
		maintenanceRecord.setMaintenanceMoment(now);
		maintenanceRecord.setAircraft(aircraft);
		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setDraftMode(true);

		super.bindObject(maintenanceRecord, "maintenanceStatus", "nextInspectionDate", "estimatedCost", "notes");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		;
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
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

		dataset = super.unbindObject(maintenanceRecord, "maintenanceMoment", "nextInspectionDate", "estimatedCost", "notes", "technician");
		dataset.put("statuses", statusChoices);
		dataset.put("draftMode", maintenanceRecord.getDraftMode());
		dataset.put("aircrafts", aircraftChoices);

		super.getResponse().addData(dataset);
	}

}
