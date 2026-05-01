package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a flight in the booking system.
 * Manages flight details, passengers, and capacity.
 * 
 * @author M9
 */
public class Flight {
    
    /** Id field */
    private int id;
    /** Flightnumber field */
    private String flightNumber;
    /** Origin field */
    private String origin;
    /** Destination field */
    private String destination;
    /** Departuredate field */
    private LocalDate departureDate;
    /** Capacity field */
    private int capacity;
    /** Price field */
    private double price;
    /** Deleted field */
    private boolean deleted;

    /** Passengers field */
    private final Set<Customer> passengers;

    /**
     * Constructs a Flight with specified details.
     * 
     * @param id unique flight identifier
     * @param flightNumber the flight number
     * @param origin departure city
     * @param destination arrival city
     * @param departureDate date of departure
     * @param capacity maximum number of passengers
     * @param price base price for the flight
     */
    public Flight(int id, String flightNumber, String origin, String destination, 
                  LocalDate departureDate, int capacity, double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
        this.deleted = false;
        passengers = new HashSet<>();
    }
    
    /**
     * Constructs a Flight with default capacity and price.
     * 
     * @param id unique flight identifier
     * @param flightNumber the flight number
     * @param origin departure city
     * @param destination arrival city
     * @param departureDate date of departure
     */
    public Flight(int id, String flightNumber, String origin, String destination, LocalDate departureDate) {
        this(id, flightNumber, origin, destination, departureDate, 100, 150.0);
    }

    /**
     * Gets flight ID.
     * @return the flight ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets flight ID.
     * @param id the new flight ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets flight number.
     * @return the flight number
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * Sets flight number.
     * @param flightNumber the new flight number
     */
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    /**
     * Gets origin city.
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }
    
    /**
     * Sets origin city.
     * @param origin the new origin
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Gets destination city.
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets destination city.
     * @param destination the new destination
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Gets departure date.
     * @return the departure date
     */
    public LocalDate getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets departure date.
     * @param departureDate the new departure date
     */
    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }
    
    /**
     * Gets flight capacity.
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Sets flight capacity.
     * @param capacity the new capacity
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    /**
     * Gets base flight price.
     * @return the price
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Sets base flight price.
     * @param price the new price
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Checks if flight is deleted.
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
     * Gets list of passengers.
     * Returns an immutable copy to prevent external modification.
     * @return unmodifiable list of passengers
     */
    public List<Customer> getPassengers() {
        return java.util.Collections.unmodifiableList(new ArrayList<>(passengers));
    }
	
    /**
     * Gets short flight details.
     * @return formatted flight details
     */
    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int available = capacity - passengers.size();
        return "Flight #" + id + " - " + flightNumber + " - " + origin + " to " 
                + destination + " on " + departureDate.format(dtf) + 
                " [" + available + "/" + capacity + " seats] - £" + String.format("%.2f", price);
    }

    /**
     * Gets detailed flight information without passenger list.
     * Used for customer-facing views where passenger privacy is important.
     * @return detailed flight information without passenger list
     */
    public String getDetailsLongNoPassengers() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("Flight Details:\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Flight Number: ").append(flightNumber).append("\n");
        sb.append("Origin: ").append(origin).append("\n");
        sb.append("Destination: ").append(destination).append("\n");
        sb.append("Departure Date: ").append(departureDate.format(dtf)).append("\n");
        sb.append("Capacity: ").append(capacity).append("\n");
        sb.append("Passengers: ").append(passengers.size()).append("\n");
        sb.append("Available Seats: ").append(capacity - passengers.size()).append("\n");
        sb.append("Price: £").append(String.format("%.2f", price)).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Gets detailed flight information.
     * @return detailed flight information
     */
    public String getDetailsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("Flight Details:\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Flight Number: ").append(flightNumber).append("\n");
        sb.append("Origin: ").append(origin).append("\n");
        sb.append("Destination: ").append(destination).append("\n");
        sb.append("Departure Date: ").append(departureDate.format(dtf)).append("\n");
        sb.append("Capacity: ").append(capacity).append("\n");
        sb.append("Passengers: ").append(passengers.size()).append("\n");
        sb.append("Available Seats: ").append(capacity - passengers.size()).append("\n");
        sb.append("Price: £").append(String.format("%.2f", price)).append("\n");
        sb.append("\nPassenger List:\n");
        
        if (passengers.isEmpty()) {
            sb.append("No passengers booked.\n");
        } else {
            for (Customer passenger : passengers) {
                sb.append("  - ").append(passenger.getDetailsShort()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Adds a passenger to the flight.
     * 
     * @param passenger the passenger to add
     * @throws FlightBookingSystemException if flight is at full capacity
     */
    public void addPassenger(Customer passenger) throws FlightBookingSystemException {
        if (passengers.size() >= capacity) {
            throw new FlightBookingSystemException("Flight is at full capacity. Cannot add more passengers.");
        }
        passengers.add(passenger);
    }
    
    /**
     * Removes a passenger from the flight.
     * 
     * @param passenger the passenger to remove
     */
    public void removePassenger(Customer passenger) {
        passengers.remove(passenger);
    }
    
    /**
     * Checks if flight has departed based on system date.
     * 
     * @param systemDate the current system date
     * @return true if flight has departed, false otherwise
     */
    public boolean hasDeparted(LocalDate systemDate) {
        return departureDate.isBefore(systemDate);
    }
    
    /**
     * Calculates dynamic pricing for this flight based on booking date, days until departure, and capacity.
     * The price increases as:
     * - The departure date approaches (urgency factor)
     * - The flight fills up (capacity factor)
     * 
     * @param bookingDate the date when booking is being made
     * @return the dynamically calculated price
     */
    public double calculateDynamicPrice(LocalDate bookingDate) {
        // Calculate days until departure
        long daysUntilDeparture = java.time.temporal.ChronoUnit.DAYS.between(bookingDate, departureDate);
        
        // Urgency factor: price increases as departure approaches
        double urgencyFactor = 0.0;
        if (daysUntilDeparture <= 7) {
            urgencyFactor = 0.5; // 50% increase for last week
        } else if (daysUntilDeparture <= 14) {
            urgencyFactor = 0.3; // 30% increase for 2 weeks
        } else if (daysUntilDeparture <= 30) {
            urgencyFactor = 0.15; // 15% increase for a month
        }
        
        // Capacity factor: price increases as seats fill up
        int occupied = passengers.size();
        double occupancyRate = (double) occupied / capacity;
        double capacityFactor = occupancyRate * 0.4; // Up to 40% increase when nearly full
        
        return price * (1 + urgencyFactor + capacityFactor);
    }
}

