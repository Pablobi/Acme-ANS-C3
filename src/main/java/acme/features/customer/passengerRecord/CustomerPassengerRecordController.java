
package acme.features.customer.passengerRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.passengers.PassengerRecord;
import acme.realms.customers.Customer;

@GuiController
public class CustomerPassengerRecordController extends AbstractGuiController<Customer, PassengerRecord> {

	@Autowired
	private CustomerPassengerRecordCreateService	createService;

	@Autowired
	private CustomerPassengerRecordDeleteService	deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
	}

}
