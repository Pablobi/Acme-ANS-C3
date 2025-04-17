
package acme.entities.bookings;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface BookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(String locatorCode);

	@Query("select COUNT(t) from Takes t where t.booking.id = :id")
	Integer getNumberOfPassengersByBookingId(int id);

}
