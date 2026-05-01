package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to delete a flight from the system.
 * Handles deletion of flights and automatic cancellation of associated bookings.
 * Validates that the flight hasn't departed and manages rollback on errors.
 * 
 * @author M9
 */
public class DeleteFlight implements Command {
    
    private final int flightId;
    
    /**
     * Constructs a DeleteFlight command.
     * 
     * @param flightId the ID of the flight to delete
     */
    public DeleteFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Executes the flight deletion command.
     * Validates the flight exists and hasn't departed, cancels associated bookings,
     * and marks the flight as deleted. Includes rollback mechanism on save failure.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if flight not found, already deleted, departed, or save fails
     */
    /**
     * Executes the command.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if execution fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        LocalDate systemDate = flightBookingSystem.getSystemDate();
        
        if (flight == null) {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " not found.");
        }
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Flight has already been deleted.");
        }
        
        checkForOutboundDeparture(flightBookingSystem, flight, systemDate);
        
        if (flight.hasDeparted(systemDate)) {
            throw new FlightBookingSystemException("Flight has already departed");
        }
        
        List<BookingCancellationInfo> cancellations = processFlightDeletionBookings(flightBookingSystem, flight, systemDate);
        
        flight.setDeleted(true);
        
        try {
            FlightBookingSystemData.store(flightBookingSystem);
        } catch (IOException ex) {
            flight.setDeleted(false);
            rollbackCancellations(cancellations);
            throw new FlightBookingSystemException("Error saving deletion. Changes have been rolled back: " + ex.getMessage());
        }
        
        System.out.println("Flight deleted successfully!");
        if (!cancellations.isEmpty()) {
            System.out.println("\nAffected bookings have been cancelled and refunds processed:");
            for (BookingCancellationInfo info : cancellations) {
                System.out.println(info.getMessage());
            }
        }
    }
    
    /**
     * Checks if the flight being deleted is an outbound flight with return bookings.
     * Prevents deletion if customers have booked return flights dependent on this outbound.
     * 
     * @param fbs the flight booking system
     * @param deletedFlight the flight being deleted
     * @param systemDate the current system date
     * @throws FlightBookingSystemException if flight is part of a return booking that hasn't departed
     */
    private void checkForOutboundDeparture(FlightBookingSystem fbs, Flight deletedFlight, LocalDate systemDate) throws FlightBookingSystemException {
        for (Customer customer : fbs.getCustomers()) {
            if (customer.isDeleted()) continue;
            
            for (Booking booking : customer.getBookings()) {
                if (booking.isCancelled()) continue;
                
                if (booking.isRoundTrip()) {
                    Flight outbound = booking.getOutboundFlight();
                    if (outbound.getId() == deletedFlight.getId() && outbound.hasDeparted(systemDate)) {
                        throw new FlightBookingSystemException("Flight has already departed");
                    }
                }
            }
        }
    }
    
    /**
     * Processes all bookings affected by the flight deletion.
     * Cancels bookings for the deleted flight and handles return flight scenarios.
     * 
     * @param fbs the flight booking system
     * @param deletedFlight the flight being deleted
     * @param systemDate the current system date
     * @return list of cancellation information for rollback purposes
     */
    private List<BookingCancellationInfo> processFlightDeletionBookings(
            FlightBookingSystem fbs, Flight deletedFlight, LocalDate systemDate) {
        
        List<BookingCancellationInfo> cancellations = new ArrayList<>();
        
        for (Customer customer : fbs.getCustomers()) {
            if (customer.isDeleted()) continue;
            
            for (Booking booking : customer.getBookings()) {
                if (booking.isCancelled()) continue;
                
                BookingCancellationInfo cancellation = processBooking(booking, deletedFlight, systemDate);
                if (cancellation != null) {
                    cancellations.add(cancellation);
                }
            }
        }
        
        return cancellations;
    }
    
    /**
     * Processes a single booking affected by flight deletion.
     * Determines cancellation type and calculates refunds based on booking configuration.
     * 
     * @param booking the booking to process
     * @param deletedFlight the flight being deleted
     * @param systemDate the current system date
     * @return cancellation information if booking was affected, null otherwise
     */
    private BookingCancellationInfo processBooking(Booking booking, Flight deletedFlight, LocalDate systemDate) {
        Flight outbound = booking.getOutboundFlight();
        Flight returnFlight = booking.getReturnFlight();
        boolean isRoundTrip = booking.isRoundTrip();
        
        double originalPrice = booking.getPrice();
        double originalFee = booking.getCancellationFee();
        Booking.BookingStatus originalStatus = booking.getStatus();
        LocalDate originalActionDate = booking.getActionDate();
        
        if (!isRoundTrip) {
            if (outbound.getId() == deletedFlight.getId()) {
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                
                // For rebooked flights, customer paid price + rebook fee (15)
                // To refund both, set cancellationFee to negative rebook fee
                if (originalStatus == Booking.BookingStatus.REBOOKED && originalFee > 0) {
                    booking.setCancellationFee(-originalFee);  // Negative fee adds to refund
                } else {
                    booking.setCancellationFee(0.0);
                }
                booking.setActionDate(systemDate);
                
                double refundAmount = booking.getRefundAmount();
                return new BookingCancellationInfo(
                    booking, originalPrice, originalFee, originalStatus, originalActionDate,
                    String.format("  - Customer %s: One-way booking cancelled. Full refund: £%.2f",
                        booking.getCustomer().getName(), refundAmount)
                );
            }
        } else {
            if (outbound.getId() == deletedFlight.getId()) {
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                
                // For rebooked flights, customer paid price + rebook fee (15)
                // To refund both, set cancellationFee to negative rebook fee
                if (originalStatus == Booking.BookingStatus.REBOOKED && originalFee > 0) {
                    booking.setCancellationFee(-originalFee);  // Negative fee adds to refund
                } else {
                    booking.setCancellationFee(0.0);
                }
                booking.setActionDate(systemDate);
                
                double refundAmount = booking.getRefundAmount();
                return new BookingCancellationInfo(
                    booking, originalPrice, originalFee, originalStatus, originalActionDate,
                    String.format("  - Customer %s: Round-trip cancelled (outbound deleted). Full refund: £%.2f",
                        booking.getCustomer().getName(), refundAmount)
                );
            } else if (returnFlight != null && returnFlight.getId() == deletedFlight.getId()) {
                double returnFlightPrice = calculateReturnFlightPrice(booking);
                double outboundFlightPrice = originalPrice - returnFlightPrice;
                
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                booking.setPrice(originalPrice);
                booking.setCancellationFee(outboundFlightPrice);
                booking.setPartialCancellation(true); // Mark as partial cancellation for display
                booking.setActionDate(systemDate);
                
                return new BookingCancellationInfo(
                    booking, originalPrice, originalFee, originalStatus, originalActionDate,
                    String.format("  - Customer %s: Round-trip cancelled (return deleted). Partial refund: £%.2f (New amount paid: £%.2f)",
                        booking.getCustomer().getName(), returnFlightPrice, outboundFlightPrice)
                );
            }
        }
        
        return null;
    }
    
    private double calculateReturnFlightPrice(Booking booking) {
        Flight returnFlight = booking.getReturnFlight();
        Flight outboundFlight = booking.getOutboundFlight();
        
        double outboundPrice = calculateFlightPrice(outboundFlight, booking.getBookingDate());
        double returnPrice = calculateFlightPrice(returnFlight, booking.getBookingDate());
        
        double totalCalculated = outboundPrice + returnPrice;
        if (totalCalculated > 0) {
            return booking.getPrice() * (returnPrice / totalCalculated);
        }
        
        return booking.getPrice() / 2.0;
    }
    
    private double calculateFlightPrice(Flight flight, LocalDate bookingDate) {
        double basePrice = flight.getPrice();
        LocalDate departureDate = flight.getDepartureDate();
        
        long daysUntilDeparture = java.time.temporal.ChronoUnit.DAYS.between(bookingDate, departureDate);
        
        double urgencyFactor = 0.0;
        if (daysUntilDeparture <= 7) {
            urgencyFactor = 0.5;
        } else if (daysUntilDeparture <= 14) {
            urgencyFactor = 0.3;
        } else if (daysUntilDeparture < 30) {
            urgencyFactor = 0.15;
        }
        
        int capacity = flight.getCapacity();
        int occupied = flight.getPassengers().size();
        double occupancyRate = (double) occupied / capacity;
        double capacityFactor = occupancyRate * 0.4;
        
        return basePrice * (1 + urgencyFactor + capacityFactor);
    }
    
    /**
     * Rolls back all booking cancellations in case of save failure.
     * Restores bookings to their original state before deletion attempt.
     * 
     * @param cancellations list of cancellations to rollback
     */
    private void rollbackCancellations(List<BookingCancellationInfo> cancellations) {
        for (BookingCancellationInfo info : cancellations) {
            info.rollback();
        }
    }
    
    /**
     * Inner class to track booking cancellation information for rollback purposes.
     * Stores original booking state to enable restoration if deletion fails.
     */
    private static class BookingCancellationInfo {
        private final Booking booking;
        private final double originalPrice;
        private final double originalFee;
        private final Booking.BookingStatus originalStatus;
        private final LocalDate originalActionDate;
        private final String message;
        
        public BookingCancellationInfo(Booking booking, double originalPrice, double originalFee,
                                      Booking.BookingStatus originalStatus, LocalDate originalActionDate,
                                      String message) {
            this.booking = booking;
            this.originalPrice = originalPrice;
            this.originalFee = originalFee;
            this.originalStatus = originalStatus;
            this.originalActionDate = originalActionDate;
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void rollback() {
            booking.setPrice(originalPrice);
            booking.setCancellationFee(originalFee);
            booking.setStatus(originalStatus);
            booking.setActionDate(originalActionDate);
        }
    }
}
