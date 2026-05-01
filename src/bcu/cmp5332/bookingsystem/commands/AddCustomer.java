package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command to add a new customer to the system.
 * 
 * @author M9
 */

public class AddCustomer implements Command {

    /**
     * Constructs an AddCustomer command for interactive mode.
     */
    public AddCustomer() {
    }

    /**
     * Executes the add customer command.
     * Prompts for customer details, validates input, creates customer and user account.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if customer creation fails or validation errors occur
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.print("Name: ");
            System.out.flush();
            String name = reader.readLine().trim();
            if (name.isEmpty()) {
                throw new FlightBookingSystemException("Name cannot be empty.");
            }
            
            // Phone validation
            String phone;
            while (true) {
                System.out.print("Phone: ");
                System.out.flush();
                phone = reader.readLine().trim();
                
                if (ValidationUtil.isValidPhone(phone)) {
                    break;
                } else {
                    System.out.println("Invalid phone number! " + ValidationUtil.getPhoneErrorMessage());
                }
            }
            
            // Email validation - accepts all valid email formats
            String email;
            while (true) {
                System.out.print("Email: ");
                System.out.flush();
                email = reader.readLine().trim();
                
                if (ValidationUtil.isValidEmail(email)) {
                    break;
                } else {
                    System.out.println("Invalid email! " + ValidationUtil.getEmailErrorMessage(false));
                }
            }
            
            int maxId = 0;
            for (Customer customer : flightBookingSystem.getCustomers()) {
                if (customer.getId() > maxId) {
                    maxId = customer.getId();
                }
            }
            
            // Create customer - this will validate uniqueness
            Customer customer = new Customer(maxId + 1, name, phone, email);
            flightBookingSystem.addCustomer(customer);
            
            // Create default user account with username = lowercase first name, password = username123
            String firstName = name.split(" ")[0].toLowerCase();
            String defaultUsername = firstName;
            String defaultPassword = firstName + "123";
            
            // Register user account
            flightBookingSystem.getAuthService().registerCustomer(defaultUsername, defaultPassword, customer.getId());
            
            // Save to file storage
            try {
                FlightBookingSystemData.store(flightBookingSystem);
            } catch (IOException ex) {
                flightBookingSystem.getCustomers().remove(customer);
                throw new FlightBookingSystemException("Error saving customer. Changes have been rolled back: " + ex.getMessage());
            }
            
            System.out.println("Customer #" + customer.getId() + " added successfully.");
            System.out.println("Default username: " + defaultUsername);
            System.out.println("Default password: " + defaultPassword);
            
        } catch (IOException ex) {
            throw new FlightBookingSystemException("Error reading input: " + ex.getMessage());
        }
    }
}
