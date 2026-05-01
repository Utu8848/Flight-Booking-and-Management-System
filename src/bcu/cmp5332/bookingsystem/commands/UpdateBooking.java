package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

/**
 * Command to update an existing booking.
 * 
 * @author M9
 */

public class UpdateBooking implements Command {
    
    private final int customerId;
    private final int oldFlightId;
    private final int newFlightId;
    private static final double REBOOKING_FEE = 15.0;
    
    public UpdateBooking(int customerId, int oldFlightId, int newFlightId) {
        this.customerId = customerId;
        this.oldFlightId = oldFlightId;
        this.newFlightId = newFlightId;
    }

    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        boolean isAdmin = flightBookingSystem.getAuthService().isAdmin();
        
        // Check if customer is trying to update another customer's booking (security check)
        if (flightBookingSystem.getAuthService().isCustomer()) {
            int loggedInCustomerId = flightBookingSystem.getAuthService().getCustomerId();
            if (customerId != loggedInCustomerId) {
                throw new FlightBookingSystemException("Access denied: You can only rebook your own bookings.");
            }
        }
        
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight oldFlight = flightBookingSystem.getFlightByID(oldFlightId);
        Flight newFlight = flightBookingSystem.getFlightByID(newFlightId);
        
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }
        if (oldFlight == null) {
            throw new FlightBookingSystemException("Old flight with ID " + oldFlightId + " not found.");
        }
        if (newFlight == null) {
            throw new FlightBookingSystemException("New flight with ID " + newFlightId + " not found.");
        }
        
        // Check if customer is trying to update to the same flight
        if (oldFlightId == newFlightId) {
            String action = isAdmin ? "update" : "rebook";
            throw new FlightBookingSystemException("Cannot " + action + " to the same flight. The new flight ID (" + newFlightId + ") is the same as the current flight ID (" + oldFlightId + ").");
        }
        
        // Find existing booking
        Booking existingBooking = null;
        boolean oldFlightIsReturn = false;
        
        for (Booking booking : customer.getBookings()) {
            if (booking.isCancelled()) {
                continue;
            }
            
            if (booking.getOutboundFlight().getId() == oldFlightId) {
                existingBooking = booking;
                oldFlightIsReturn = false;
                break;
            }
            
            // Also check if old flight ID is the return flight in a round trip
            if (booking.isRoundTrip() && booking.getReturnFlight().getId() == oldFlightId) {
                existingBooking = booking;
                oldFlightIsReturn = true;
                break;
            }
        }
        
        if (existingBooking == null) {
            throw new FlightBookingSystemException("No active booking found for the old flight #" + oldFlightId + ".");
        }
        
        // CRITICAL: Prevent updating round trip bookings (whether old flight is outbound or return)
        if (existingBooking.isRoundTrip()) {
            String flightType = oldFlightIsReturn ? "return" : "outbound";
            throw new FlightBookingSystemException("Flight #" + oldFlightId + " is the " + flightType + 
                " flight of a round-trip booking. Updating of booking is not allowed for round-trip flights. " +
                "Please cancel and rebook if changes are needed.");
        }
        
        // Check old flight deletion status
        if (oldFlight.isDeleted()) {
            throw new FlightBookingSystemException("Old flight has been deleted.");
        }
        
        // Check old flight departure status
        if (oldFlight.hasDeparted(flightBookingSystem.getSystemDate())) {
            String action = isAdmin ? "updated" : "rebooked";
            throw new FlightBookingSystemException("The old flight has already departed and cannot be " + action + ".");
        }
        
        // Check new flight conditions
        if (newFlight.isDeleted()) {
            throw new FlightBookingSystemException("New flight has been deleted.");
        }
        if (newFlight.hasDeparted(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("The new flight has already departed.");
        }
        
        // Check for duplicate: new flight shouldn't be already booked by this customer
        for (Booking otherBooking : customer.getBookings()) {
            if (otherBooking.isCancelled() || otherBooking == existingBooking) {
                continue;
            }
            
            if (otherBooking.getOutboundFlight().getId() == newFlightId) {
                throw new FlightBookingSystemException(
                    "You already have an active booking for flight #" + newFlightId + ".");
            }
            
            if (otherBooking.getReturnFlight() != null && otherBooking.getReturnFlight().getId() == newFlightId) {
                throw new FlightBookingSystemException(
                    "You already have an active booking for flight #" + newFlightId + " (booked as return flight).");
            }
        }
        
        // Now perform the update - simple one-way replacement
        Booking newBooking = new Booking(customer, newFlight, flightBookingSystem.getSystemDate());
        
        // Update flight passenger lists
        oldFlight.removePassenger(customer);
        newFlight.addPassenger(customer);
        
        // Set rebooking properties
        newBooking.setCancellationFee(REBOOKING_FEE);
        newBooking.setStatus(Booking.BookingStatus.REBOOKED);
        newBooking.setActionDate(flightBookingSystem.getSystemDate());
        
        // Mark old booking as cancelled
        existingBooking.setStatus(Booking.BookingStatus.CANCELLED);
        existingBooking.setActionDate(flightBookingSystem.getSystemDate());
        
        // Add new booking to customer
        customer.addBooking(newBooking);
        
        // Save changes
        try {
            FlightBookingSystemData.store(flightBookingSystem);
        } catch (IOException ex) {
            // Rollback all changes
            existingBooking.setStatus(Booking.BookingStatus.BOOKED);
            customer.cancelBooking(newBooking);
            newFlight.removePassenger(customer);
            
            // Restore old passenger
            try {
                oldFlight.addPassenger(customer);
            } catch (FlightBookingSystemException ignored) {}
            
            throw new FlightBookingSystemException("Error saving update. Changes have been rolled back: " + ex.getMessage());
        }
        
        String action = isAdmin ? "updated" : "rebooked";
        System.out.println("Booking " + action + " successfully!");
        System.out.println("New flight price: £" + String.format("%.2f", newBooking.getPrice()));
        System.out.println("Rebooking fee: £" + String.format("%.2f", REBOOKING_FEE));
        System.out.println("Total cost: £" + String.format("%.2f", newBooking.getTotalCost()));
    }
}
