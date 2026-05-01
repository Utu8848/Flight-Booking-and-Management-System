package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.IOException;

/**
 * Interface for data persistence operations in the Flight Booking System.
 * Defines standard methods for loading and storing system data.
 * 
 * @author M9
 */
public interface DataManager {
    
    /** Field separator used in data files */
    public static final String SEPARATOR = "::";
    
    /**
     * Loads data from persistent storage into the flight booking system.
     * 
     * @param fbs the flight booking system to load data into
     * @throws IOException if file reading fails
     * @throws FlightBookingSystemException if data validation fails
     */
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException;
    
    /**
     * Stores data from the flight booking system to persistent storage.
     * 
     * @param fbs the flight booking system to save data from
     * @throws IOException if file writing fails
     */
    public void storeData(FlightBookingSystem fbs) throws IOException;
    
}
