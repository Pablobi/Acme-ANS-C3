
package acme.features.customer.takes;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.passengers.Takes;
import acme.realms.customers.Customer;

@GuiController
public class CustomerTakesController extends AbstractGuiController<Customer, Takes> {

	@Autowired
	private CustomerTakesCreateService createService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("create", this.createService);
	}

}
