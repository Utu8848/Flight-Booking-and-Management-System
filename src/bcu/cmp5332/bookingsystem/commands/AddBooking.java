package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

/**
 * Command to create a new flight booking for a customer.
 * 
 * @author M9
 */

public class AddBooking implements Command {
    
    private final int customerId;
    private final int outboundFlightId;
    private final Integer returnFlightId; // Optional - null for one-way
    
    /**
     * Constructs a one-way booking command.
     * 
     * @param customerId the customer ID
     * @param outboundFlightId the outbound flight ID
     */
    public AddBooking(int customerId, int outboundFlightId) {
        this(customerId, outboundFlightId, null);
    }
    
    /**
     * Constructs a booking command (one-way or round-trip).
     * 
     * @param customerId the customer ID
     * @param outboundFlightId the outbound flight ID
     * @param returnFlightId the return flight ID (null for one-way)
     */
    public AddBooking(int customerId, int outboundFlightId, Integer returnFlightId) {
        this.customerId = customerId;
        this.outboundFlightId = outboundFlightId;
        this.returnFlightId = returnFlightId;
    }

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Check if customer is trying to book for another customer (security check)
        if (flightBookingSystem.getAuthService().isCustomer()) {
            int loggedInCustomerId = flightBookingSystem.getAuthService().getCustomerId();
            if (customerId != loggedInCustomerId) {
                throw new FlightBookingSystemException("Access denied: You can only make bookings for yourself.");
            }
        }
        
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight outboundFlight = flightBookingSystem.getFlightByID(outboundFlightId);
        Flight returnFlight = (returnFlightId != null) ? flightBookingSystem.getFlightByID(returnFlightId) : null;
        
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }
        if (outboundFlight == null) {
            throw new FlightBookingSystemException("Flight with ID " + outboundFlightId + " not found.");
        }
        if (returnFlightId != null && returnFlight == null) {
            throw new FlightBookingSystemException("Return flight with ID " + returnFlightId + " not found.");
        }
        if (customer.isDeleted()) {
            throw new FlightBookingSystemException("Customer has been deleted.");
        }
        
        // Check deletion status FIRST
        if (outboundFlight.isDeleted()) {
            throw new FlightBookingSystemException("Outbound flight has been deleted.");
        }
        if (returnFlight != null && returnFlight.isDeleted()) {
            throw new FlightBookingSystemException("Return flight has been deleted.");
        }
        
        // Check departure status SECOND
        if (outboundFlight.hasDeparted(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("The outbound flight has already departed.");
        }
        if (returnFlight != null && returnFlight.hasDeparted(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("The return flight has already departed.");
        }
        
        // COMPREHENSIVE DUPLICATE BOOKING CHECK - THIRD
        // Check if customer already has any booking containing the new outbound or return flights
        // A flight can appear as either outbound OR return in existing bookings
        for (Booking existingBooking : customer.getBookings()) {
            if (existingBooking.isCancelled()) {
                continue; // Skip cancelled bookings
            }
            
            // Check if new outbound flight matches ANY flight in existing booking
            if (existingBooking.getOutboundFlight().getId() == outboundFlightId) {
                throw new FlightBookingSystemException(
                    "You already have an active booking for flight #" + outboundFlightId + " (currently booked as outbound flight).");
            }
            
            if (existingBooking.getReturnFlight() != null && 
                existingBooking.getReturnFlight().getId() == outboundFlightId) {
                throw new FlightBookingSystemException(
                    "You already have an active booking for flight #" + outboundFlightId + " (currently booked as return flight).");
            }
            
            // Check if new return flight matches ANY flight in existing booking (if return flight provided)
            if (returnFlightId != null) {
                if (existingBooking.getOutboundFlight().getId() == returnFlightId) {
                    throw new FlightBookingSystemException(
                        "You already have an active booking for flight #" + returnFlightId + " (currently booked as outbound flight).");
                }
                
                if (existingBooking.getReturnFlight() != null && 
                    existingBooking.getReturnFlight().getId() == returnFlightId) {
                    throw new FlightBookingSystemException(
                        "You already have an active booking for flight #" + returnFlightId + " (currently booked as return flight).");
                }
            }
        }
        
        // Validate round-trip route logic FOURTH (only after checking deleted/departed/duplicates)
        if (returnFlight != null) {
            String outboundOrigin = outboundFlight.getOrigin();
            String outboundDestination = outboundFlight.getDestination();
            String returnOrigin = returnFlight.getOrigin();
            String returnDestination = returnFlight.getDestination();
            
            if (!outboundDestination.equalsIgnoreCase(returnOrigin)) {
                throw new FlightBookingSystemException(
                    String.format("Invalid round-trip route: Outbound destination '%s' must match return origin '%s'.",
                        outboundDestination, returnOrigin));
            }
            
            if (!outboundOrigin.equalsIgnoreCase(returnDestination)) {
                throw new FlightBookingSystemException(
                    String.format("Invalid round-trip route: Outbound origin '%s' must match return destination '%s'.",
                        outboundOrigin, returnDestination));
            }
            
            // Validate return flight departure date is on or after outbound flight departure date
            if (returnFlight.getDepartureDate().isBefore(outboundFlight.getDepartureDate())) {
                throw new FlightBookingSystemException(
                    String.format("Invalid round-trip booking: Return flight cannot depart before outbound flight. " +
                        "Outbound departs on %s, but return departs on %s.",
                        outboundFlight.getDepartureDate(), returnFlight.getDepartureDate()));
            }
        }
        
        // Create booking with dynamic pricing
        Booking booking = new Booking(customer, outboundFlight, returnFlight, flightBookingSystem.getSystemDate());
        
        // Add passengers to flights (this checks capacity)
        outboundFlight.addPassenger(customer);
        if (returnFlight != null) {
            try {
                returnFlight.addPassenger(customer);
            } catch (FlightBookingSystemException ex) {
                // Rollback outbound if return fails
                outboundFlight.removePassenger(customer);
                throw ex;
            }
        }
        
        // Add booking to customer
        customer.addBooking(booking);
        
        // Save changes to file storage
        try {
            FlightBookingSystemData.store(flightBookingSystem);
        } catch (IOException ex) {
            // Rollback changes
            outboundFlight.removePassenger(customer);
            if (returnFlight != null) {
                returnFlight.removePassenger(customer);
            }
            customer.cancelBooking(booking);
            throw new FlightBookingSystemException("Error saving booking. Changes have been rolled back: " + ex.getMessage());
        }
        
        System.out.println("Booking added successfully!");
        System.out.println("Type: " + (returnFlight != null ? "Round-trip" : "One-way"));
        System.out.println("Price: £" + String.format("%.2f", booking.getPrice()));
    }
}
