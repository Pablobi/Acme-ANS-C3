
package acme.features.authenticated.flightCrewMember;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Authenticated;
import acme.client.components.principals.UserAccount;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airlines.Airline;
import acme.realms.flightCrewMembers.AvailabilityStatus;
import acme.realms.flightCrewMembers.FlightCrewMember;

@GuiService
public class AuthenticatedFlightCrewMemberCreateService extends AbstractGuiService<Authenticated, FlightCrewMember> {

	@Autowired
	private AuthenticatedFlightCrewMemberRepository repository;


	@Override
	public void authorise() {

		boolean status = !super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		FlightCrewMember flightCrewMember;
		int userId = super.getRequest().getPrincipal().getAccountId();

		UserAccount userAccount = this.repository.findUserById(userId);
		flightCrewMember = new FlightCrewMember();
		flightCrewMember.setUserAccount(userAccount);

		super.getBuffer().addData(flightCrewMember);
	}

	@Override
	public void bind(final FlightCrewMember flightCrewMember) {
		int airlineId;
		Airline airline;

		airlineId = super.getRequest().getData("airline", int.class);
		airline = this.repository.findAirlineById(airlineId);
		flightCrewMember.setAirline(airline);
		flightCrewMember.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

		super.bindObject(flightCrewMember, "employeeCode", "phoneNumber", "languageSkills", "salary", "yearsOfExperience");

	}

	@Override
	public void validate(final FlightCrewMember flightCrewMember) {
		;
	}

	@Override
	public void perform(final FlightCrewMember flightCrewMember) {
		this.repository.save(flightCrewMember);
	}

	@Override
	public void unbind(final FlightCrewMember flightCrewMember) {
		Collection<Airline> airlines;
		SelectChoices airlineChoices;
		Dataset dataset;

		airlines = this.repository.findAllAirlines();
		airlineChoices = SelectChoices.from(airlines, "name", null);

		dataset = super.unbindObject(flightCrewMember, "employeeCode", "phoneNumber", "languageSkills", "salary", "yearsOfExperience");

		dataset.put("airline", airlineChoices.getSelected().getKey());
		dataset.put("airlines", airlineChoices);
		dataset.put("availabilityStatus", AvailabilityStatus.AVAILABLE);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}

}
