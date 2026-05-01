package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.model.AuthenticationService;

import java.io.IOException;

/**
 * Command to delete a customer from the system.
 * 
 * @author M9
 */

public class DeleteCustomer implements Command {
    
    private final int customerId;
    
    /**
     * Constructs a DeleteCustomer command.
     * 
     * @param customerId the customer ID to delete
     */
    public DeleteCustomer(int customerId) {
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
        if (customer.isDeleted()) {
            throw new FlightBookingSystemException("Customer has already been deleted.");
        }
        
        // Set deleted flag on customer
        customer.setDeleted(true);
        
        // Also mark the associated user account as deleted
        AuthenticationService authService = flightBookingSystem.getAuthService();
        authService.markCustomerUserAsDeleted(customerId);
        
        // Save changes
        try {
            FlightBookingSystemData.store(flightBookingSystem);
        } catch (IOException ex) {
            customer.setDeleted(false);
            authService.markCustomerUserAsDeleted(customerId, false); // Rollback
            throw new FlightBookingSystemException("Error saving deletion. Changes have been rolled back: " + ex.getMessage());
        }
        
        System.out.println("Customer and associated user account deleted successfully!");
    }
}
