package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

/**
 * Command to cancel an existing booking.
 * 
 * @author M9
 */

public class CancelBooking implements Command {
    
    private final int customerId;
    private final int outboundFlightId;
    private final Integer returnFlightId; // Optional for round trip cancellation
    private static final double CANCELLATION_FEE = 25.0;
    
    /**
     * Constructs a CancelBooking command for one-way booking.
     * 
     * @param customerId the customer ID
     * @param outboundFlightId the outbound flight ID
     */
    public CancelBooking(int customerId, int outboundFlightId) {
        this(customerId, outboundFlightId, null);
    }
    
    /**
     * Constructs a CancelBooking command.
     * 
     * @param customerId the customer ID
     * @param outboundFlightId the outbound flight ID
     * @param returnFlightId the return flight ID (null for one-way)
     */
    public CancelBooking(int customerId, int outboundFlightId, Integer returnFlightId) {
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
        // Check if customer is trying to cancel another customer's booking (security check)
        if (flightBookingSystem.getAuthService().isCustomer()) {
            int loggedInCustomerId = flightBookingSystem.getAuthService().getCustomerId();
            if (customerId != loggedInCustomerId) {
                throw new FlightBookingSystemException("Access denied: You can only cancel your own bookings.");
            }
        }
        
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight outboundFlight = flightBookingSystem.getFlightByID(outboundFlightId);
        
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }
        if (outboundFlight == null) {
            throw new FlightBookingSystemException("Flight with ID " + outboundFlightId + " not found.");
        }
        
        // Find the booking
        Booking bookingToCancel = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getOutboundFlight().getId() == outboundFlightId && !booking.isCancelled()) {
                bookingToCancel = booking;
                break;
            }
        }
        
        if (bookingToCancel == null) {
            // Check if the provided flight ID is actually a return flight in a round trip
            for (Booking booking : customer.getBookings()) {
                if (!booking.isCancelled() && booking.isRoundTrip() && 
                    booking.getReturnFlight().getId() == outboundFlightId) {
                    throw new FlightBookingSystemException("Flight #" + outboundFlightId + " is the return flight of a round-trip booking. " +
                        "To cancel this round-trip booking, please provide both outbound flight ID #" + 
                        booking.getOutboundFlight().getId() + " and return flight ID #" + outboundFlightId + ".");
                }
            }
            throw new FlightBookingSystemException("No active booking found for flight #" + outboundFlightId + ".");
        }
        
        // Check if booking is a round trip
        boolean isRoundTrip = bookingToCancel.isRoundTrip();
        
        // Validate round trip cancellation
        if (isRoundTrip) {
            if (returnFlightId == null) {
                throw new FlightBookingSystemException("This is a round-trip booking. Please provide both outbound and return flight IDs to cancel.");
            }
            
            if (bookingToCancel.getReturnFlight().getId() != returnFlightId) {
                throw new FlightBookingSystemException("Return flight ID mismatch. Expected flight #" + 
                    bookingToCancel.getReturnFlight().getId() + " but got #" + returnFlightId + ".");
            }
            
            // Check if outbound flight has departed
            if (outboundFlight.hasDeparted(flightBookingSystem.getSystemDate())) {
                throw new FlightBookingSystemException("Cancellation of round trip is not possible on or after the departure date of the outbound flight.");
            }
        } else {
            // One-way booking
            if (returnFlightId != null) {
                throw new FlightBookingSystemException("This is a one-way booking. Please provide only the outbound flight ID.");
            }
            
            // Check if flight has departed
            if (outboundFlight.hasDeparted(flightBookingSystem.getSystemDate())) {
                throw new FlightBookingSystemException("The flight has already departed.");
            }
        }
        
        // Check deletion status
        if (outboundFlight.isDeleted()) {
            throw new FlightBookingSystemException("Outbound flight has been deleted.");
        }
        
        if (isRoundTrip && bookingToCancel.getReturnFlight().isDeleted()) {
            throw new FlightBookingSystemException("Return flight has been deleted.");
        }
        
        // Calculate cancellation fee (double for round trip)
        double fee = isRoundTrip ? (CANCELLATION_FEE * 2) : CANCELLATION_FEE;
        
        // Apply cancellation fee and update status
        bookingToCancel.setCancellationFee(fee);
        bookingToCancel.setStatus(Booking.BookingStatus.CANCELLED);
        bookingToCancel.setActionDate(flightBookingSystem.getSystemDate());
        
        // Remove passenger from both flights
        bookingToCancel.getOutboundFlight().removePassenger(customer);
        if (bookingToCancel.getReturnFlight() != null) {
            bookingToCancel.getReturnFlight().removePassenger(customer);
        }
        
        // Save changes to file storage
        try {
            FlightBookingSystemData.store(flightBookingSystem);
        } catch (IOException ex) {
            // Rollback changes
            bookingToCancel.setStatus(Booking.BookingStatus.BOOKED);
            bookingToCancel.setCancellationFee(0.0);
            try {
                bookingToCancel.getOutboundFlight().addPassenger(customer);
                if (bookingToCancel.getReturnFlight() != null) {
                    bookingToCancel.getReturnFlight().addPassenger(customer);
                }
            } catch (FlightBookingSystemException ignored) {}
            throw new FlightBookingSystemException("Error saving cancellation. Changes have been rolled back: " + ex.getMessage());
        }
        
        double refund = bookingToCancel.getRefundAmount();
        System.out.println("Booking cancelled successfully!");
        System.out.println("Type: " + (isRoundTrip ? "Round-trip" : "One-way"));
        System.out.println("Cancellation fee: £" + String.format("%.2f", fee));
        System.out.println("Refund amount: £" + String.format("%.2f", refund));
    }
}
