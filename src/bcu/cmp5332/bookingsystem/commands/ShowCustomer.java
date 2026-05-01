package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to display detailed information about a customer.
 * 
 * @author M9
 */

public class ShowCustomer implements Command {
    
    private final int customerId;
    
    public ShowCustomer(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }
        System.out.println(customer.getDetailsLong());
    }
}
