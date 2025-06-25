
package acme.entities.passengers;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.entities.bookings.Booking;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "booking_id"), @Index(columnList = "booking_id,passenger_id"), @Index(columnList = "passenger_id")
})
public class PassengerRecord extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	// Relationships
	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Passenger			passenger;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Booking				booking;
}
