package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.main.DuplicateFlightException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Command to add a new flight to the system.
 * 
 * @author M9
 */

public class AddFlight implements Command {
    
    // Fields for GUI mode
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private Integer capacity;
    private Double price;
    private boolean skipConfirmation; // For GUI when user already confirmed
    private boolean isGUIMode; // Track if constructed for GUI

    /**
     * Constructor for CLI mode (interactive input).
     */
    public AddFlight() {
        // Interactive mode - will prompt for input
        this.skipConfirmation = false;
        this.isGUIMode = false;
    }
    
    /**
     * Constructor for GUI mode (basic parameters).
     * Uses default capacity (100) and price (150.0).
     * 
     * @param flightNumber the flight number
     * @param origin the origin city
     * @param destination the destination city
     * @param departureDate the departure date
     */
    public AddFlight(String flightNumber, String origin, String destination, LocalDate departureDate) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = 100;
        this.price = 150.0;
        this.skipConfirmation = false;
        this.isGUIMode = true;
    }
    
    /**
     * Constructor for GUI mode (all parameters).
     * 
     * @param flightNumber the flight number
     * @param origin the origin city
     * @param destination the destination city
     * @param departureDate the departure date
     * @param capacity the flight capacity
     * @param price the base price
     */
    public AddFlight(String flightNumber, String origin, String destination, 
                     LocalDate departureDate, int capacity, double price) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
        this.skipConfirmation = false;
        this.isGUIMode = true;
    }
    
    /**
     * Constructor for GUI mode with confirmation bypass.
     * Used when user has already confirmed duplicate flight in GUI.
     * 
     * @param flightNumber the flight number
     * @param origin the origin city
     * @param destination the destination city
     * @param departureDate the departure date
     * @param capacity the flight capacity
     * @param price the base price
     * @param skipConfirmation if true, skip duplicate confirmation
     */
    public AddFlight(String flightNumber, String origin, String destination, 
                     LocalDate departureDate, int capacity, double price, boolean skipConfirmation) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
        this.skipConfirmation = skipConfirmation;
        this.isGUIMode = true;
    }

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        try {
            // If parameters not set, prompt for input (CLI mode)
            if (flightNumber == null) {
                promptForInput();
            }
            
            // Validate departure date is not in the past
            LocalDate systemDate = flightBookingSystem.getSystemDate();
            if (departureDate.isBefore(systemDate)) {
                throw new FlightBookingSystemException(
                    "Cannot add flight with departure date in the past. " +
                    "Departure date: " + departureDate + ", System date: " + systemDate
                );
            }
            
            // Validate departure date is not more than 12 months in the future
            // Most airlines allow bookings 11-12 months in advance (approximately 365 days)
            LocalDate maxBookingDate = systemDate.plusMonths(12);
            if (departureDate.isAfter(maxBookingDate)) {
                throw new FlightBookingSystemException(
                    "Cannot add flight more than 12 months in advance. " +
                    "Departure date: " + departureDate + ", Maximum allowed date: " + maxBookingDate + 
                    ". Most airlines open bookings approximately 11-12 months in advance."
                );
            }
            
            // Trim all input data to ensure consistency
            flightNumber = flightNumber.trim();
            origin = origin.trim();
            destination = destination.trim();
            
            // Check for exact duplicate (same details: flight number, origin, destination, date)
            // Note: Flight number alone can be duplicated, but complete details must be unique
            Flight duplicateFlight = null;
            for (Flight existingFlight : flightBookingSystem.getFlights()) {
                if (!existingFlight.isDeleted() && 
                    existingFlight.getFlightNumber().equalsIgnoreCase(flightNumber) &&
                    existingFlight.getOrigin().equalsIgnoreCase(origin) &&
                    existingFlight.getDestination().equalsIgnoreCase(destination) &&
                    existingFlight.getDepartureDate().equals(departureDate)) {
                    duplicateFlight = existingFlight;
                    break;
                }
            }
            
            // If duplicate found, handle based on mode
            if (duplicateFlight != null && !skipConfirmation) {
                if (isGUIMode) {
                    // GUI mode - throw special exception for GUI to handle with dialog
                    throw new DuplicateFlightException(
                        "A flight with the same details already exists.", 
                        duplicateFlight);
                } else {
                    // CLI mode - ask for confirmation interactively
                    boolean confirmed = confirmDuplicateFlight(duplicateFlight);
                    if (!confirmed) {
                        System.out.println("Flight addition cancelled by user.");
                        return; // User chose not to proceed
                    }
                }
            }
            
            // Generate new ID
            int maxId = 0;
            for (Flight flight : flightBookingSystem.getFlights()) {
                if (flight.getId() > maxId) {
                    maxId = flight.getId();
                }
            }
            
            // Create flight
            Flight flight = new Flight(maxId + 1, flightNumber, origin, destination, 
                                      departureDate, capacity, price);
            flightBookingSystem.addFlight(flight);
            
            // Save to file storage
            try {
                FlightBookingSystemData.store(flightBookingSystem);
            } catch (IOException ex) {
                flightBookingSystem.getFlights().remove(flight);
                throw new FlightBookingSystemException("Error saving flight. Changes have been rolled back: " + ex.getMessage());
            }
            
            System.out.println("Flight #" + flight.getId() + " added successfully.");
            
        } catch (IOException | DateTimeParseException ex) {
            throw new FlightBookingSystemException("Error reading input: " + ex.getMessage());
        }
    }
    
    /**
     * Asks user for confirmation to add duplicate flight (CLI mode).
     * 
     * @param existingFlight the existing flight with same details
     * @return true if user confirms, false otherwise
     * @throws IOException if input reading fails
     */
    private boolean confirmDuplicateFlight(Flight existingFlight) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.println("\n⚠ WARNING: A flight with the same details already exists:");
        System.out.println("  Flight #" + existingFlight.getId());
        System.out.println("  Flight Number: " + existingFlight.getFlightNumber());
        System.out.println("  Route: " + existingFlight.getOrigin() + " → " + existingFlight.getDestination());
        System.out.println("  Departure Date: " + existingFlight.getDepartureDate());
        System.out.println("  Capacity: " + existingFlight.getCapacity());
        System.out.println("  Base Price: £" + existingFlight.getPrice());
        System.out.println();
        System.out.print("Do you want to add this flight anyway? (yes/no): ");
        System.out.flush();
        
        String response = reader.readLine().trim().toLowerCase();
        return response.equals("yes") || response.equals("y");
    }
    
    /**
     * Prompts user for flight details (CLI mode).
     * FIXED: Proper output flushing to ensure prompts appear correctly.
     * 
     * @throws IOException if input reading fails
     */
    private void promptForInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Flight Number: ");
        System.out.flush(); // FIXED: Force output
        this.flightNumber = reader.readLine();
        
        System.out.print("Origin: ");
        System.out.flush(); // FIXED: Force output
        this.origin = reader.readLine();
        
        System.out.print("Destination: ");
        System.out.flush(); // FIXED: Force output
        this.destination = reader.readLine();
        
        System.out.print("Departure Date (YYYY-MM-DD): ");
        System.out.flush(); // FIXED: Force output
        String departureDateStr = reader.readLine();
        this.departureDate = LocalDate.parse(departureDateStr);
        
        System.out.print("Capacity: ");
        System.out.flush(); // FIXED: Force output
        this.capacity = Integer.parseInt(reader.readLine());
        
        System.out.print("Price (£): ");
        System.out.flush(); // FIXED: Force output
        this.price = Double.parseDouble(reader.readLine());
    }
}
