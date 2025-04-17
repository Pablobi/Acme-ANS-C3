
package acme.entities.flights;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.constraints.ValidLongText;
import acme.constraints.ValidShortText;
import acme.realms.managers.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	// Serialisation identifier
	private static final long	serialVersionUID	= 1L;

	// Attributes
	@Mandatory
	@ValidShortText
	@Automapped
	private String				tag;

	@Mandatory
	@Valid
	@Automapped
	private Boolean				selfTransfer;

	@Mandatory
	@ValidMoney
	@Automapped
	private Money				cost;

	@Optional
	@ValidLongText
	@Automapped
	private String				description;

	// Derived attributes
	//	@Transient
	//	@Query("SELECT MIN(l.scheduledDeparture) FROM Leg l WHERE l.flight = :flight")
	//	public Date getScheduledDeparture();

	//
	//		@Transient
	//		public Date getScheduledArrival() {
	//			TypedQuery<Date> query = this.entityManager.createQuery("SELECT MAX(l.scheduledArrival) FROM Leg l WHERE l.flight = :flight", Date.class);
	//			query.setParameter("flight", this);
	//			return query.getSingleResult();
	//		}
	//	
	//		@Transient
	//		public String getOriginCity() {
	//			TypedQuery<String> query = this.entityManager.createQuery("SELECT l.departureAirport FROM Leg l WHERE l.flight = :flight ORDER BY l.scheduledDeparture ASC", String.class);
	//			query.setParameter("flight", this);
	//			return query.setMaxResults(1).getSingleResult();
	//		}
	//	
	//		@Transient
	//		public String getDestinationCity() {
	//			TypedQuery<String> query = this.entityManager.createQuery("SELECT l.arrivalAirport FROM Leg l WHERE l.flight = :flight ORDER BY l.scheduledArrival DESC", String.class);
	//			query.setParameter("flight", this);
	//			return query.setMaxResults(1).getSingleResult();
	//		}
	//	
	//		@Transient
	//		public int getNumberOfLayovers() {
	//			TypedQuery<Long> query = this.entityManager.createQuery("SELECT COUNT(l) FROM Leg l WHERE l.flight = :flight", Long.class);
	//			query.setParameter("flight", this);
	//			Long count = query.getSingleResult();
	//			return count != null ? Math.max(count.intValue() - 1, 0) : 0;
	//		}

	//	@Transient
	//	public Date getScheduledDeparture(final FlightService flightService) {
	//		return flightService.getScheduledDeparture(this.getId());
	//	}
	//
	//	@Transient
	//	public Date getScheduledArrival(final FlightService flightService) {
	//		return flightService.getScheduledArrival(this.getId());
	//	}
	//
	//	@Transient
	//	public String getOriginCity(final FlightService flightService) {
	//		return flightService.getOriginCity(this.getId());
	//	}
	//
	//	@Transient
	//	public String getDestinationCity(final FlightService flightService) {
	//		return flightService.getDestinationCity(this.getId());
	//	}
	//
	//	@Transient
	//	public int getNumberOfLayovers(final FlightService flightService) {
	//		return flightService.getNumberOfLayovers(this.getId());
	//	}

	// Relationships
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager				manager;

}
