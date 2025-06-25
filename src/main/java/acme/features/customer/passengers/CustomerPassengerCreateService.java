
package acme.features.customer.passengers;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Passenger;
import acme.realms.customers.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		Integer id;
		boolean status;

		status = true;
		if (super.getRequest().getMethod().equals("POST")) {
			id = super.getRequest().getData("id", Integer.class);
			if (id != null) {
				if (id != 0)
					status = false;
			} else
				status = false;
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		Integer customerId;
		Customer customer;

		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		customer = this.repository.findCustomerById(customerId);

		passenger = new Passenger();
		passenger.setCustomer(customer);
		passenger.setDraftMode(true);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {

		super.bindObject(passenger, "name", "email", "passport", "dateOfBirth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {
		;
	}

	@Override
	public void perform(final Passenger passenger) {
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		dataset = super.unbindObject(passenger, "name", "email", "passport", "dateOfBirth", "specialNeeds");

		super.getResponse().addData(dataset);
	}

}
