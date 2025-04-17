
package acme.constraints;

import java.util.Date;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.client.helpers.MomentHelper;
import acme.entities.maintenanceRecords.MaintenanceRecord;

@Validator
public class MaintenanceRecordsValidator extends AbstractValidator<ValidMaintenanceRecord, MaintenanceRecord> {

	// ConstraintValidator interface

	@Override
	protected void initialise(final ValidMaintenanceRecord annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final MaintenanceRecord record, final ConstraintValidatorContext context) {

		assert context != null;

		boolean result;

		if (record == null)
			super.state(context, false, "*", "javax.validation.constraints.NotNull.message");
		else {
			Date maintenanceMoment = record.getMaintenanceMoment();
			Date nextInspection = record.getNextInspectionDate();

			boolean nextInspectionIsValid = MomentHelper.isBefore(maintenanceMoment, nextInspection);

			super.state(context, nextInspectionIsValid, "nextInspectionDate", "validation.maintenanceRecords.nextInspectionDate");
		}

		result = !super.hasErrors(context);

		return result;

	}

}
