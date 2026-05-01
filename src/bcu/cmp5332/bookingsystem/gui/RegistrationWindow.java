package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.util.ValidationUtil;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * GUI window for new user registration.
 * 
 * @author M9
 */

public class RegistrationWindow extends JFrame {
    private FlightBookingSystem fbs;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private boolean registrationSuccessful = false;
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    
    public RegistrationWindow(FlightBookingSystem fbs) {
        super("Customer Registration");
        this.fbs = fbs;
        initialize();
    }
    
    private void initialize() {
        // Force cross-platform Look and Feel for consistent rendering
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // If that fails, continue with default
        }
        
        setLayout(new BorderLayout(10, 10));
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Add window listener to handle cancellation
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // If registration was not successful (i.e., user cancelled), reopen login window
                if (!registrationSuccessful) {
                    SwingUtilities.invokeLater(() -> new LoginWindow(fbs));
                }
            }
        });
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(500, 60));
        JLabel titleLabel = new JLabel("New Customer Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        
        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Full Name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Phone
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneField = new JTextField();
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Email
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        inputPanel.add(confirmPasswordLabel);
        inputPanel.add(confirmPasswordField);
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(phoneLabel);
        inputPanel.add(phoneField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        
        JLabel infoLabel = new JLabel("Registration Information:");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel info1 = new JLabel("• Username must be unique");
        info1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel info2 = new JLabel("• Password must be at least 6 characters");
        info2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel info3 = new JLabel("• Phone and email must be unique");
        info3.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel info4 = new JLabel("• Email must be valid (e.g., user@example.com, user@gmail.com)");
        info4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel info5 = new JLabel("• Phone must be exactly 10 digits (e.g., 9841234567)");
        info5.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        infoPanel.add(infoLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(info1);
        infoPanel.add(info2);
        infoPanel.add(info3);
        infoPanel.add(info4);
        infoPanel.add(info5);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(SUCCESS_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.setOpaque(true);
        registerButton.setContentAreaFilled(true);
        registerButton.setBorderPainted(true);
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(39, 174, 96), 1));
        // Force the button to honor our settings
        registerButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setOpaque(true);
        cancelButton.setContentAreaFilled(true);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        // Force the button to honor our settings
        cancelButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
        registerButton.addActionListener(e -> register());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        // Add panels
        add(headerPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(infoPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
        
        // Pack, position, and show - ensuring visibility
        pack();
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        toFront();
        repaint();
        
        // After window is shown, allow normal window behavior
        javax.swing.Timer timer = new javax.swing.Timer(200, e -> {
            setAlwaysOnTop(false);
            ((javax.swing.Timer)e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void register() {
        // Get field values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        
        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showError("All fields are required!");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            confirmPasswordField.setText("");
            return;
        }
        
        // Validate phone number format
        if (!ValidationUtil.isValidPhone(phone)) {
            showError(ValidationUtil.getPhoneErrorMessage());
            return;
        }
        
        // Validate email format
        if (!ValidationUtil.isValidEmail(email)) {
            showError(ValidationUtil.getEmailErrorMessage(false));
            return;
        }
        
        try {
            // Generate new customer ID
            int newCustomerId = 1;
            for (Customer c : fbs.getCustomers()) {
                if (c.getId() >= newCustomerId) {
                    newCustomerId = c.getId() + 1;
                }
            }
            
            // Create new customer (this will throw exception if phone/email exists)
            Customer newCustomer = new Customer(newCustomerId, name, phone, email);
            fbs.addCustomer(newCustomer);
            
            // Register user account
            fbs.getAuthService().registerCustomer(username, password, newCustomerId);
            
            // Save to files
            FlightBookingSystemData.store(fbs);
            
            registrationSuccessful = true;
            
            JOptionPane.showMessageDialog(this,
                "Registration successful!\n\n" +
                "Customer ID: " + newCustomerId + "\n" +
                "Username: " + username + "\n\n" +
                "Logging you in now...",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Close registration window
            dispose();
            
            // Automatically log in the user
            fbs.getAuthService().login(username, password);
            
            // Launch CustomerWindow
            SwingUtilities.invokeLater(() -> {
                new CustomerWindow(fbs);
            });
            
        } catch (Exception ex) {
            // Show only the user-friendly message, not the full exception
            showError(ex.getMessage());
            // Don't print stack trace to console for user-facing errors
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Registration Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
}
