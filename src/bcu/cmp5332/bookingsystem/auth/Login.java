package bcu.cmp5332.bookingsystem.auth;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.AuthenticationService;
import bcu.cmp5332.bookingsystem.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Command to authenticate and log in a user to the system.
 * 
 * @author M9
 */

public class Login implements Command {
    
    /** The username for authentication */
    private String username;
    
    /** The password for authentication */
    private String password;
    
    /**
     * Constructs a Login command for interactive mode.
     * Credentials will be prompted from the user.
     */
    public Login() {
        // Interactive mode
    }
    
    /**
     * Constructs a Login command with specified credentials.
     * 
     * @param username the username for authentication
     * @param password the password for authentication
     */
    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Executes the login command.
     * Authenticates the user and establishes a session.
     * 
     * @param fbs the flight booking system
     * @throws FlightBookingSystemException if login fails or user is already logged in
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        try {
            AuthenticationService auth = fbs.getAuthService();
            
            // Check if already logged in BEFORE asking for credentials
            if (auth.isLoggedIn()) {
                System.out.println("⚠ Already logged in as: " + auth.getCurrentUser().getUsername() + 
                                 " (" + auth.getCurrentUser().getUserType() + ")");
                System.out.println("Please logout first if you want to login as a different user.");
                return;
            }
            
            if (username == null) {
                promptForCredentials();
            }
            
            auth.login(username, password);
            User user = auth.getCurrentUser();
            
            System.out.println("✓ Login successful!");
            System.out.println("Welcome, " + user.getUsername() + "!");
            System.out.println("User type: " + user.getUserType());
            
            if (user.isCustomer()) {
                System.out.println("Customer ID: " + user.getCustomerId());
            }
            
        } catch (IOException ex) {
            throw new FlightBookingSystemException("Error reading input: " + ex.getMessage());
        }
    }
    
    /**
     * Prompts the user to enter their username and password.
     * Reads credentials from standard input.
     * 
     * @throws IOException if an error occurs reading input
     */
    private void promptForCredentials() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        
        System.out.print("Username: ");
        System.out.flush();
        this.username = reader.readLine();
        
        System.out.print("Password: ");
        System.out.flush();
        this.password = reader.readLine();
    }
}
