
package acme.features.customer.passengerRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.passengers.Passenger;
import acme.entities.passengers.PassengerRecord;

@Repository
public interface CustomerPassengerRecordRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(int id);

	@Query("select p from Passenger p where p.id = :id")
	Passenger findPassengerById(int id);

	@Query("select p from Passenger p where p.customer.id = :id")
	Collection<Passenger> findAllPassengersByCustomerId(int id);

	@Query("select p from Passenger p where p.customer.id = :id AND p.draftMode = false")
	Collection<Passenger> findAllPublishedPassengersByCustomerId(int id);

	@Query("select b from Booking b where b.locatorCode = :locatorCode")
	Booking findBookingByLocatorCode(String locatorCode);

	@Query("select t.passenger from PassengerRecord t where t.booking.id = :id")
	Collection<Passenger> findAllPassengersByBookingId(int id);

	@Query("select t from PassengerRecord t where t.booking.id = :bookingId and t.passenger.id = :passengerId")
	PassengerRecord findPassengerRecordByPassengerBooking(Integer passengerId, Integer bookingId);
}
