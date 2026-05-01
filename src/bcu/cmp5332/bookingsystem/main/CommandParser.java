package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.*;
import bcu.cmp5332.bookingsystem.auth.*;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parses text commands and creates corresponding Command objects.
 * 
 * @author M9
 */

public class CommandParser {
    
    /**
     * Default constructor.
     */
    public CommandParser() {
    }
    
    /**
     * Parses a command line with FlightBookingSystem context for automatic customer ID detection.
     * 
     * @param line the command line to parse
     * @param fbs the flight booking system
     * @return the parsed command
     * @throws IOException if I/O error occurs
     * @throws FlightBookingSystemException if parsing fails
     */
    public static Command parse(String line, FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try {
            String[] parts = line.split(" ");
            String cmd = parts[0];

            // Login/Logout commands (always available)
            if (cmd.equals("login")) {
                return new Login();
            } else if (cmd.equals("logout")) {
                return new Logout();
            } else if (cmd.equals("register")) {
                return new Register();
            } else if (cmd.equals("addflight")) {
                return new AddFlight();
            } else if (cmd.equals("addcustomer")) {
                return new AddCustomer();
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
            } else if (parts.length == 1) {
                if (line.equals("listflights")) {
                    return new ListFlights();
                } else if (line.equals("listcustomers")) {
                    return new ListCustomers();
                } else if (line.equals("help")) {
                    return new Help();
                }
            } else if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);

                if (cmd.equals("showflight")) {
                    return new ShowFlight(id);
                } else if (cmd.equals("showcustomer")) {
                    return new ShowCustomer(id);
                } else if (cmd.equals("deleteflight")) {
                    return new DeleteFlight(id);
                } else if (cmd.equals("deletecustomer")) {
                    return new DeleteCustomer(id);
                } else if (cmd.equals("addbooking")) {
                    // Customer command - auto-detect customer ID (one-way)
                    if (fbs.getAuthService().isCustomer()) {
                        int customerId = fbs.getAuthService().getCustomerId();
                        int flightId = id;
                        return new AddBooking(customerId, flightId);
                    } else {
                        throw new FlightBookingSystemException("Invalid command format. Use: addbooking <customerID> <flightID> [<returnFlightID>]\nType 'help' to see all commands.");
                    }
                } else if (cmd.equals("cancelbooking")) {
                    // Customer command - auto-detect customer ID (one-way)
                    if (fbs.getAuthService().isCustomer()) {
                        int customerId = fbs.getAuthService().getCustomerId();
                        int outboundFlightId = id;
                        return new CancelBooking(customerId, outboundFlightId);
                    } else {
                        throw new FlightBookingSystemException("Invalid command format. Use: cancelbooking <customerID> <outboundFlightID> [<returnFlightID>]\nType 'help' to see all commands.");
                    }
                }
            } else if (parts.length >= 3) {
                if (cmd.equals("addbooking")) {
                    // Check if customer is logged in - they can provide 2 flight IDs for round-trip
                    if (fbs.getAuthService().isCustomer() && parts.length == 3) {
                        int customerId = fbs.getAuthService().getCustomerId();
                        int outboundFlightId = Integer.parseInt(parts[1]);
                        int returnFlightId = Integer.parseInt(parts[2]);
                        return new AddBooking(customerId, outboundFlightId, returnFlightId); // Customer round-trip
                    }
                    
                    // Admin usage with explicit customer ID
                    int id1 = Integer.parseInt(parts[1]);
                    int id2 = Integer.parseInt(parts[2]);
                    Integer id3 = null;
                    
                    // Check if there's a return flight ID (4th parameter for admin)
                    if (parts.length >= 4) {
                        try {
                            id3 = Integer.parseInt(parts[3]);
                        } catch (NumberFormatException e) {
                            // Not a number, ignore
                        }
                    }
                    
                    // Admin booking
                    if (id3 != null) {
                        return new AddBooking(id1, id2, id3); // Admin round-trip
                    } else {
                        return new AddBooking(id1, id2); // Admin one-way
                    }
                } else if (cmd.equals("cancelbooking")) {
                    // Check if customer is logged in - they can provide 2 flight IDs for round-trip
                    if (fbs.getAuthService().isCustomer() && parts.length == 3) {
                        int customerId = fbs.getAuthService().getCustomerId();
                        int outboundFlightId = Integer.parseInt(parts[1]);
                        int returnFlightId = Integer.parseInt(parts[2]);
                        return new CancelBooking(customerId, outboundFlightId, returnFlightId); // Customer round-trip
                    }
                    
                    // Admin usage with explicit customer ID
                    int customerId = Integer.parseInt(parts[1]);
                    int outboundFlightId = Integer.parseInt(parts[2]);
                    
                    // Check if there's a return flight ID (4th parameter for admin round-trip)
                    if (parts.length >= 4) {
                        try {
                            int returnFlightId = Integer.parseInt(parts[3]);
                            return new CancelBooking(customerId, outboundFlightId, returnFlightId);
                        } catch (NumberFormatException e) {
                            // Not a valid number, treat as one-way
                        }
                    }
                    return new CancelBooking(customerId, outboundFlightId);
                } else if (cmd.equals("updatebooking") || cmd.equals("rebook")) {
                    // Customer command: rebook <oldFlightID> <newFlightID>
                    if (cmd.equals("rebook")) {
                        if (fbs.getAuthService().isAdmin()) {
                            throw new FlightBookingSystemException("The 'rebook' command is for customers only. Administrators should use 'updatebooking' command.");
                        }
                        if (parts.length == 3) {
                            int customerId = fbs.getAuthService().getCustomerId();
                            int oldFlightId = Integer.parseInt(parts[1]);
                            int newFlightId = Integer.parseInt(parts[2]);
                            return new UpdateBooking(customerId, oldFlightId, newFlightId);
                        } else {
                            throw new FlightBookingSystemException("Invalid command format. Use: rebook <oldFlightID> <newFlightID>\nType 'help' to see all commands.");
                        }
                    }
                    
                    // Admin command: updatebooking <customerID> <oldFlightID> <newFlightID>
                    if (cmd.equals("updatebooking")) {
                        if (fbs.getAuthService().isCustomer()) {
                            throw new FlightBookingSystemException("The 'updatebooking' command is for administrators only. Customers should use 'rebook' command.");
                        }
                        if (parts.length == 4) {
                            int customerId = Integer.parseInt(parts[1]);
                            int oldFlightId = Integer.parseInt(parts[2]);
                            int newFlightId = Integer.parseInt(parts[3]);
                            return new UpdateBooking(customerId, oldFlightId, newFlightId);
                        } else {
                            throw new FlightBookingSystemException("Invalid command format. Use: updatebooking <customerID> <oldFlightID> <newFlightID>\nType 'help' to see all commands.");
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Error parsing command: " + ex.getMessage());
        }

        throw new FlightBookingSystemException("Invalid command.");
    }
    
    /**
     * Legacy parse method for backward compatibility (without FBS context).
     * 
     * @param line the command line to parse
     * @return the parsed command
     * @throws IOException if I/O error occurs
     * @throws FlightBookingSystemException if parsing fails
     */
    public static Command parse(String line) throws IOException, FlightBookingSystemException {
        try {
            String[] parts = line.split(" ", 4);
            String cmd = parts[0];

            // Login/Logout commands (always available)
            if (cmd.equals("login")) {
                return new Login();
            } else if (cmd.equals("logout")) {
                return new Logout();
            } else if (cmd.equals("register")) {
                return new Register();
            } else if (cmd.equals("addflight")) {
                return new AddFlight();
            } else if (cmd.equals("addcustomer")) {
                return new AddCustomer();
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
            } else if (parts.length == 1) {
                if (line.equals("listflights")) {
                    return new ListFlights();
                } else if (line.equals("listcustomers")) {
                    return new ListCustomers();
                } else if (line.equals("help")) {
                    return new Help();
                }
            } else if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);

                if (cmd.equals("showflight")) {
                    return new ShowFlight(id);
                } else if (cmd.equals("showcustomer")) {
                    return new ShowCustomer(id);
                } else if (cmd.equals("deleteflight")) {
                    return new DeleteFlight(id);
                } else if (cmd.equals("deletecustomer")) {
                    return new DeleteCustomer(id);
                }
            } else if (parts.length >= 3) {
                int id1 = Integer.parseInt(parts[1]);
                int id2 = Integer.parseInt(parts[2]);

                if (cmd.equals("addbooking")) {
                    // Check for optional return flight ID
                    Integer id3 = null;
                    if (parts.length >= 4) {
                        try {
                            id3 = Integer.parseInt(parts[3]);
                            return new AddBooking(id1, id2, id3); // Round-trip
                        } catch (NumberFormatException e) {
                            // Not a valid number, treat as one-way
                        }
                    }
                    return new AddBooking(id1, id2); // One-way
                } else if (cmd.equals("cancelbooking")) {
                    // Check for optional return flight ID
                    Integer id3 = null;
                    if (parts.length >= 4) {
                        try {
                            id3 = Integer.parseInt(parts[3]);
                            return new CancelBooking(id1, id2, id3); // Round-trip
                        } catch (NumberFormatException e) {
                            // Not a valid number, treat as one-way
                        }
                    }
                    return new CancelBooking(id1, id2); // One-way
                } else if (cmd.equals("updatebooking") || cmd.equals("rebook")) {
                    // updatebooking/rebook [customer id] [old flight id] [new flight id]
                    if (parts.length >= 4) {
                        // Admin attempted to use "rebook" - show error
                        if (cmd.equals("rebook")) {
                            throw new FlightBookingSystemException("The 'rebook' command is for customers only. Administrators should use 'updatebooking' command.");
                        }
                        int id3 = Integer.parseInt(parts[3]);
                        return new UpdateBooking(id1, id2, id3);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            throw new FlightBookingSystemException("Error parsing command: " + ex.getMessage());
        }

        throw new FlightBookingSystemException("Invalid command.");
    }
    
    private static LocalDate parseDateWithAttempts(BufferedReader br, int attempts) throws IOException, FlightBookingSystemException {
        if (attempts < 1) {
            throw new IllegalArgumentException("Number of attempts should be higher than 0");
        }
        while (attempts > 0) {
            attempts--;
            System.out.print("Departure Date (\"YYYY-MM-DD\" format): ");
            try {
                LocalDate departureDate = LocalDate.parse(br.readLine());
                return departureDate;
            } catch (DateTimeParseException dtpe) {
                System.out.println("Date must be in YYYY-MM-DD format. " + attempts + " attempts remaining...");
            }
        }
        
        throw new FlightBookingSystemException("Incorrect departure date provided. Cannot create flight.");
    }
    
    private static LocalDate parseDateWithAttempts(BufferedReader br) throws IOException, FlightBookingSystemException {
        return parseDateWithAttempts(br, 3);
    }
}
