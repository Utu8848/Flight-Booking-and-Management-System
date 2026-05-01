package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main data loader and storage manager for the Flight Booking System.
 * Coordinates multiple data managers to load and save system data.
 * 
 * @author M9
 */

public class FlightBookingSystemData {

    /**
     * Constructs a FlightBookingSystemData.
     */
    public FlightBookingSystemData() {
    }
    
    private static final List<DataManager> dataManagers = new ArrayList<>();
    
    // runs only once when the object gets loaded to memory
    static {
        dataManagers.add(new FlightDataManager());
        
        // FIXED: Uncommented these lines so customers and bookings load!
        dataManagers.add(new CustomerDataManager());
        dataManagers.add(new BookingDataManager());
    }
    
    /**
     * Loads the flight booking system data from persistent storage.
     * Uses all registered data managers to load flights, customers, and bookings.
     * 
     * @return the loaded flight booking system
     * @throws FlightBookingSystemException if data validation fails
     * @throws IOException if file reading fails
     */
    public static FlightBookingSystem load() throws FlightBookingSystemException, IOException {

        FlightBookingSystem fbs = new FlightBookingSystem();
        for (DataManager dm : dataManagers) {
            dm.loadData(fbs);
        }
        return fbs;
    }

    /**
     * Stores the flight booking system data to persistent storage.
     * Uses all registered data managers to save flights, customers, and bookings.
     * Skips persistence if system is in test mode.
     * 
     * @param fbs the flight booking system to save
     * @throws IOException if file writing fails
     */
    public static void store(FlightBookingSystem fbs) throws IOException {
        // Don't persist to files in test mode
        if (fbs.isTestMode()) {
            return;
        }
        
        for (DataManager dm : dataManagers) {
            dm.storeData(fbs);
        }
    }
    
}
