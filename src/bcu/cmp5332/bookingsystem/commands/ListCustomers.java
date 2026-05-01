package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.util.List;

/**
 * Command to list all customers in the system.
 * 
 * @author M9
 */

public class ListCustomers implements Command {

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        List<Customer> customers = flightBookingSystem.getCustomers();
        
        System.out.println("Total customers loaded: " + customers.size());
        
        int visibleCount = 0;
        for (Customer customer : customers) {
            if (!customer.isDeleted()) {
                System.out.println(customer.getDetailsShort() + " [Bookings: " + customer.getBookings().size() + "]");
                visibleCount++;
            }
        }
        
        if (visibleCount == 0) {
            System.out.println("No active customers found.");
            System.out.println("(Total customers in system: " + customers.size() + ", but all are deleted)");
        } else {
            System.out.println("\n" + visibleCount + " active customer(s) found.");
        }
    }
}
