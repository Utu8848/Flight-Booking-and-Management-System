package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDate;
import java.util.List;

/**
 * Command to list all flights in the system.
 * 
 * @author M9
 */

public class ListFlights implements Command {

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        List<Flight> flights = flightBookingSystem.getFlights();
        LocalDate systemDate = flightBookingSystem.getSystemDate();
        
        int count = 0;
        System.out.println("Upcoming Flights:");
        System.out.println("====================================================================");
        
        for (Flight flight : flights) {
            // Only show non-deleted flights that haven't departed
            if (!flight.isDeleted() && !flight.hasDeparted(systemDate)) {
                System.out.println(flight.getDetailsShort());
                count++;
            }
        }
        
        System.out.println("\n" + count + " upcoming flight(s) found.");
    }
}
