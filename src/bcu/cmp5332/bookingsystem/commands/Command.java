package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Interface for all command objects in the system.
 * Defines the contract that all executable commands must implement.
 * 
 * @author M9
 */
public interface Command {

    /** Help message displaying all available commands. */
    public static final String HELP_MESSAGE = "Flight Booking System - Available Commands:\n"
        + "========================================================================================\n"
        + "\nFlight Management:\n"
        + "  listflights                                   - List all upcoming flights\n"
        + "  addflight                                     - Add a new flight\n"
        + "  showflight [flight id]                        - Show detailed flight information\n"
        + "  deleteflight [flight id]                      - Delete (hide) a flight\n"
        + "\nCustomer Management:\n"
        + "  listcustomers                                 - List all customers\n"
        + "  addcustomer                                   - Add a new customer\n"
        + "  showcustomer [customer id]                    - Show detailed customer information\n"
        + "  deletecustomer [customer id]                  - Delete (hide) a customer\n"
        + "\nBooking Management:\n"
        + "  addbooking [customer id] [flight id]          - Create a new booking\n"
        + "  cancelbooking [customer id] [flight id]       - Cancel an existing booking\n"
        + "  updatebooking [customer id] [old flight id] [new flight id] - Update a booking\n"
        + "\nSystem Commands:\n"
        + "  loadgui                                       - Launch the GUI interface\n"
        + "  help                                          - Display this help message\n"
        + "  exit                                          - Exit the program\n"
        + "\n========================================================================================";

    /**
     * Executes the command on the given flight booking system.
     * 
     * @param flightBookingSystem the flight booking system to operate on
     * @throws FlightBookingSystemException if the command execution fails
     */
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException;
    
}
