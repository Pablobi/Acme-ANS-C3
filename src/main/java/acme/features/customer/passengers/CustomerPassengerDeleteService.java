
package acme.features.customer.passengers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passengers.Passenger;
import acme.entities.passengers.PassengerRecord;
import acme.realms.customers.Customer;

@GuiService
public class CustomerPassengerDeleteService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		Integer passengerId;
		Customer customer;
		Passenger passenger;
		boolean status;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		customer = passenger.getCustomer();
		status = super.getRequest().getPrincipal().hasRealm(customer) && passenger.isDraftMode();

		if (super.getRequest().getMethod().equals("GET"))
			status = false;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Passenger passenger;
		int id;

		id = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		;
	}

	@Override
	public void validate(final Passenger passenger) {
		;
	}

	@Override
	public void perform(final Passenger passenger) {
		Collection<PassengerRecord> passengerRecords;
		passengerRecords = this.repository.findPassengerRecordsByPassengerId(passenger.getId());
		for (final PassengerRecord passengerRecord : passengerRecords)
			this.repository.delete(passengerRecord);
		this.repository.delete(passenger);
	}

}
