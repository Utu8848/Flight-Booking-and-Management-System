package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the flight booking system.
 * Manages customer details and their bookings.
 * 
 * @author M9
 */
public class Customer {
    
    /** Id field */
    private int id;
    /** Name field */
    private String name;
    /** Phone field */
    private String phone;
    /** Email field */
    private String email;
    /** Deleted field */
    private boolean deleted;
    /** Bookings field */
    private final List<Booking> bookings = new ArrayList<>();
    
    /**
     * Constructs a Customer with specified details.
     * 
     * @param id unique customer identifier
     * @param name customer's full name
     * @param phone customer's phone number
     * @param email customer's email address
     */
    public Customer(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.deleted = false;
    }
    
    /**
     * Gets customer ID.
     * @return the customer ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets customer ID.
     * @param id the new customer ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets customer name.
     * @return the customer name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets customer name.
     * @param name the new customer name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets customer phone number.
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets customer phone number.
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Gets customer email.
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets customer email.
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Checks if customer is deleted.
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Sets deleted status.
     * @param deleted the new deleted status
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    /**
     * Gets list of customer bookings.
     * @return unmodifiable list of bookings
     */
    public List<Booking> getBookings() {
        return java.util.Collections.unmodifiableList(bookings);
    }
    
    /**
     * Adds a booking.
     * @param booking the booking to add
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    /**
     * Cancels a booking.
     * @param booking the booking to cancel
     * @throws FlightBookingSystemException if booking not found
     */
    public void cancelBooking(Booking booking) throws FlightBookingSystemException {
        if (!bookings.remove(booking)) {
            throw new FlightBookingSystemException("Booking not found.");
        }
    }
    
    /**
     * Gets short customer details.
     * @return formatted customer details
     */
    public String getDetailsShort() {
        return "Customer #" + id + " - " + name + " - " + phone + " - " + email;
    }
    
    /**
     * Gets detailed customer information.
     * @return detailed customer information
     */
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder();
        sb.append("Customer Details:\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Phone: ").append(phone).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Status: ").append(deleted ? "Deleted" : "Active").append("\n");
        sb.append("Total Bookings: ").append(bookings.size()).append("\n");
        sb.append("\nBookings:\n");
        
        if (bookings.isEmpty()) {
            sb.append("No bookings found.\n");
        } else {
            for (Booking booking : bookings) {
                sb.append("  - ").append(booking.getDetailsShort()).append("\n");
            }
        }
        
        return sb.toString();
    }
}
