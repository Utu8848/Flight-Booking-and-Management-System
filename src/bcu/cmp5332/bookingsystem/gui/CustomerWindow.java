package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.*;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * Customer GUI window for the Flight Booking System.
 * Provides a simplified interface for customer users to view flights,
 * manage their bookings, and interact with the system.
 * 
 * <p>Customers can:
 * <ul>
 * <li>View and search available flights with multiple filters</li>
 * <li>Issue new bookings (one-way or return)</li>
 * <li>View their existing bookings</li>
 * <li>Cancel bookings</li>
 * <li>Rebook flights (change flight for existing booking)</li>
 * </ul>
 * 
 * @author M9
 */
public class CustomerWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu flightsMenu, bookingsMenu, accountMenu;
    private JMenuItem flightsView;
    private JMenuItem bookingsIssue, bookingsView, bookingsCancel, bookingsRebook;
    private JMenuItem accountLogout, accountExit;

    private FlightBookingSystem fbs;
    private Customer currentCustomer;
    private JTable currentTable;
    private JLabel statusLabel;
    private JPanel mainPanel;

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TABLE_HEADER_COLOR = new Color(52, 73, 94);

    /**
     * Constructs a CustomerWindow for the logged-in customer.
     * Initializes the GUI components and loads the customer data.
     * 
     * @param fbs the flight booking system instance
     */
    public CustomerWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        
        // Get current customer
        Integer customerId = fbs.getAuthService().getCustomerId();
        if (customerId != null) {
            try {
                this.currentCustomer = fbs.getCustomerByID(customerId);
            } catch (FlightBookingSystemException ex) {
                JOptionPane.showMessageDialog(null,
                    "Error loading customer data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        initialize();
    }

    /**
     * Initializes the window components, menu bar, and status bar.
     * Sets up the UI appearance and displays the welcome screen.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("Flight Booking System - Customer Portal");
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(BACKGROUND_COLOR);

        createStatusBar();
        createMenuBar();

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        showWelcomeScreen();

        setSize(1200, 700);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Creates and configures the menu bar with customer-specific menu items.
     * Includes Flights, Bookings, and Account menus with their respective items.
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));

        // Flights Menu
        flightsMenu = new JMenu("Flights");
        flightsMenu.setForeground(Color.BLACK);
        flightsMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        flightsView = new JMenuItem("View Available Flights");
        flightsView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flightsView.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        flightsView.addActionListener(this);
        
        flightsMenu.add(flightsView);
        menuBar.add(flightsMenu);
        
        // Bookings Menu
        bookingsMenu = new JMenu("My Bookings");
        bookingsMenu.setForeground(Color.BLACK);
        bookingsMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        bookingsIssue = new JMenuItem("Book a Flight");
        bookingsIssue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsIssue.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bookingsIssue.addActionListener(this);
        
        bookingsView = new JMenuItem("View My Bookings");
        bookingsView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsView.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bookingsView.addActionListener(this);
        
        bookingsCancel = new JMenuItem("Cancel a Booking");
        bookingsCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsCancel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bookingsCancel.addActionListener(this);
        
        bookingsRebook = new JMenuItem("Rebook a Flight");
        bookingsRebook.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsRebook.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bookingsRebook.addActionListener(this);
        
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsView);
        bookingsMenu.addSeparator();
        bookingsMenu.add(bookingsCancel);
        bookingsMenu.add(bookingsRebook);
        menuBar.add(bookingsMenu);

        // Account Menu
        accountMenu = new JMenu("Account");
        accountMenu.setForeground(Color.BLACK);
        accountMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        accountLogout = new JMenuItem("Logout");
        accountLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountLogout.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        accountLogout.addActionListener(this);
        
        accountExit = new JMenuItem("Exit");
        accountExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountExit.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        accountExit.addActionListener(this);
        
        accountMenu.add(accountLogout);
        accountMenu.addSeparator();
        accountMenu.add(accountExit);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);
        
        // Apply custom styling to center menu items properly
        centerMenuItems();
    }
    
    /**
     * Centers menu items by removing icon space and applying centered alignment.
     */
    /**
     * Centers menu items horizontally in the menu bar.
     * Used for aesthetic positioning of menu elements.
     */
    private void centerMenuItems() {
        // Process all menus
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null) {
                // Get the popup menu to configure it
                JPopupMenu popup = menu.getPopupMenu();
                popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                
                for (int j = 0; j < menu.getItemCount(); j++) {
                    JMenuItem item = menu.getItem(j);
                    if (item != null) {
                        // Remove all icon-related spacing
                        item.setIconTextGap(0);
                        item.setIcon(null);
                        
                        // Set margins to zero to remove internal padding
                        item.setMargin(new Insets(0, 0, 0, 0));
                        
                        // Remove the default border and create a custom one with minimal padding
                        item.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                        
                        // Center the text
                        item.setHorizontalAlignment(SwingConstants.CENTER);
                        item.setHorizontalTextPosition(SwingConstants.CENTER);
                        
                        // Force repaint
                        item.revalidate();
                    }
                }
            }
        }
    }

    /**
     * Creates and adds the status bar at the bottom of the window.
     * Displays welcome message with customer name.
     */
    private void createStatusBar() {
        statusLabel = new JLabel("Welcome, " + fbs.getAuthService().getCurrentUser().getUsername() + 
                                " - Customer Portal");
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(44, 62, 80));
        statusLabel.setForeground(Color.WHITE);
        add(statusLabel, BorderLayout.SOUTH);
    }

    /**
     * Handles menu item click events.
     * Routes actions to appropriate methods based on the menu item selected.
     * 
     * @param ae the action event triggered by menu item selection
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == flightsView) {
            displayFlights();
        } else if (ae.getSource() == bookingsIssue) {
            issueBookingDialog();
        } else if (ae.getSource() == bookingsView) {
            displayMyBookings();
        } else if (ae.getSource() == bookingsCancel) {
            cancelBookingDialog();
        } else if (ae.getSource() == bookingsRebook) {
            rebookFlightDialog();
        } else if (ae.getSource() == accountLogout) {
            logout();
        } else if (ae.getSource() == accountExit) {
            exitApplication();
        }
    }

    /**
     * Displays the welcome screen with customer information and system instructions.
     * Shows customer details and available menu options.
     */
    private void showWelcomeScreen() {
        mainPanel.removeAll();
        
        // Create welcome panel with background image
        JPanel welcomePanel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            
            {
                // Load background image from resources
                try {
                    java.net.URL imgURL = getClass().getClassLoader().getResource("images/customer_background.jpg");
                    if (imgURL != null) {
                        backgroundImage = new ImageIcon(imgURL).getImage();
                    } else {
                        // Try PNG format if JPG not found
                        imgURL = getClass().getClassLoader().getResource("images/customer_background.png");
                        if (imgURL != null) {
                            backgroundImage = new ImageIcon(imgURL).getImage();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Could not load customer background image: " + e.getMessage());
                    backgroundImage = null;
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                if (backgroundImage != null) {
                    // Draw background image scaled to panel size
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback: gradient background if image not found
                    GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_COLOR, 
                                                              0, getHeight(), new Color(200, 220, 240));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        welcomePanel.setBackground(BACKGROUND_COLOR);
        
        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setBackground(new Color(255, 255, 255, 230)); // Semi-transparent white
        contentBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(40, 60, 40, 60)
        ));

        // Load and display logo
        JLabel icon = new JLabel();
        try {
            java.net.URL imgURL = getClass().getClassLoader().getResource("images/logo.png");
            if (imgURL != null) {
                ImageIcon logoIcon = new ImageIcon(imgURL);
                Image scaledLogo = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                icon.setIcon(new ImageIcon(scaledLogo));
            } else {
                // Fallback: text placeholder if logo not found
                icon.setText("[LOGO]");
                icon.setFont(new Font("Segoe UI", Font.BOLD, 16));
                icon.setForeground(PRIMARY_COLOR);
            }
        } catch (Exception e) {
            icon.setText("[LOGO]");
            icon.setFont(new Font("Segoe UI", Font.BOLD, 16));
            icon.setForeground(PRIMARY_COLOR);
        }
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.add(icon);
        
        contentBox.add(Box.createVerticalStrut(20));

        JLabel titleLabel = new JLabel("Customer Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TABLE_HEADER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.add(titleLabel);

        contentBox.add(Box.createVerticalStrut(15));

        JLabel subtitleLabel = new JLabel("Welcome, " + currentCustomer.getName());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.add(subtitleLabel);
        
        contentBox.add(Box.createVerticalStrut(20));
        
        JLabel instructionLabel = new JLabel("Use the menu above to view flights and manage bookings");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.add(instructionLabel);
        
        contentBox.add(Box.createVerticalStrut(25));
        
        // Auto-updating real-time date label (Nepal timezone)
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(new Color(100, 100, 100));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Initial date display
        dateLabel.setText(FlightBookingSystem.getCurrentNepalDate().format(dateFormatter));
        contentBox.add(dateLabel);
        
        // Timer to update date every second (in case it changes at midnight)
        Timer timer = new Timer(1000, e -> {
            dateLabel.setText(FlightBookingSystem.getCurrentNepalDate().format(dateFormatter));
        });
        timer.start();
        
        // Stop timer when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                timer.stop();
            }
        });

        welcomePanel.add(contentBox);
        mainPanel.add(welcomePanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        statusLabel.setText("Welcome, " + currentCustomer.getName() + " | " + 
                           FlightBookingSystem.getCurrentNepalDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
    }

    /**
     * Displays all available flights without any filters.
     * This method calls displayFlightsWithFilter with all filter parameters set to null.
     */
    private void displayFlights() {
        displayFlightsWithFilter(null, null, null, null, null, null, null, "none", "none");
    }
    
    /**
     * Displays available flights with optional filters and sorting.
     * Flights are filtered based on provided criteria and sorted according to specified order.
     * Only shows flights that have not departed and are not deleted.
     * 
     * @param flightIdFilter filter by exact flight ID, null to include all
     * @param flightNumberFilter filter by flight number (contains match), null to include all
     * @param originFilter filter by origin city (contains match), null to include all
     * @param destinationFilter filter by destination city (contains match), null to include all
     * @param minPrice minimum price filter, null for no minimum
     * @param maxPrice maximum price filter, null for no maximum
     * @param departureDate filter by exact departure date, null to include all dates
     * @param dateSortOrder sort order for date ("asc", "desc", or "none")
     * @param priceSortOrder sort order for price ("asc", "desc", or "none")
     */
    private void displayFlightsWithFilter(Integer flightIdFilter, String flightNumberFilter,
                                         String originFilter, String destinationFilter, 
                                         Double minPrice, Double maxPrice, 
                                         LocalDate departureDate, 
                                         String dateSortOrder, String priceSortOrder) {
        mainPanel.removeAll();
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel headerLabel = new JLabel("Available Flights");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TABLE_HEADER_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Create search/filter panel with GridBagLayout for better organization
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // First row - Flight ID and Flight Number
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel flightIdLabel = new JLabel("Flight ID:");
        flightIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(flightIdLabel, gbc);
        
        gbc.gridx = 1;
        JTextField flightIdField = new JTextField(flightIdFilter != null ? flightIdFilter.toString() : "", 7);
        filterPanel.add(flightIdField, gbc);
        
        gbc.gridx = 2;
        JLabel flightNumberLabel = new JLabel("Flight Number:");
        flightNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(flightNumberLabel, gbc);
        
        gbc.gridx = 3;
        JTextField flightNumberField = new JTextField(flightNumberFilter != null ? flightNumberFilter : "", 10);
        filterPanel.add(flightNumberField, gbc);
        
        gbc.gridx = 4;
        JLabel originLabel = new JLabel("Origin:");
        originLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(originLabel, gbc);
        
        gbc.gridx = 5;
        JTextField originField = new JTextField(originFilter != null ? originFilter : "", 10);
        filterPanel.add(originField, gbc);
        
        gbc.gridx = 6;
        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(destinationLabel, gbc);
        
        gbc.gridx = 7;
        JTextField destinationField = new JTextField(destinationFilter != null ? destinationFilter : "", 10);
        filterPanel.add(destinationField, gbc);
        
        // Second row - Prices and Date
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel minPriceLabel = new JLabel("Min Price (£):");
        minPriceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(minPriceLabel, gbc);
        
        gbc.gridx = 1;
        JTextField minPriceField = new JTextField(minPrice != null ? String.valueOf(minPrice) : "", 7);
        filterPanel.add(minPriceField, gbc);
        
        gbc.gridx = 2;
        JLabel maxPriceLabel = new JLabel("Max Price (£):");
        maxPriceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(maxPriceLabel, gbc);
        
        gbc.gridx = 3;
        JTextField maxPriceField = new JTextField(maxPrice != null ? String.valueOf(maxPrice) : "", 7);
        filterPanel.add(maxPriceField, gbc);
        
        gbc.gridx = 4;
        JLabel departureDateLabel = new JLabel("Departure Date:");
        departureDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(departureDateLabel, gbc);
        
        gbc.gridx = 5;
        JTextField departureDateField = new JTextField(departureDate != null ? departureDate.toString() : "", 10);
        departureDateField.setToolTipText("Format: YYYY-MM-DD");
        filterPanel.add(departureDateField, gbc);
        
        // Third row - Sort options and buttons
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dateSortLabel = new JLabel("Sort by Date:");
        dateSortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(dateSortLabel, gbc);
        
        gbc.gridx = 1;
        String[] sortOptions = {"None", "Ascending", "Descending"};
        JComboBox<String> dateSortCombo = new JComboBox<>(sortOptions);
        dateSortCombo.setPreferredSize(new Dimension(120, 25));
        if (dateSortOrder.equals("asc")) dateSortCombo.setSelectedIndex(1);
        else if (dateSortOrder.equals("desc")) dateSortCombo.setSelectedIndex(2);
        else dateSortCombo.setSelectedIndex(0);
        filterPanel.add(dateSortCombo, gbc);
        
        gbc.gridx = 2;
        JLabel priceSortLabel = new JLabel("Sort by Price:");
        priceSortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        filterPanel.add(priceSortLabel, gbc);
        
        gbc.gridx = 3;
        JComboBox<String> priceSortCombo = new JComboBox<>(sortOptions);
        priceSortCombo.setPreferredSize(new Dimension(120, 25));
        if (priceSortOrder.equals("asc")) priceSortCombo.setSelectedIndex(1);
        else if (priceSortOrder.equals("desc")) priceSortCombo.setSelectedIndex(2);
        else priceSortCombo.setSelectedIndex(0);
        filterPanel.add(priceSortCombo, gbc);
        
        gbc.gridx = 4;
        JButton searchBtn = new JButton("Search/Filter");
        searchBtn.setBackground(PRIMARY_COLOR);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        filterPanel.add(searchBtn, gbc);
        
        gbc.gridx = 5;
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        filterPanel.add(clearBtn, gbc);
        
        searchBtn.addActionListener(e -> {
            String flightIdStr = flightIdField.getText().trim();
            String flightNumber = flightNumberField.getText().trim();
            String origin = originField.getText().trim();
            String destination = destinationField.getText().trim();
            String minPriceStr = minPriceField.getText().trim();
            String maxPriceStr = maxPriceField.getText().trim();
            String departureDateStr = departureDateField.getText().trim();
            
            Integer flightIdVal = null;
            Double minPriceVal = null;
            Double maxPriceVal = null;
            LocalDate departureDateVal = null;
            
            try {
                if (!flightIdStr.isEmpty()) {
                    flightIdVal = Integer.parseInt(flightIdStr);
                }
                if (!minPriceStr.isEmpty()) {
                    minPriceVal = Double.parseDouble(minPriceStr);
                }
                if (!maxPriceStr.isEmpty()) {
                    maxPriceVal = Double.parseDouble(maxPriceStr);
                }
                if (!departureDateStr.isEmpty()) {
                    departureDateVal = LocalDate.parse(departureDateStr);
                }
                
                String dateSort = dateSortCombo.getSelectedIndex() == 1 ? "asc" : 
                                dateSortCombo.getSelectedIndex() == 2 ? "desc" : "none";
                String priceSort = priceSortCombo.getSelectedIndex() == 1 ? "asc" : 
                                 priceSortCombo.getSelectedIndex() == 2 ? "desc" : "none";
                                 
                displayFlightsWithFilter(
                    flightIdVal,
                    flightNumber.isEmpty() ? null : flightNumber,
                    origin.isEmpty() ? null : origin,
                    destination.isEmpty() ? null : destination,
                    minPriceVal,
                    maxPriceVal,
                    departureDateVal,
                    dateSort,
                    priceSort
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(CustomerWindow.this, 
                    "Invalid input format. Please check:\n" +
                    "- Flight ID should be a number\n" +
                    "- Prices should be numbers\n" +
                    "- Date format: YYYY-MM-DD (e.g., 2025-03-15)", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        clearBtn.addActionListener(e -> {
            displayFlightsWithFilter(null, null, null, null, null, null, null, "none", "none");
        });
        
        dateSortCombo.addActionListener(e -> {
            String flightIdStr = flightIdField.getText().trim();
            String flightNumber = flightNumberField.getText().trim();
            String origin = originField.getText().trim();
            String destination = destinationField.getText().trim();
            String minPriceStr = minPriceField.getText().trim();
            String maxPriceStr = maxPriceField.getText().trim();
            String departureDateStr = departureDateField.getText().trim();
            
            try {
                Integer flightIdVal = flightIdStr.isEmpty() ? null : Integer.parseInt(flightIdStr);
                Double minPriceVal = minPriceStr.isEmpty() ? null : Double.parseDouble(minPriceStr);
                Double maxPriceVal = maxPriceStr.isEmpty() ? null : Double.parseDouble(maxPriceStr);
                LocalDate departureDateVal = departureDateStr.isEmpty() ? null : LocalDate.parse(departureDateStr);
                
                String dateSort = dateSortCombo.getSelectedIndex() == 1 ? "asc" : 
                                dateSortCombo.getSelectedIndex() == 2 ? "desc" : "none";
                String priceSort = priceSortCombo.getSelectedIndex() == 1 ? "asc" : 
                                 priceSortCombo.getSelectedIndex() == 2 ? "desc" : "none";
                                 
                displayFlightsWithFilter(
                    flightIdVal,
                    flightNumber.isEmpty() ? null : flightNumber,
                    origin.isEmpty() ? null : origin,
                    destination.isEmpty() ? null : destination,
                    minPriceVal,
                    maxPriceVal,
                    departureDateVal,
                    dateSort,
                    priceSort
                );
            } catch (Exception ex) {
                // Silently fail for sort changes with invalid data
            }
        });
        
        priceSortCombo.addActionListener(e -> {
            String flightIdStr = flightIdField.getText().trim();
            String flightNumber = flightNumberField.getText().trim();
            String origin = originField.getText().trim();
            String destination = destinationField.getText().trim();
            String minPriceStr = minPriceField.getText().trim();
            String maxPriceStr = maxPriceField.getText().trim();
            String departureDateStr = departureDateField.getText().trim();
            
            try {
                Integer flightIdVal = flightIdStr.isEmpty() ? null : Integer.parseInt(flightIdStr);
                Double minPriceVal = minPriceStr.isEmpty() ? null : Double.parseDouble(minPriceStr);
                Double maxPriceVal = maxPriceStr.isEmpty() ? null : Double.parseDouble(maxPriceStr);
                LocalDate departureDateVal = departureDateStr.isEmpty() ? null : LocalDate.parse(departureDateStr);
                
                String dateSort = dateSortCombo.getSelectedIndex() == 1 ? "asc" : 
                                dateSortCombo.getSelectedIndex() == 2 ? "desc" : "none";
                String priceSort = priceSortCombo.getSelectedIndex() == 1 ? "asc" : 
                                 priceSortCombo.getSelectedIndex() == 2 ? "desc" : "none";
                                 
                displayFlightsWithFilter(
                    flightIdVal,
                    flightNumber.isEmpty() ? null : flightNumber,
                    origin.isEmpty() ? null : origin,
                    destination.isEmpty() ? null : destination,
                    minPriceVal,
                    maxPriceVal,
                    departureDateVal,
                    dateSort,
                    priceSort
                );
            } catch (Exception ex) {
                // Silently fail for sort changes with invalid data
            }
        });
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        List<Flight> flights = fbs.getFlights();
        LocalDate currentDate = FlightBookingSystem.getCurrentNepalDate();
        
        java.util.List<Flight> availableFlights = new java.util.ArrayList<>();
        for (Flight flight : flights) {
            if (!flight.isDeleted() && !flight.hasDeparted(currentDate)) {
                // Apply filters
                boolean matchesFlightId = flightIdFilter == null || 
                    flight.getId() == flightIdFilter;
                boolean matchesFlightNumber = flightNumberFilter == null || 
                    flight.getFlightNumber().toLowerCase().contains(flightNumberFilter.toLowerCase());
                boolean matchesOrigin = originFilter == null || 
                    flight.getOrigin().toLowerCase().contains(originFilter.toLowerCase());
                boolean matchesDestination = destinationFilter == null || 
                    flight.getDestination().toLowerCase().contains(destinationFilter.toLowerCase());
                
                // Calculate dynamic price for filtering
                double dynamicPrice = flight.calculateDynamicPrice(currentDate);
                boolean matchesMinPrice = minPrice == null || dynamicPrice >= minPrice;
                boolean matchesMaxPrice = maxPrice == null || dynamicPrice <= maxPrice;
                
                // Check departure date match
                boolean matchesDepartureDate = departureDate == null || 
                    flight.getDepartureDate().equals(departureDate);
                
                if (matchesFlightId && matchesFlightNumber && matchesOrigin && matchesDestination && 
                    matchesMinPrice && matchesMaxPrice && matchesDepartureDate) {
                    availableFlights.add(flight);
                }
            }
        }
        
        // Apply sorting - price sort takes precedence over date sort if both are set
        if (priceSortOrder.equals("asc")) {
            availableFlights.sort((f1, f2) -> {
                double p1 = f1.calculateDynamicPrice(currentDate);
                double p2 = f2.calculateDynamicPrice(currentDate);
                return Double.compare(p1, p2);
            });
        } else if (priceSortOrder.equals("desc")) {
            availableFlights.sort((f1, f2) -> {
                double p1 = f1.calculateDynamicPrice(currentDate);
                double p2 = f2.calculateDynamicPrice(currentDate);
                return Double.compare(p2, p1);
            });
        } else if (dateSortOrder.equals("asc")) {
            availableFlights.sort((f1, f2) -> f1.getDepartureDate().compareTo(f2.getDepartureDate()));
        } else if (dateSortOrder.equals("desc")) {
            availableFlights.sort((f1, f2) -> f2.getDepartureDate().compareTo(f1.getDepartureDate()));
        }
        
        String[] columns = {"ID", "Flight No.", "Origin", "Destination", "Departure", "Available Seats", "Current Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Flight flight : availableFlights) {
            double dynamicPrice = flight.calculateDynamicPrice(currentDate);
            model.addRow(new Object[]{
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate(),
                flight.getCapacity() - flight.getPassengers().size() + " seats",
                String.format("£%.2f", dynamicPrice)
            });
        }
        
        currentTable = new JTable(model);
        styleTable(currentTable);
        
        JScrollPane scrollPane = new JScrollPane(currentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.revalidate();
        mainPanel.repaint();
        
        statusLabel.setText("Showing " + model.getRowCount() + " upcoming flights - Prices update based on demand");
    }

    /**
     * Displays all bookings for the current customer.
     * Shows booking details including flight information and booking status.
     */
    private void displayMyBookings() {
        mainPanel.removeAll();
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel headerLabel = new JLabel("My Bookings");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TABLE_HEADER_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Calculate upcoming bookings count
        LocalDate systemDate = LocalDate.now();
        List<Booking> bookings = currentCustomer.getBookings();
        int upcomingCount = (int) bookings.stream()
            .filter(b -> (b.getStatus() == Booking.BookingStatus.BOOKED || 
                         b.getStatus() == Booking.BookingStatus.REBOOKED) &&
                         b.getOutboundFlight().getDepartureDate().isAfter(systemDate))
            .count();
        
        // Add upcoming count label to header
        JLabel upcomingLabel = new JLabel("Upcoming Bookings: " + upcomingCount);
        upcomingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        upcomingLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(upcomingLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        String[] columns = {"Booking Date", "Flight No.", "Route", "Departure", "Price", 
                           "Fee", "Amount Paid", "Refund", "Status", "Last Modified"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Track which round trips we've already added
        java.util.Set<Booking> processedBookings = new java.util.HashSet<>();
        
        // Color palette for round trips with low opacity
        Color[] roundTripColors = {
            new Color(173, 216, 230, 80),  // Light blue
            new Color(255, 218, 185, 80),  // Peach
            new Color(221, 160, 221, 80),  // Plum
            new Color(144, 238, 144, 80),  // Light green
            new Color(255, 192, 203, 80),  // Pink
            new Color(255, 228, 181, 80),  // Moccasin
            new Color(176, 224, 230, 80),  // Powder blue
            new Color(255, 239, 213, 80),  // Papaya whip
        };
        int colorIndex = 0;
        
        // Store row colors for custom renderer
        java.util.Map<Integer, Color> rowColors = new java.util.HashMap<>();
        // Track which rows mark the end of a booking (for darker separator lines)
        java.util.Set<Integer> bookingEndRows = new java.util.HashSet<>();
        int currentRow = 0;
        
        for (Booking booking : bookings) {
            if (processedBookings.contains(booking)) {
                continue;
            }
            
            Flight outboundFlight = booking.getOutboundFlight();
            // For partial cancellations, don't show the fee (it's just used for calculation)
            String feeStr;
            if (booking.isPartialCancellation()) {
                feeStr = "-";
            } else {
                feeStr = booking.getCancellationFee() > 0 ? 
                    String.format("£%.2f", booking.getCancellationFee()) : "-";
            }
            
            // For Refund: show £0.00 instead of hyphen when cancelled rebooked flight is cancelled by admin
            String refundStr;
            if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                refundStr = String.format("£%.2f", booking.getRefundAmount());
            } else {
                refundStr = "-";
            }
            
            // For Amount Paid: show £0.00 instead of hyphen when cancelled rebooked flight has negative fee
            String amountPaidStr;
            if (booking.getStatus() == Booking.BookingStatus.CANCELLED && booking.getCancellationFee() < 0) {
                amountPaidStr = "£0.00";
            } else {
                amountPaidStr = String.format("£%.2f", booking.getAmountPaid());
            }
            
            String route = outboundFlight.getOrigin() + " → " + outboundFlight.getDestination();
            
            // Only show Last Modified date for CANCELLED and REBOOKED statuses
            String lastModified = "-";
            if (booking.getStatus() == Booking.BookingStatus.CANCELLED || 
                booking.getStatus() == Booking.BookingStatus.REBOOKED) {
                lastModified = booking.getActionDate().format(dtf);
            }
            
            if (booking.isRoundTrip()) {
                // This is a round trip - add both outbound and return as consecutive rows
                Color roundTripColor = roundTripColors[colorIndex % roundTripColors.length];
                colorIndex++;
                
                // For round-trip, only show fee/amount paid/refund once for the entire booking
                // Show them in the outbound row only
                model.addRow(new Object[]{
                    booking.getBookingDate().format(dtf),
                    outboundFlight.getFlightNumber(),
                    route,
                    outboundFlight.getDepartureDate().format(dtf),
                    String.format("£%.2f", booking.getPrice()),
                    feeStr,  // Show fee only if it exists (e.g., cancellation or rebook fee)
                    amountPaidStr,
                    refundStr,
                    booking.getStatus(),
                    lastModified
                });
                rowColors.put(currentRow, roundTripColor);
                currentRow++;
                
                // Add return flight row with dashes for price-related columns (displayed in outbound only)
                Flight returnFlight = booking.getReturnFlight();
                String returnRoute = returnFlight.getOrigin() + " → " + returnFlight.getDestination();
                
                model.addRow(new Object[]{
                    "-",
                    returnFlight.getFlightNumber(),
                    returnRoute,
                    returnFlight.getDepartureDate().format(dtf),
                    "-",
                    "-",
                    "-",
                    "-",
                    "-",
                    "-"
                });
                rowColors.put(currentRow, roundTripColor);
                currentRow++;
                
                processedBookings.add(booking);
                
                // Mark this as the end of a booking (for darker separator line)
                bookingEndRows.add(currentRow - 1);
            } else {
                // One-way booking
                model.addRow(new Object[]{
                    booking.getBookingDate().format(dtf),
                    outboundFlight.getFlightNumber(),
                    route,
                    outboundFlight.getDepartureDate().format(dtf),
                    String.format("£%.2f", booking.getPrice()),
                    feeStr,
                    amountPaidStr,
                    refundStr,
                    booking.getStatus(),
                    lastModified
                });
                currentRow++;
                
                // Mark this as the end of a booking (for darker separator line)
                bookingEndRows.add(currentRow - 1);
            }
        }
        
        currentTable = new JTable(model);
        
        // Custom cell renderer for row coloring and darker booking separators
        TableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (rowColors.containsKey(row)) {
                        c.setBackground(rowColors.get(row));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(SECONDARY_COLOR);
                    c.setForeground(Color.WHITE);
                }
                
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                
                // Add darker bottom border for booking end rows
                if (bookingEndRows.contains(row)) {
                    ((JLabel) c).setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 100, 100)));
                } else {
                    ((JLabel) c).setBorder(null);
                }
                
                return c;
            }
        };
        
        // Apply custom renderer to all columns
        for (int i = 0; i < currentTable.getColumnCount(); i++) {
            currentTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
        
        // Style table but don't override renderers
        styleTableHeaderOnly(currentTable);
        
        JScrollPane scrollPane = new JScrollPane(currentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.revalidate();
        mainPanel.repaint();
        
        int activeCount = (int) bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.BOOKED || 
                                                               b.getStatus() == Booking.BookingStatus.REBOOKED).count();
        statusLabel.setText("Total bookings: " + bookings.size() + " | Active: " + activeCount + " | Upcoming: " + upcomingCount);
    }

    /**
     * Applies professional styling to a JTable component.
     * Configures fonts, colors, row height, and cell renderers.
     * 
     * @param table the JTable to style
     */
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        
        // FIXED: Custom header renderer to force correct colors (matching admin GUI)
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.TableCellRenderer() {
            private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(TABLE_HEADER_COLOR);
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /**
     * Applies styling to table header only.
     * Used when table body doesn't need custom styling.
     * 
     * @param table the JTable whose header to style
     */
    private void styleTableHeaderOnly(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        
        // Custom header renderer only - don't override cell renderers
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new javax.swing.table.TableCellRenderer() {
            private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(TABLE_HEADER_COLOR);
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }

    /**
     * Opens a dialog for creating a new booking.
     * Allows customer to book one-way or return flights.
     * Validates all required fields are filled and contain valid numeric flight IDs.
     * Creates booking through the command system and displays total price.
     */
    private void issueBookingDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextField outboundFlightIdField = new JTextField();
        JTextField returnFlightIdField = new JTextField();
        JCheckBox roundTripCheckBox = new JCheckBox("Round-trip booking");
        
        panel.add(new JLabel("Outbound Flight ID:"));
        panel.add(outboundFlightIdField);
        panel.add(new JLabel("Return Flight ID:"));
        panel.add(returnFlightIdField);
        panel.add(new JLabel(""));
        panel.add(roundTripCheckBox);
        
        // Initially disable return flight field
        returnFlightIdField.setEnabled(false);
        
        // Enable/disable return flight field based on checkbox
        roundTripCheckBox.addActionListener(e -> {
            returnFlightIdField.setEnabled(roundTripCheckBox.isSelected());
            if (!roundTripCheckBox.isSelected()) {
                returnFlightIdField.setText("");
            }
        });
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Book a Flight", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate empty fields first
                if (outboundFlightIdField.getText().trim().isEmpty()) {
                    showError("Please enter an Outbound Flight ID!");
                    return;
                }
                
                if (roundTripCheckBox.isSelected() && returnFlightIdField.getText().trim().isEmpty()) {
                    showError("Please enter Return Flight ID for round-trip booking!");
                    return;
                }
                
                int outboundFlightId = Integer.parseInt(outboundFlightIdField.getText().trim());
                
                // Get the outbound flight to show price before booking
                Flight outboundFlight = fbs.getFlightByID(outboundFlightId);
                double totalPrice = outboundFlight.calculateDynamicPrice(FlightBookingSystem.getCurrentNepalDate());
                
                Command cmd;
                String bookingType = "One-way";
                
                if (roundTripCheckBox.isSelected() && !returnFlightIdField.getText().trim().isEmpty()) {
                    int returnFlightId = Integer.parseInt(returnFlightIdField.getText().trim());
                    Flight returnFlight = fbs.getFlightByID(returnFlightId);
                    totalPrice += returnFlight.calculateDynamicPrice(FlightBookingSystem.getCurrentNepalDate());
                    cmd = new AddBooking(currentCustomer.getId(), outboundFlightId, returnFlightId);
                    bookingType = "Round-trip";
                } else {
                    cmd = new AddBooking(currentCustomer.getId(), outboundFlightId);
                }
                
                cmd.execute(fbs);
                displayMyBookings();
                showSuccess(String.format("Flight booked successfully!\nType: %s\nBooking date: %s\nPrice paid: £%.2f", 
                    bookingType, FlightBookingSystem.getCurrentNepalDate(), totalPrice));
            } catch (NumberFormatException ex) {
                showError("Please enter valid flight ID(s)!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Opens a dialog for canceling an existing booking.
     * Prompts for booking type (one-way or round-trip) and required flight IDs.
     * Validates all required fields are filled and contain valid numeric IDs.
     * Applies appropriate cancellation fees and updates through the CancelBooking command.
     */
    private void cancelBookingDialog() {
        // First ask if it's one-way or round-trip
        String[] options = {"One-way", "Round-trip"};
        int tripType = JOptionPane.showOptionDialog(this,
            "Is this a one-way or round-trip booking?",
            "Select Booking Type",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (tripType == JOptionPane.CLOSED_OPTION) {
            return; // User cancelled
        }
        
        boolean isRoundTrip = (tripType == 1);
        
        JPanel panel = new JPanel(new GridLayout(isRoundTrip ? 5 : 4, 1, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel flightPanel = new JPanel(new GridLayout(isRoundTrip ? 2 : 1, 2, 10, 10));
        JTextField outboundFlightIdField = new JTextField();
        flightPanel.add(new JLabel("Outbound Flight ID:"));
        flightPanel.add(outboundFlightIdField);
        
        JTextField returnFlightIdField = null;
        if (isRoundTrip) {
            returnFlightIdField = new JTextField();
            flightPanel.add(new JLabel("Return Flight ID:"));
            flightPanel.add(returnFlightIdField);
        }
        panel.add(flightPanel);
        
        panel.add(new JLabel("")); // Spacer
        
        double fee = isRoundTrip ? 50.0 : 25.0;
        JLabel feeLabel = new JLabel(String.format("<html><b>Cancellation Fee: £%.2f</b></html>", fee));
        feeLabel.setForeground(new Color(192, 57, 43));
        panel.add(feeLabel);
        
        JLabel warningLabel = new JLabel("<html><i>Cancellation will apply a fee.</i></html>");
        warningLabel.setForeground(new Color(127, 140, 141));
        panel.add(warningLabel);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Cancel a Booking", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate empty fields first
                if (outboundFlightIdField.getText().trim().isEmpty()) {
                    showError("Please enter an Outbound Flight ID!");
                    return;
                }
                
                if (isRoundTrip && returnFlightIdField.getText().trim().isEmpty()) {
                    showError("Please enter Return Flight ID for round-trip cancellation!");
                    return;
                }
                
                int outboundFlightId = Integer.parseInt(outboundFlightIdField.getText().trim());
                Integer returnFlightId = null;
                
                if (isRoundTrip) {
                    returnFlightId = Integer.parseInt(returnFlightIdField.getText().trim());
                }
                
                Command cmd = new CancelBooking(currentCustomer.getId(), outboundFlightId, returnFlightId);
                cmd.execute(fbs);
                displayMyBookings();
                showSuccess(String.format("Booking cancelled successfully!\nCancellation fee of £%.2f has been applied.", fee));
            } catch (NumberFormatException ex) {
                showError("Please enter valid flight ID(s)!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Opens a dialog for changing flight in an existing booking.
     * Allows customer to switch to a different flight for a booking.
     * Validates all required fields are filled and contain valid numeric flight IDs.
     * Applies rebooking fee and updates through UpdateBooking command.
     * Note: Round-trip bookings cannot be rebooked.
     */
    private void rebookFlightDialog() {
        // Create custom dialog for better control over spacing
        JDialog dialog = new JDialog(this, "Rebook a Flight", true);
        dialog.setLayout(new BorderLayout());
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(10, 10, 15, 10)); // Added extra bottom padding
        
        // Flight input fields panel
        JPanel flightPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField oldFlightIdField = new JTextField();
        JTextField newFlightIdField = new JTextField();
        
        flightPanel.add(new JLabel("Current Flight ID:"));
        flightPanel.add(oldFlightIdField);
        flightPanel.add(new JLabel("New Flight ID:"));
        flightPanel.add(newFlightIdField);
        contentPanel.add(flightPanel);
        
        // Add spacer
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Fee label
        JLabel feeLabel = new JLabel("<html><b>Rebooking Fee: £15.00</b></html>");
        feeLabel.setForeground(new Color(192, 57, 43));
        feeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(feeLabel);
        
        // Add small spacer
        contentPanel.add(Box.createVerticalStrut(5));
        
        // Warning label
        JLabel warningLabel = new JLabel("<html><i>Note: Round-trip bookings cannot be rebooked.</i></html>");
        warningLabel.setForeground(new Color(127, 140, 141));
        warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(warningLabel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        // Button panel with bottom padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(5, 10, 10, 10)); // Extra bottom padding
        
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(75, 26));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(75, 26));
        
        final boolean[] okClicked = {false};
        
        okButton.addActionListener(e -> {
            okClicked[0] = true;
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> {
            dialog.dispose();
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        // Process the result after dialog closes
        if (okClicked[0]) {
            try {
                // Validate empty fields first
                if (oldFlightIdField.getText().trim().isEmpty() || 
                    newFlightIdField.getText().trim().isEmpty()) {
                    showError("Please fill in all required fields!");
                    return;
                }
                
                int oldFlightId = Integer.parseInt(oldFlightIdField.getText().trim());
                int newFlightId = Integer.parseInt(newFlightIdField.getText().trim());
                
                // Get new flight to show price
                Flight newFlight = fbs.getFlightByID(newFlightId);
                double newPrice = newFlight.calculateDynamicPrice(FlightBookingSystem.getCurrentNepalDate());
                
                Command cmd = new UpdateBooking(currentCustomer.getId(), oldFlightId, newFlightId);
                cmd.execute(fbs);
                displayMyBookings();
                showSuccess(String.format("Booking rebooked successfully!\n" +
                    "New flight price: £%.2f\n" +
                    "Rebooking fee: £15.00\n" +
                    "Total cost: £%.2f", newPrice, newPrice + 15.0));
            } catch (NumberFormatException ex) {
                showError("Please enter valid flight IDs!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Logs out the current customer and returns to login window.
     * Saves system data and closes the customer window.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?\n\nThe customer window will close and you'll need to login again.",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String username = fbs.getAuthService().getCurrentUser().getUsername();
                fbs.getAuthService().logout();
                
                // Save data before closing
                FlightBookingSystemData.store(fbs);
                
                dispose();
                
                JOptionPane.showMessageDialog(null,
                    "Logged out successfully, " + username + "!\n\n" +
                    "You can login again using the 'login' command in console\n" +
                    "or type 'loadgui' to open the login window.",
                    "Logout Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception ex) {
                showError("Logout error: " + ex.getMessage());
            }
        }
    }

    /**
     * Exits the application after user confirmation.
     * Saves all data before terminating the program.
     */
    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?\n\n" +
            "The customer window will close but the console will remain active.",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                FlightBookingSystemData.store(fbs);
                JOptionPane.showMessageDialog(this,
                    "Thank you for using Flight Booking System!\n" +
                    "Your data has been saved successfully.\n\n" +
                    "Have a great day, " + currentCustomer.getName() + "!",
                    "Goodbye",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Error saving data: " + ex.getMessage());
            }
            dispose(); // Close GUI window but keep console running
        }
    }

    /**
     * Displays a success message dialog to the user.
     * 
     * @param message the success message to display
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error message dialog to the user.
     * 
     * @param message the error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
