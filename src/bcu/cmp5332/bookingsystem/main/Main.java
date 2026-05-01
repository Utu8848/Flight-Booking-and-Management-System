package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.AuthenticationService;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Main entry point for the application.
 * 
 * @author M9
 */
public class Main {
    
    // Commands that require admin privileges
    private static final List<String> ADMIN_COMMANDS = Arrays.asList(
        "addflight", "deleteflight", "addcustomer", "deletecustomer", "showcustomer", "showflight"
    );
    
    // Commands that require login (admin or customer)
    private static final List<String> LOGIN_REQUIRED_COMMANDS = Arrays.asList(
        "addbooking", "cancelbooking", "updatebooking", "rebook", "listcustomers"
    );
    
    // Commands that don't require login
    private static final List<String> PUBLIC_COMMANDS = Arrays.asList(
        "login", "register", "help", "listflights", "showflight", "logout", "exit", "loadgui"
    );
    
    // Commands allowed after logout
    private static final List<String> POST_LOGOUT_COMMANDS = Arrays.asList(
        "login", "register", "help", "exit", "loadgui"
    );
    
    private static boolean hasLoggedOut = false;

    public static void main(String[] args) throws IOException, FlightBookingSystemException {
        
        FlightBookingSystem fbs = FlightBookingSystemData.load();
        AuthenticationService auth = fbs.getAuthService();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║        Flight Booking Management System        ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Please login to continue or register for a new account.");
        System.out.println("Type 'login' to start, 'register' for new account, or 'help' for commands.");
        System.out.println();
        
        while (true) {
            // Show user status in prompt
            String prompt = auth.isLoggedIn() ? 
                "[" + auth.getCurrentUser().getUsername() + "] > " : "> ";
            System.out.print(prompt);
            
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            
            String cmd = line.split(" ")[0].toLowerCase();
            
            if (cmd.equals("exit")) {
                if (auth.isLoggedIn()) {
                    System.out.println("Logging out " + auth.getCurrentUser().getUsername() + "...");
                }
                break;
            }

            try {
                // First, try to parse the command to validate it (pass FBS for customer ID detection)
                Command command = CommandParser.parse(line, fbs);
                
                // If parsing succeeded, check permissions
                if (!checkPermissions(cmd, auth)) {
                    continue;
                }
                
                // Execute the command
                command.execute(fbs);
                
                // Update logout flag when user logs out
                if (cmd.equals("logout") && !auth.isLoggedIn()) {
                    hasLoggedOut = true;
                }
                
                // Reset logout flag when user logs in again
                if (cmd.equals("login") && auth.isLoggedIn()) {
                    hasLoggedOut = false;
                }
                
            } catch (FlightBookingSystemException ex) {
                System.out.println("Error: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        
        FlightBookingSystemData.store(fbs);
        System.out.println();
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("  Thank you for using Flight Booking System!");
        if (auth.isLoggedIn()) {
            System.out.println("  Goodbye, " + auth.getCurrentUser().getUsername() + "!");
        } else {
            System.out.println("  Have a wonderful day!");
        }
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println();
        System.exit(0);
    }
    
    /**
     * Checks if the user has permission to execute the command.
     */
    private static boolean checkPermissions(String cmd, AuthenticationService auth) {
        // After logout, only login and register commands are allowed
        if (hasLoggedOut && !POST_LOGOUT_COMMANDS.contains(cmd)) {
            System.out.println("⚠ You have been logged out. Please login or register to continue.");
            System.out.println("Available commands: login, register, loadgui, help, exit");
            return false;
        }
        
        // Public commands - always allowed
        if (PUBLIC_COMMANDS.contains(cmd)) {
            return true;
        }
        
        // Check if user is logged in for commands that require login
        if (!auth.isLoggedIn()) {
            System.out.println("⚠ You must be logged in to use this command.");
            System.out.println("Type 'login' to authenticate or 'register' to create a new account.");
            return false;
        }
        
        // Check admin-only commands
        if (ADMIN_COMMANDS.contains(cmd)) {
            if (!auth.isAdmin()) {
                System.out.println("⚠ Access denied. This command requires administrator privileges.");
                return false;
            }
        }
        
        // For customer-specific restrictions
        if (cmd.equals("listcustomers") && auth.isCustomer()) {
            System.out.println("⚠ Customers cannot view the customer list.");
            return false;
        }
        
        return true;
    }
}
