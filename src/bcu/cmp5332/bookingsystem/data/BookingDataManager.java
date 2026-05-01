package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Manages persistence of booking data to and from files.
 * 
 * @author M9
 */

public class BookingDataManager implements DataManager {
    
    /** The resource file path for bookings. */
    public final String RESOURCE = "./resources/data/bookings.txt";

    /**
     * Constructs a BookingDataManager.
     */
    public BookingDataManager() {
    }

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        try (Scanner sc = new Scanner(new File(RESOURCE))) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int customerId = Integer.parseInt(properties[0]);
                    int outboundFlightId = Integer.parseInt(properties[1]);
                    Integer returnFlightId = null;
                    int dateIndex = 2; // Default for old format or when parsing fails
                    
                    if (properties.length > 2 && !properties[2].isEmpty() && !properties[2].equals("null")) {
                        try {
                            returnFlightId = Integer.parseInt(properties[2]);
                            dateIndex = 3; // Return flight exists, so booking date is at index 3
                        } catch (NumberFormatException e) {
                            // properties[2] is not a number, it's probably the booking date
                            dateIndex = 2;
                        }
                    } else {
                        // properties[2] is empty or "null", so it's one-way booking
                        dateIndex = 3; // For new format with null placeholder
                    }
                    
                    LocalDate bookingDate = LocalDate.parse(properties[dateIndex]);
                    
                    // Get customer and flights
                    Customer customer = fbs.getCustomerByID(customerId);
                    Flight outboundFlight = fbs.getFlightByID(outboundFlightId);
                    Flight returnFlight = (returnFlightId != null) ? fbs.getFlightByID(returnFlightId) : null;
                    
                    if (customer == null || outboundFlight == null) {
                        System.err.println("Warning: Skipping booking on line " + line_idx + 
                                         " - Customer or Outbound Flight not found");
                        line_idx++;
                        continue;
                    }
                    
                    if (returnFlightId != null && returnFlight == null) {
                        System.err.println("Warning: Return flight " + returnFlightId + " not found on line " + line_idx);
                    }
                    
                    // Create booking
                    Booking booking = new Booking(customer, outboundFlight, returnFlight, bookingDate);
                    
                    // Load additional properties
                    if (properties.length > dateIndex + 1 && !properties[dateIndex + 1].isEmpty()) {
                        booking.setPrice(Double.parseDouble(properties[dateIndex + 1]));
                    }
                    if (properties.length > dateIndex + 2 && !properties[dateIndex + 2].isEmpty()) {
                        booking.setCancellationFee(Double.parseDouble(properties[dateIndex + 2]));
                    }
                    if (properties.length > dateIndex + 3 && !properties[dateIndex + 3].isEmpty()) {
                        try {
                            Booking.BookingStatus status = Booking.BookingStatus.valueOf(properties[dateIndex + 3]);
                            booking.setStatus(status);
                        } catch (IllegalArgumentException e) {
                            boolean cancelled = Boolean.parseBoolean(properties[dateIndex + 3]);
                            booking.setStatus(cancelled ? Booking.BookingStatus.CANCELLED : Booking.BookingStatus.BOOKED);
                        }
                    }
                    if (properties.length > dateIndex + 4 && !properties[dateIndex + 4].isEmpty() && !properties[dateIndex + 4].equals("null")) {
                        booking.setActionDate(LocalDate.parse(properties[dateIndex + 4]));
                    }
                    if (properties.length > dateIndex + 5 && !properties[dateIndex + 5].isEmpty()) {
                        booking.setPartialCancellation(Boolean.parseBoolean(properties[dateIndex + 5]));
                    }
                    
                    // Add booking to customer and passengers to flights
                    customer.addBooking(booking);
                    if (!booking.isCancelled()) {
                        try {
                            outboundFlight.addPassenger(customer);
                            if (returnFlight != null) {
                                returnFlight.addPassenger(customer);
                            }
                        } catch (FlightBookingSystemException ex) {
                            System.err.println("Warning: Could not add passenger to flight: " + ex.getMessage());
                        }
                    }
                } catch (NumberFormatException | FlightBookingSystemException ex) {
                    throw new FlightBookingSystemException("Unable to parse booking data on line " + line_idx
                        + "\nError: " + ex);
                }
                line_idx++;
            }
        } catch (IOException ex) {
            new File(RESOURCE).getParentFile().mkdirs();
            new File(RESOURCE).createNewFile();
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(RESOURCE))) {
            for (Customer customer : fbs.getCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    out.print(customer.getId() + SEPARATOR);
                    out.print(booking.getOutboundFlight().getId() + SEPARATOR);
                    out.print((booking.getReturnFlight() != null ? booking.getReturnFlight().getId() : "null") + SEPARATOR);
                    out.print(booking.getBookingDate() + SEPARATOR);
                    out.print(booking.getPrice() + SEPARATOR);
                    out.print(booking.getCancellationFee() + SEPARATOR);
                    out.print(booking.getStatus() + SEPARATOR);
                    out.print(booking.getActionDate() + SEPARATOR);
                    out.print(booking.isPartialCancellation() + SEPARATOR);
                    out.println();
                }
            }
        }
    }
}
