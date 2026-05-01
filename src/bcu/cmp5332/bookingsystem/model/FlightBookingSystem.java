package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Main model class for the Flight Booking System.
 * Manages flights, customers, and system date.
 * 
 * @author M9
 */
public class FlightBookingSystem {
    
    // Nepal timezone (Asia/Kathmandu - UTC+5:45)
    private static final ZoneId NEPAL_ZONE = ZoneId.of("Asia/Kathmandu");
    
    // System date - automatically uses current real-time date in Nepal timezone
    private LocalDate systemDate = LocalDate.now(NEPAL_ZONE);
    
    /** Customers field */
    private final Map<Integer, Customer> customers = new TreeMap<>();
    /** Flights field */
    private final Map<Integer, Flight> flights = new TreeMap<>();
    /** Authservice field */
    private AuthenticationService authService;
    /** Testmode field */
    private boolean testMode; // If true, don't persist to files
    
    /**
     * Constructs a FlightBookingSystem in production mode.
     */
    public FlightBookingSystem() {
        this(false);
    }
    
    /**
     * Constructor with test mode option.
     * @param testMode if true, AuthenticationService won't persist to files
     */
    public FlightBookingSystem(boolean testMode) {
        this.testMode = testMode;
        this.authService = new AuthenticationService(this, testMode);
    }

    /**
     * Gets the current system date.
     * @return the system date
     */
    public LocalDate getSystemDate() {
        return systemDate;
    }
    
    /**
     * Sets the system date.
     * @param systemDate the new system date
     */
    public void setSystemDate(LocalDate systemDate) {
        this.systemDate = systemDate;
    }
    
    /**
     * Checks if the system is in test mode.
     * @return true if in test mode, false otherwise
     */
    public boolean isTestMode() {
        return testMode;
    }
    
    /**
     * Gets the current real-time date in Nepal timezone.
     * This is a utility method to ensure consistent timezone usage across the application.
     * @return current date in Nepal timezone (Asia/Kathmandu)
     */
    public static LocalDate getCurrentNepalDate() {
        return LocalDate.now(NEPAL_ZONE);
    }
    
    /**
     * Gets the Nepal timezone.
     * @return Nepal timezone (Asia/Kathmandu - UTC+5:45)
     */
    public static ZoneId getNepalZone() {
        return NEPAL_ZONE;
    }
    
    /**
     * Gets the authentication service.
     * @return the authentication service
     */
    public AuthenticationService getAuthService() {
        return authService;
    }

    /**
     * Gets all flights in the system.
     * @return unmodifiable list of flights
     */
    public List<Flight> getFlights() {
        List<Flight> out = new ArrayList<>(flights.values());
        return Collections.unmodifiableList(out);
    }

    /**
     * Gets a flight by ID.
     * @param id the flight ID
     * @return the flight, or null if not found
     * @throws FlightBookingSystemException if flight not found
     */
    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        Flight flight = flights.get(id);
        if (flight == null) {
            return null;
        }
        return flight;
    }

    /**
     * Gets all customers in the system.
     * @return unmodifiable list of customers
     */
    public List<Customer> getCustomers() {
        List<Customer> out = new ArrayList<>(customers.values());
        return Collections.unmodifiableList(out);
    }

    /**
     * Gets a customer by ID.
     * @param id the customer ID
     * @return the customer, or null if not found
     * @throws FlightBookingSystemException if customer not found
     */
    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        Customer customer = customers.get(id);
        if (customer == null) {
            return null;
        }
        return customer;
    }

    /**
     * Adds a flight to the system.
     * @param flight the flight to add
     * @throws FlightBookingSystemException if flight already exists
     */
    public void addFlight(Flight flight) throws FlightBookingSystemException {
        if (flights.containsKey(flight.getId())) {
            throw new IllegalArgumentException("Duplicate flight ID.");
        }
        // Note: Flight number uniqueness is now handled in AddFlight command
        // Multiple flights can have the same flight number (different routes/dates)
        // The AddFlight command checks for complete duplicate details and asks for confirmation
        flights.put(flight.getId(), flight);
    }

    /**
     * Adds a customer to the system.
     * Validates that phone and email are unique.
     * @param customer the customer to add
     * @throws FlightBookingSystemException if customer already exists or phone/email is duplicate
     */
    public void addCustomer(Customer customer) throws FlightBookingSystemException {
        if (customers.containsKey(customer.getId())) {
            throw new FlightBookingSystemException("Duplicate customer ID.");
        }
        
        // Check for duplicate phone number
        for (Customer existing : customers.values()) {
            if (!existing.isDeleted() && existing.getPhone().equals(customer.getPhone())) {
                throw new FlightBookingSystemException(
                    "A customer with phone number '" + customer.getPhone() + "' already exists!");
            }
        }
        
        // Check for duplicate email
        for (Customer existing : customers.values()) {
            if (!existing.isDeleted() && existing.getEmail().equalsIgnoreCase(customer.getEmail())) {
                throw new FlightBookingSystemException(
                    "A customer with email '" + customer.getEmail() + "' already exists!");
            }
        }
        
        customers.put(customer.getId(), customer);
    }
}
