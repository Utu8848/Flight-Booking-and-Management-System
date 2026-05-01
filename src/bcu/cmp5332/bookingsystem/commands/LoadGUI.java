package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.gui.CustomerWindow;
import bcu.cmp5332.bookingsystem.gui.LoginWindow;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.AuthenticationService;

/**
 * Command to launch the graphical user interface.
 * 
 * @author M9
 */

public class LoadGUI implements Command {

    /**
     * Executes the command.
     * 
     * @param fbs the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        AuthenticationService auth = fbs.getAuthService();
        
        // If not logged in, show login window first
        if (!auth.isLoggedIn()) {
            System.out.println("Opening login window...");
            System.out.println("You can login with existing credentials or register a new account.");
            
            // Launch login window in non-blocking way
            javax.swing.SwingUtilities.invokeLater(() -> {
                new LoginWindow(fbs);
            });
            
            System.out.println("Login window opened. Please complete login in the window.");
            System.out.println("You can continue using console commands while the window is open.");
            return;
        }
        
        // Launch appropriate window based on user role
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (auth.isAdmin()) {
                new MainWindow(fbs);
                System.out.println("✓ Admin GUI loaded successfully.");
            } else if (auth.isCustomer()) {
                new CustomerWindow(fbs);
                System.out.println("✓ Customer GUI loaded successfully.");
            }
        });
        
        System.out.println("GUI is loading...");
        System.out.println("Note: You can continue using commands in the console while GUI is open.");
    }
}
