package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to display detailed information about a flight.
 * 
 * @author M9
 */

public class ShowFlight implements Command {
    
    private final int flightId;
    
    public ShowFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " not found.");
        }
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Flight has been deleted.");
        }
        
        // Check if user is a customer - if so, hide passenger list
        boolean isCustomer = flightBookingSystem.getAuthService().isCustomer();
        if (isCustomer) {
            System.out.println(flight.getDetailsLongNoPassengers());
        } else {
            System.out.println(flight.getDetailsLong());
        }
    }
}
