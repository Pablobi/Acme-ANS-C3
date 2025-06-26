
package acme.realms.flightCrewMembers;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidCrewMemberCode;
import acme.constraints.ValidLongText;
import acme.entities.airlines.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidCrewMemberCode
public class FlightCrewMember extends AbstractRole {

	// Serialisation identifier
	private static final long	serialVersionUID	= 1L;

	// Attributes
	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$", message = "{validation.flightCrewMember.codePattern}")
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$", message = "{validation.flightCrewMember.phoneNumberPattern}")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				languageSkills;

	@Mandatory
	@Valid
	@Automapped
	private AvailabilityStatus	availabilityStatus;

	@Mandatory
	@ValidMoney
	@Automapped
	private Money				salary;

	@Optional
	@ValidNumber(min = 0, max = 120)
	@Automapped
	private Integer				yearsOfExperience;

	// Derived attributes

	// Relationships

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airline				airline;
}
