
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.bookings.Booking;
import acme.entities.flights.Flight;
import acme.entities.passengers.Passenger;
import acme.entities.passengers.PassengerRecord;
import acme.realms.customers.Customer;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("select b from Booking b where b.id = :id")
	Booking findBookingById(Integer id);

	@Query("select b from Booking b")
	Collection<Booking> findAllBookings();

	@Query("select b from Booking b where b.customer.userAccount.id = :customerId")
	Collection<Booking> findAllBookingsByCustomerId(Integer customerId);

	@Query("select t.passenger from PassengerRecord t where t.booking.id = :bookingId")
	Collection<Passenger> findAllPassengersByBookingId(Integer bookingId);

	@Query("select c from Customer c where c.id = :customerId")
	Customer findCustomerById(Integer customerId);

	@Query("select f from Flight f where f.id = :flightId")
	Flight findFlightById(Integer flightId);

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select b.lastCreditCardDigits from Booking b where b.id = :id")
	Integer findCreditCardNibbleByBookingId(Integer id);

	@Query("select pr from PassengerRecord pr where pr.booking.id = :bookingId")
	Collection<PassengerRecord> findPassengerRecordsByBookingId(Integer bookingId);
}
