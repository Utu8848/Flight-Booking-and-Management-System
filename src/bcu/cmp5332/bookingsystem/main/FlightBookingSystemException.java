package bcu.cmp5332.bookingsystem.main;

/**
 * Base exception class for all flight booking system errors.
 * 
 * @author M9
 */

public class FlightBookingSystemException extends Exception {

    /**
     * Constructs a FlightBookingSystemException.
     * 
     * @param message the error message
     */
    public FlightBookingSystemException(String message) {
        super(message);
    }
}
