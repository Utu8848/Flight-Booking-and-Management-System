package bcu.cmp5332.bookingsystem.auth;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to log out the current user from the system.
 * 
 * @author M9
 */

public class Logout implements Command {
    
    /**
     * Executes the logout command.
     * Terminates the current user session.
     * 
     * @param fbs the flight booking system
     * @throws FlightBookingSystemException if logout fails
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        if (!fbs.getAuthService().isLoggedIn()) {
            System.out.println("No user is currently logged in.");
            return;
        }
        
        String username = fbs.getAuthService().getCurrentUser().getUsername();
        fbs.getAuthService().logout();
        System.out.println("Logged out successfully. Goodbye, " + username + "!");
    }
}
