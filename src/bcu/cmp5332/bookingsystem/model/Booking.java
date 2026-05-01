package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a flight booking in the system.
 * Links customers to flights and tracks booking details including pricing and fees.
 * 
 * @author M9
 */
public class Booking {
    
    /**
     * Enum representing the status of a booking.
     */
    public enum BookingStatus {
        /** Initial booking. */
        BOOKED,
        /** Booking was cancelled. */
        CANCELLED,
        /** Booking was changed to a different flight. */
        REBOOKED
    }
    
    /** Customer field */
    private Customer customer;
    /** Outboundflight field */
    private Flight outboundFlight;
    /** Returnflight field */
    private Flight returnFlight; // Optional - can be null for one-way bookings
    /** Bookingdate field */
    private LocalDate bookingDate;
    /** Price field */
    private double price;
    /** Cancellationfee field */
    private double cancellationFee;
    /** Status field */
    private BookingStatus status;
    /** Actiondate field */
    private LocalDate actionDate; // Date when last action (booking/cancellation/rebooking) was performed
    /** Ispartialcancellation field */
    private boolean isPartialCancellation; // True if only return flight was cancelled in a round-trip

    /**
     * Constructs a new one-way Booking.
     * 
     * @param customer the customer making the booking
     * @param outboundFlight the outbound flight being booked
     * @param bookingDate the date of booking
     */
    public Booking(Customer customer, Flight outboundFlight, LocalDate bookingDate) {
        this(customer, outboundFlight, null, bookingDate);
    }
    
    /**
     * Constructs a new Booking (one-way or round-trip).
     * 
     * @param customer the customer making the booking
     * @param outboundFlight the outbound flight being booked
     * @param returnFlight the return flight (null for one-way)
     * @param bookingDate the date of booking
     */
    public Booking(Customer customer, Flight outboundFlight, Flight returnFlight, LocalDate bookingDate) {
        this.customer = customer;
        this.outboundFlight = outboundFlight;
        this.returnFlight = returnFlight;
        this.bookingDate = bookingDate;
        this.status = BookingStatus.BOOKED;
        this.actionDate = bookingDate;
        this.cancellationFee = 0.0;
        this.isPartialCancellation = false;
        // Calculate dynamic price based on booking date and flight details
        this.price = calculateDynamicPrice();
    }
    
    /**
     * Calculates dynamic pricing based on urgency and capacity.
     * @return the calculated price
     */
    private double calculateDynamicPrice() {
        double totalPrice = calculateFlightPrice(outboundFlight);
        
        // Add return flight price if present
        if (returnFlight != null) {
            totalPrice += calculateFlightPrice(returnFlight);
        }
        
        return totalPrice;
    }
    
    /**
     * Calculates price for a single flight based on urgency and capacity.
     * @param flight the flight to calculate price for
     * @return the calculated price for this flight
     */
    private double calculateFlightPrice(Flight flight) {
        double basePrice = flight.getPrice();
        LocalDate departureDate = flight.getDepartureDate();
        
        // Calculate days until departure
        long daysUntilDeparture = java.time.temporal.ChronoUnit.DAYS.between(bookingDate, departureDate);
        
        // Urgency factor: price increases as departure approaches
        double urgencyFactor = 0.0;
        if (daysUntilDeparture <= 7) {
            urgencyFactor = 0.5; // 50% increase for last week
        } else if (daysUntilDeparture <= 14) {
            urgencyFactor = 0.3; // 30% increase for 2 weeks
        } else if (daysUntilDeparture < 30) {
            urgencyFactor = 0.15; // 15% increase for less than a month
        }
        
        // Capacity factor: price increases as seats fill up
        int capacity = flight.getCapacity();
        int occupied = flight.getPassengers().size();
        double occupancyRate = (double) occupied / capacity;
        double capacityFactor = occupancyRate * 0.4; // Up to 40% increase when nearly full
        
        return basePrice * (1 + urgencyFactor + capacityFactor);
    }
    
    /**
     * Gets the customer.
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }
    
    /**
     * Sets the customer.
     * @param customer the new customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    /**
     * Gets the outbound flight.
     * @return the outbound flight
     */
    public Flight getOutboundFlight() {
        return outboundFlight;
    }
    
    /**
     * Sets the outbound flight.
     * @param outboundFlight the new outbound flight
     */
    public void setOutboundFlight(Flight outboundFlight) {
        this.outboundFlight = outboundFlight;
    }
    
    /**
     * Gets the return flight.
     * @return the return flight (null if one-way)
     */
    public Flight getReturnFlight() {
        return returnFlight;
    }
    
    /**
     * Sets the return flight.
     * @param returnFlight the new return flight
     */
    public void setReturnFlight(Flight returnFlight) {
        this.returnFlight = returnFlight;
    }
    
    /**
     * Gets the primary flight (outbound).
     * @return the outbound flight
     * @deprecated Use getOutboundFlight() instead
     */
    @Deprecated
    public Flight getFlight() {
        return outboundFlight;
    }
    
    /**
     * Sets the primary flight (outbound).
     * @param flight the new outbound flight
     * @deprecated Use setOutboundFlight() instead
     */
    @Deprecated
    public void setFlight(Flight flight) {
        this.outboundFlight = flight;
    }
    
    /**
     * Checks if this is a round-trip booking.
     * @return true if return flight exists, false otherwise
     */
    public boolean isRoundTrip() {
        return returnFlight != null;
    }
    
    /**
     * Gets the booking date.
     * @return the booking date
     */
    public LocalDate getBookingDate() {
        return bookingDate;
    }
    
    /**
     * Sets the booking date.
     * @param bookingDate the new booking date
     */
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    /**
     * Gets the booking price.
     * @return the price
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Sets the booking price.
     * @param price the new price
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Gets the cancellation fee.
     * @return the cancellation fee
     */
    public double getCancellationFee() {
        return cancellationFee;
    }
    
    /**
     * Sets the cancellation fee.
     * @param cancellationFee the new cancellation fee
     */
    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }
    
    /**
     * Gets the booking status.
     * @return the booking status
     */
    public BookingStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the booking status.
     * @param status the new booking status
     */
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the action date (when the last action was performed).
     * @return the action date
     */
    public LocalDate getActionDate() {
        return actionDate;
    }
    
    /**
     * Sets the action date.
     * @param actionDate the new action date
     */
    public void setActionDate(LocalDate actionDate) {
        this.actionDate = actionDate;
    }
    
    /**
     * Checks if this is a partial cancellation (only return flight cancelled).
     * @return true if partial cancellation, false otherwise
     */
    public boolean isPartialCancellation() {
        return isPartialCancellation;
    }
    
    /**
     * Sets the partial cancellation flag.
     * @param isPartialCancellation the partial cancellation flag
     */
    public void setPartialCancellation(boolean isPartialCancellation) {
        this.isPartialCancellation = isPartialCancellation;
    }
    
    /**
     * Checks if booking is cancelled.
     * @return true if cancelled, false otherwise
     */
    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }
    
    /**
     * Sets cancelled status.
     * @param cancelled the new cancelled status
     * @deprecated Use setStatus(BookingStatus.CANCELLED) instead
     */
    @Deprecated
    public void setCancelled(boolean cancelled) {
        if (cancelled) {
            this.status = BookingStatus.CANCELLED;
        } else {
            this.status = BookingStatus.BOOKED;
        }
    }
    
    /**
     * Calculates the refund amount based on status and fees.
     * @return the refund amount
     */
    public double getRefundAmount() {
        if (status == BookingStatus.CANCELLED) {
            return Math.max(0, price - cancellationFee);
        }
        return 0.0;
    }
    
    /**
     * Calculates the total cost (price + fees).
     * @return the total cost
     */
    public double getTotalCost() {
        return price + cancellationFee;
    }
    
    /**
     * Gets the amount paid based on booking status.
     * 
     * @return the amount paid
     */
    public double getAmountPaid() {
        switch (status) {
            case BOOKED:
                return price;
            case CANCELLED:
                return cancellationFee;
            case REBOOKED:
                return price + cancellationFee;
            default:
                return price;
        }
    }
    
    /**
     * Gets short booking details.
     * @return formatted booking details
     */
    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String statusStr = status == BookingStatus.BOOKED ? "" : " [" + status + "]";
        String flightInfo = outboundFlight.getFlightNumber();
        if (returnFlight != null) {
            flightInfo += " (Round-trip with " + returnFlight.getFlightNumber() + ")";
        }
        return "Booking: " + flightInfo + " on " + bookingDate.format(dtf) + 
               " - £" + String.format("%.2f", price) + statusStr;
    }
    
    /**
     * Gets detailed booking information.
     * @return detailed booking information
     */
    public String getDetailsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("Booking Details:\n");
        sb.append("Customer: ").append(customer.getName()).append(" (ID: ").append(customer.getId()).append(")\n");
        sb.append("Booking Type: ").append(returnFlight != null ? "Round-trip" : "One-way").append("\n");
        sb.append("\nOutbound Flight:\n");
        sb.append("  Flight: ").append(outboundFlight.getFlightNumber()).append("\n");
        sb.append("  Route: ").append(outboundFlight.getOrigin()).append(" -> ").append(outboundFlight.getDestination()).append("\n");
        sb.append("  Departure: ").append(outboundFlight.getDepartureDate().format(dtf)).append("\n");
        
        if (returnFlight != null) {
            sb.append("\nReturn Flight:\n");
            sb.append("  Flight: ").append(returnFlight.getFlightNumber()).append("\n");
            sb.append("  Route: ").append(returnFlight.getOrigin()).append(" -> ").append(returnFlight.getDestination()).append("\n");
            sb.append("  Departure: ").append(returnFlight.getDepartureDate().format(dtf)).append("\n");
        }
        
        sb.append("\nBooking Date: ").append(bookingDate.format(dtf)).append("\n");
        sb.append("Action Date: ").append(actionDate.format(dtf)).append("\n");
        sb.append("Price: £").append(String.format("%.2f", price)).append("\n");
        if (cancellationFee > 0) {
            sb.append("Fee: £").append(String.format("%.2f", cancellationFee)).append("\n");
        }
        sb.append("Total: £").append(String.format("%.2f", getTotalCost())).append("\n");
        if (status == BookingStatus.CANCELLED) {
            sb.append("Refund: £").append(String.format("%.2f", getRefundAmount())).append("\n");
        }
        sb.append("Status: ").append(status).append("\n");
        return sb.toString();
    }
}
