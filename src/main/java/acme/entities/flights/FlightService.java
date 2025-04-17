
package acme.entities.flights;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.services.AbstractGuiService;
import acme.realms.managers.Manager;

@Service
public class FlightService extends AbstractGuiService<Manager, Flight> {

	@Autowired
	private FlightRepository flightRepository;


	public Date getScheduledDeparture(final Flight flight) {
		return this.flightRepository.findScheduledDeparture(flight);
	}

	public Date getScheduledArrival(final Flight flight) {
		return this.flightRepository.findScheduledArrival(flight);
	}

	public String getOriginCity(final Flight flight) {
		return this.flightRepository.findOriginCity(flight);
	}

	public String getDestinationCity(final Flight flight) {
		return this.flightRepository.findDestinationCity(flight);
	}

	public Integer getNumberOfLayovers(final Flight flight) {
		return this.flightRepository.countLegs(flight);
	}

}
