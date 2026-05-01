package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.model.Flight;

/**
 * Exception thrown when attempting to add a duplicate flight.
 * 
 * @author M9
 */

public class DuplicateFlightException extends FlightBookingSystemException {
    
    private Flight existingFlight;
    
    /**
     * Constructs a DuplicateFlightException.
     * 
     * @param message the error message
     * @param existingFlight the existing flight that caused the duplicate
     */
    public DuplicateFlightException(String message, Flight existingFlight) {
        super(message);
        this.existingFlight = existingFlight;
    }
    
    /**
     * Gets the existing flight.
     * 
     * @return the existing flight
     */
    public Flight getExistingFlight() {
        return existingFlight;
    }
}
