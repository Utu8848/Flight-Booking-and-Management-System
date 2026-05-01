package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.AuthenticationService;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import javax.swing.*;
import java.awt.*;

/**
 * GUI window for user authentication and login.
 * 
 * @author M9
 */

public class LoginWindow extends JFrame {
    private FlightBookingSystem fbs;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loginSuccessful = false;
    
    public LoginWindow(FlightBookingSystem fbs) {
        super("Login - Flight Booking System");
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
        
        setLayout(new BorderLayout(15, 15));
        setSize(450, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(450, 60));
        JLabel titleLabel = new JLabel("Flight Booking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);
        loginButton.setBorderPainted(true);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(39, 174, 96), 1));
        // Force the button to honor our settings
        loginButton.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        
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
        
        loginButton.addActionListener(e -> login());
        cancelButton.addActionListener(e -> dispose());
        passwordField.addActionListener(e -> login());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        // Registration link panel
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        registerPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        
        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton registerButton = new JButton("Register as Customer");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerButton.setForeground(new Color(41, 128, 185));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setFocusPainted(false);
        registerButton.setOpaque(false);
        registerButton.addActionListener(e -> openRegistration());
        
        registerPanel.add(registerLabel);
        registerPanel.add(registerButton);
        
        // Add panels
        add(headerPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(registerPanel, BorderLayout.SOUTH);
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
    
    private void openRegistration() {
        dispose();
        new RegistrationWindow(fbs);
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            AuthenticationService auth = fbs.getAuthService();
            auth.login(username, password);
            
            loginSuccessful = true;
            JOptionPane.showMessageDialog(this, 
                "Welcome, " + username + "!\nUser type: " + auth.getCurrentUser().getUserType(), 
                "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            
            // Close login window
            dispose();
            
            // Launch appropriate GUI based on user role
            SwingUtilities.invokeLater(() -> {
                if (auth.isAdmin()) {
                    new MainWindow(fbs);
                } else if (auth.isCustomer()) {
                    new CustomerWindow(fbs);
                }
            });
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}
