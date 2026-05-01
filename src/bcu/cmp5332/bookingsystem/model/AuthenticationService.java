package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.io.*;
import java.util.*;

/**
 * Handles user authentication and session management.
 * 
 * @author M9
 */

public class AuthenticationService {
    
    /** Users File field */
    private static final String USERS_FILE = "./resources/data/users.txt";
    /** Users field */
    private Map<String, User> users;
    /** Currentuser field */
    private User currentUser;
    
    /** Fbs field */
    private FlightBookingSystem fbs; // Reference to check customer deletion status
    /** Testmode field */
    private boolean testMode; // If true, don't persist to files
    
    /**
     * Constructs an AuthenticationService.
     * 
     * @param fbs the flight booking system
     */
    public AuthenticationService(FlightBookingSystem fbs) {
        this(fbs, false);
    }
    
    /**
     * Constructor with test mode option.
     * @param fbs the flight booking system
     * @param testMode if true, data won't be persisted to files
     */
    public AuthenticationService(FlightBookingSystem fbs, boolean testMode) {
        this.fbs = fbs;
        this.testMode = testMode;
        this.users = new HashMap<>();
        if (testMode) {
            createDefaultUsers();
        } else {
            loadUsers();
        }
    }
    
    /**
     * Loads users from file.
     */
    private void loadUsers() {
        File file = new File(USERS_FILE);
        
        // Create default admin if file doesn't exist
        if (!file.exists()) {
            createDefaultUsers();
            saveUsers();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String password = parts[1];
                    User.UserType type = User.UserType.valueOf(parts[2]);
                    
                    // Check for deleted status (added at the end for backward compatibility)
                    boolean deleted = false;
                    Integer customerId = null;
                    
                    if (type == User.UserType.CUSTOMER) {
                        if (parts.length >= 4) {
                            customerId = Integer.parseInt(parts[3]);
                        }
                        if (parts.length >= 5) {
                            deleted = Boolean.parseBoolean(parts[4]);
                        }
                        users.put(username, new User(username, password, type, customerId, deleted));
                    } else {
                        // Admin user
                        if (parts.length >= 4) {
                            deleted = Boolean.parseBoolean(parts[3]);
                        }
                        User adminUser = new User(username, password, type);
                        adminUser.setDeleted(deleted);
                        users.put(username, adminUser);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Error loading users: " + ex.getMessage());
            createDefaultUsers();
        }
    }
    
    /**
     * Creates default admin and sample customer users.
     */
    private void createDefaultUsers() {
        // Default admin: username=admin, password=admin123
        users.put("admin", new User("admin", "admin123", User.UserType.ADMIN));
        
        // Sample customer users (linked to customer IDs 1-6) - Nepali names
        users.put("aarav", new User("aarav", "aarav123", User.UserType.CUSTOMER, 1));
        users.put("binita", new User("binita", "binita123", User.UserType.CUSTOMER, 2));
        users.put("chiranjivi", new User("chiranjivi", "chiranjivi123", User.UserType.CUSTOMER, 3));
        users.put("diya", new User("diya", "diya123", User.UserType.CUSTOMER, 4));
        users.put("elina", new User("elina", "elina123", User.UserType.CUSTOMER, 5));
        users.put("furba", new User("furba", "furba123", User.UserType.CUSTOMER, 6));
    }
    
    /**
     * Saves users to file.
     */
    private void saveUsers() {
        if (testMode) {
            return; // Don't persist in test mode
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.println(user.toString());
            }
        } catch (IOException ex) {
            System.err.println("Error saving users: " + ex.getMessage());
        }
    }
    
    /**
     * Authenticates a user.
     * 
     * @param username the username
     * @param password the password
     * @return true if login successful
     * @throws FlightBookingSystemException if authentication fails
     */
    public boolean login(String username, String password) throws FlightBookingSystemException {
        User user = users.get(username);
        
        if (user == null) {
            throw new FlightBookingSystemException("Invalid username or password.");
        }
        
        if (!user.getPassword().equals(password)) {
            throw new FlightBookingSystemException("Invalid username or password.");
        }
        
        // Check if user is deleted (soft-deleted)
        if (user.isDeleted()) {
            throw new FlightBookingSystemException(
                "This user account has been deleted. " +
                "Please contact an administrator if you believe this is an error."
            );
        }
        
        // For customer users, also check if the linked customer is deleted
        if (user.isCustomer() && user.getCustomerId() != null && fbs != null) {
            try {
                Customer customer = fbs.getCustomerByID(user.getCustomerId());
                if (customer != null && customer.isDeleted()) {
                    throw new FlightBookingSystemException(
                        "This customer account has been deleted. " +
                        "Please contact an administrator if you believe this is an error."
                    );
                }
            } catch (FlightBookingSystemException ex) {
                // Re-throw if it's about deleted customer
                if (ex.getMessage().contains("deleted")) {
                    throw ex;
                }
                // Customer not found - continue with login
                // (might happen in certain edge cases)
            }
        }
        
        currentUser = user;
        return true;
    }
    
    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Gets the currently logged-in user.
     * 
     * @return the current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is logged in.
     * 
     * @return true if a user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Checks if current user is admin.
     * 
     * @return true if current user is admin
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Checks if current user is customer.
     * 
     * @return true if current user is customer
     */
    public boolean isCustomer() {
        return currentUser != null && currentUser.isCustomer();
    }
    
    /**
     * Gets customer ID of logged-in customer.
     * 
     * @return the customer ID or null if not a customer
     */
    public Integer getCustomerId() {
        return currentUser != null ? currentUser.getCustomerId() : null;
    }
    
    /**
     * Registers a new customer user.
     * 
     * @param username the username
     * @param password the password
     * @param customerId the customer ID
     * @throws FlightBookingSystemException if registration fails
     */
    public void registerCustomer(String username, String password, int customerId) throws FlightBookingSystemException {
        if (users.containsKey(username)) {
            throw new FlightBookingSystemException("Username already exists.");
        }
        
        User newUser = new User(username, password, User.UserType.CUSTOMER, customerId);
        users.put(username, newUser);
        saveUsers();
    }
    
    /**
     * Marks a customer's user account as deleted.
     * @param customerId the customer ID
     */
    public void markCustomerUserAsDeleted(int customerId) {
        markCustomerUserAsDeleted(customerId, true);
    }
    
    /**
     * Sets the deleted status for a customer's user account.
     * @param customerId the customer ID
     * @param deleted the deleted status
     */
    public void markCustomerUserAsDeleted(int customerId, boolean deleted) {
        for (User user : users.values()) {
            if (user.isCustomer() && user.getCustomerId() != null && 
                user.getCustomerId() == customerId) {
                user.setDeleted(deleted);
                saveUsers();
                break;
            }
        }
    }
}
