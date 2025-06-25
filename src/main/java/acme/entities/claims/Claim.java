
package acme.entities.claims;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidClaim;
import acme.constraints.ValidLongText;
import acme.entities.flights.Leg;
import acme.realms.agents.Agent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidClaim
@Table(indexes = {
	@Index(columnList = "id"), @Index(columnList = "agent_id")
})
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				email;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimType			type;

	@Mandatory
	@Valid
	@Automapped
	private boolean				draftMode;

	// Derived attributes -----------------------------------------------------


	@Transient
	public ClaimStatus getStatus() {
		ClaimStatus result;
		TrackingLogRepository repository;

		repository = SpringHelper.getBean(TrackingLogRepository.class);
		Integer size = repository.findTrackingLogsByMasterId(this.getId()).size();

		if (size > 0)
			result = repository.findTrackingLogsOrderedByPercentage(this.getId()).get(0).getStatus();
		else
			result = ClaimStatus.PENDING;

		return result;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Agent	agent;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Leg		leg;

}
