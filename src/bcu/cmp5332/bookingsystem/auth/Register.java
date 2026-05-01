package bcu.cmp5332.bookingsystem.auth;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command to register a new user account in the system.
 * 
 * @author M9
 */

public class Register implements Command {

    /**
     * Executes the registration command.
     * Collects user information and creates a new customer account.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if registration fails or user is already logged in
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Check if an admin is trying to register
        if (flightBookingSystem.getAuthService().isLoggedIn() && 
            flightBookingSystem.getAuthService().isAdmin()) {
            throw new FlightBookingSystemException("Registration is for customers only. Admins cannot register.");
        }
        
        // Check if a customer is already logged in
        if (flightBookingSystem.getAuthService().isLoggedIn() && 
            flightBookingSystem.getAuthService().isCustomer()) {
            throw new FlightBookingSystemException("You are already logged in. Please logout first to register a new account.");
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            System.out.println("\n══════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("         CUSTOMER REGISTRATION");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════\n");
            
            // Get username
            System.out.print("Enter username: ");
            System.out.flush();
            String username = br.readLine().trim();
            if (username.isEmpty()) {
                throw new FlightBookingSystemException("Username cannot be empty.");
            }
            
            // Get password
            System.out.print("Enter password (min 6 characters): ");
            System.out.flush();
            String password = br.readLine().trim();
            if (password.length() < 6) {
                throw new FlightBookingSystemException("Password must be at least 6 characters.");
            }
            
            // Confirm password
            System.out.print("Confirm password: ");
            System.out.flush();
            String confirmPassword = br.readLine().trim();
            if (!password.equals(confirmPassword)) {
                throw new FlightBookingSystemException("Passwords do not match.");
            }
            
            // Get customer details
            System.out.print("Enter full name: ");
            System.out.flush();
            String name = br.readLine().trim();
            if (name.isEmpty()) {
                throw new FlightBookingSystemException("Name cannot be empty.");
            }
            
            System.out.print("Enter phone number (10 digits): ");
            System.out.flush();
            String phone = br.readLine().trim();
            if (!ValidationUtil.isValidPhone(phone)) {
                throw new FlightBookingSystemException(ValidationUtil.getPhoneErrorMessage());
            }
            
            System.out.print("Enter email address: ");
            System.out.flush();
            String email = br.readLine().trim();
            if (!ValidationUtil.isValidEmail(email)) {
                throw new FlightBookingSystemException(ValidationUtil.getEmailErrorMessage(false));
            }
            
            // Generate new customer ID
            int newCustomerId = 1;
            for (Customer c : flightBookingSystem.getCustomers()) {
                if (c.getId() >= newCustomerId) {
                    newCustomerId = c.getId() + 1;
                }
            }
            
            // Create new customer
            Customer newCustomer = new Customer(newCustomerId, name, phone, email);
            flightBookingSystem.addCustomer(newCustomer);
            
            // Register user account
            flightBookingSystem.getAuthService().registerCustomer(username, password, newCustomerId);
            
            // Save to files
            FlightBookingSystemData.store(flightBookingSystem);
            
            System.out.println("\n✓ Registration successful!");
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("Customer ID: " + newCustomerId);
            System.out.println("Username: " + username);
            System.out.println("Name: " + name);
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════");
            System.out.println("You can now login with your credentials using the 'login' command.\n");
            
        } catch (IOException ex) {
            throw new FlightBookingSystemException("Error reading input: " + ex.getMessage());
        }
    }
}
