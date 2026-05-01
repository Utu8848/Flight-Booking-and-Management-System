package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.AuthenticationService;

/**
 * Command to display help information about available commands.
 * 
 * @author M9
 */

public class Help implements Command {

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        AuthenticationService auth = flightBookingSystem.getAuthService();
        
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("                   AVAILABLE COMMANDS");
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
        
        // Public commands (always available)
        System.out.println("\n📋 GENERAL COMMANDS:");
        if (!auth.isLoggedIn() || auth.isCustomer()) {
            System.out.println("  login                - Log in to the system");
            System.out.println("  register             - Register a new customer account");
        } else {
            System.out.println("  login                - Log in to the system");
        }
        System.out.println("  logout               - Log out from the system");
        System.out.println("  listflights          - View all future flights");
        System.out.println("  showflight <id>      - Show details of a specific flight");
        System.out.println("  loadgui              - Launch graphical interface");
        System.out.println("  help                 - Display this help message");
        System.out.println("  exit                 - Exit the application");
        
        if (!auth.isLoggedIn()) {
            System.out.println("\n⚠ Please login to see more commands.");
            System.out.println("\nDefault Accounts:");
            System.out.println("  Admin:    username=admin,    password=admin123");
            System.out.println("  Customer: username=aarav,    password=aarav123");
            System.out.println("  Customer: username=binita,   password=binita123");
            System.out.println("  Customer: username=chiranjivi, password=chiranjivi123");
            System.out.println("  Customer: username=diya,     password=diya123");
            System.out.println("  Customer: username=elina,    password=elina123");
            return;
        }
        
        // Commands for logged-in users
        if (auth.isAdmin()) {
            System.out.println("\n👑 ADMINISTRATOR COMMANDS:");
            System.out.println("  addflight                              - Add a new flight");
            System.out.println("  deleteflight <id>                      - Delete a flight");
            System.out.println("  addcustomer                            - Add a new customer");
            System.out.println("  deletecustomer <id>                    - Delete a customer");
            System.out.println("  listcustomers                          - View all customers");
            System.out.println("  showcustomer <id>                      - Show customer details");
            System.out.println("\n📝 ADMIN BOOKING COMMANDS:");
            System.out.println("  addbooking <custID> <outboundID>                - Create one-way booking");
            System.out.println("  addbooking <custID> <outboundID> <returnID>     - Create round-trip booking");
            System.out.println("  cancelbooking <custID> <flightID>               - Cancel one-way booking (£25 fee)");
            System.out.println("  cancelbooking <custID> <outboundID> <returnID>  - Cancel round-trip booking (£50 fee)");
            System.out.println("  updatebooking <custID> <oldID> <newID>          - Update booking to a new flight (£15 fee)");
        }
        
        if (auth.isCustomer()) {
            System.out.println("\n📝 CUSTOMER BOOKING COMMANDS:");
            System.out.println("  addbooking <outboundID>                        - Create one-way booking");
            System.out.println("  addbooking <outboundID> <returnID>             - Create round-trip booking");
            System.out.println("  cancelbooking <flightID>                       - Cancel one-way booking (£25 fee)");
            System.out.println("  cancelbooking <outboundID> <returnID>          - Cancel round-trip booking (£50 fee)");
            System.out.println("  rebook <oldFlightID> <newFlightID>             - Rebook to a new flight (£15 fee)");
            System.out.println("\nNote: Your bookings will be created automatically with your Customer ID (" + auth.getCustomerId() + ")");
        }
        
        System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════");
    }
}
