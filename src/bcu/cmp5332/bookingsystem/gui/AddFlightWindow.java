package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.main.DuplicateFlightException;
import bcu.cmp5332.bookingsystem.model.Flight;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * GUI window for adding new flights.
 * 
 * @author M9
 */
public class AddFlightWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private JTextField depDateText = new JTextField();
    private JTextField capacityText = new JTextField();
    private JTextField priceText = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton cancelBtn = new JButton("Cancel");

    /**
     * Constructs an AddFlightWindow.
     * 
     * @param mw the main window
     */
    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        }

        setTitle("Add a New Flight");

        setSize(350, 300);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(7, 2));
        topPanel.add(new JLabel("Flight No : "));
        topPanel.add(flightNoText);
        topPanel.add(new JLabel("Origin : "));
        topPanel.add(originText);
        topPanel.add(new JLabel("Destination : "));
        topPanel.add(destinationText);
        topPanel.add(new JLabel("Departure Date (YYYY-MM-DD) : "));
        topPanel.add(depDateText);
        topPanel.add(new JLabel("Capacity : "));
        topPanel.add(capacityText);
        topPanel.add(new JLabel("Price (£) : "));
        topPanel.add(priceText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        
        // Style the buttons
        addBtn.setOpaque(true);
        addBtn.setContentAreaFilled(true);
        addBtn.setFocusPainted(false);
        
        cancelBtn.setOpaque(true);
        cancelBtn.setContentAreaFilled(true);
        cancelBtn.setFocusPainted(false);
        
        bottomPanel.add(addBtn);
        bottomPanel.add(cancelBtn);

        addBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == addBtn) {
            addBook();
        } else if (ae.getSource() == cancelBtn) {
            this.setVisible(false);
        }

    }

    private void addBook() {
        try {
            String flightNumber = flightNoText.getText();
            String origin = originText.getText();
            String destination = destinationText.getText();
            LocalDate departureDate = null;
            
            // Validate all fields are filled
            if (flightNumber.trim().isEmpty() || origin.trim().isEmpty() || 
                destination.trim().isEmpty() || depDateText.getText().trim().isEmpty() ||
                capacityText.getText().trim().isEmpty() || priceText.getText().trim().isEmpty()) {
                throw new FlightBookingSystemException("All fields are required!");
            }
            
            // Parse date
            try {
                departureDate = LocalDate.parse(depDateText.getText());
            }
            catch (DateTimeParseException dtpe) {
                throw new FlightBookingSystemException("Date must be in YYYY-MM-DD format");
            }
            
            // Parse and validate capacity
            int capacity;
            try {
                capacity = Integer.parseInt(capacityText.getText().trim());
                if (capacity <= 0) {
                    throw new FlightBookingSystemException("Capacity must be a positive number!");
                }
                if (capacity > 1000) {
                    throw new FlightBookingSystemException("Capacity cannot exceed 1000 seats!");
                }
            } catch (NumberFormatException ex) {
                throw new FlightBookingSystemException("Capacity must be a valid number!");
            }
            
            // Parse and validate price
            double price;
            try {
                price = Double.parseDouble(priceText.getText().trim());
                if (price <= 0) {
                    throw new FlightBookingSystemException("Price must be a positive number!");
                }
                if (price > 10000) {
                    throw new FlightBookingSystemException("Price cannot exceed £10,000!");
                }
            } catch (NumberFormatException ex) {
                throw new FlightBookingSystemException("Price must be a valid number!");
            }
            
            // create and execute the AddFlight Command with all 6 parameters
            Command addFlight = new AddFlight(flightNumber, origin, destination, departureDate, capacity, price);
            addFlight.execute(mw.getFlightBookingSystem());
            // refresh the view with the list of flights
            mw.displayFlights();
            // hide (close) the AddFlightWindow
            this.setVisible(false);
        } catch (DuplicateFlightException dfe) {
            // Handle duplicate flight - show confirmation dialog
            Flight existingFlight = dfe.getExistingFlight();
            
            String message = "⚠ WARNING: A flight with the same details already exists:\n\n" +
                           "Flight #" + existingFlight.getId() + "\n" +
                           "Flight Number: " + existingFlight.getFlightNumber() + "\n" +
                           "Route: " + existingFlight.getOrigin() + " → " + existingFlight.getDestination() + "\n" +
                           "Departure Date: " + existingFlight.getDepartureDate() + "\n" +
                           "Capacity: " + existingFlight.getCapacity() + "\n" +
                           "Base Price: £" + existingFlight.getPrice() + "\n\n" +
                           "Do you want to add this flight anyway?";
            
            int response = JOptionPane.showConfirmDialog(
                this,
                message,
                "Duplicate Flight Detected",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                // User confirmed - add flight with skipConfirmation flag
                try {
                    String flightNumber = flightNoText.getText();
                    String origin = originText.getText();
                    String destination = destinationText.getText();
                    LocalDate departureDate = LocalDate.parse(depDateText.getText());
                    int capacity = Integer.parseInt(capacityText.getText().trim());
                    double price = Double.parseDouble(priceText.getText().trim());
                    
                    Command addFlight = new AddFlight(flightNumber, origin, destination, 
                                                     departureDate, capacity, price, true);
                    addFlight.execute(mw.getFlightBookingSystem());
                    mw.displayFlights();
                    this.setVisible(false);
                } catch (FlightBookingSystemException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            // If NO, do nothing - window stays open for user to edit
        } catch (FlightBookingSystemException ex) {
            // Show only the user-friendly message, not the full exception
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
