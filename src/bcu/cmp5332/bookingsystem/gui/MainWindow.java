package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.*;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.util.ValidationUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * Main administrative GUI window with full system access.
 * 
 * @author M9
 */

public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu, flightsMenu, bookingsMenu, customersMenu;
    private JMenuItem adminLogout, adminExit;
    private JMenuItem flightsView, flightsAdd, flightsDel;
    private JMenuItem bookingsIssue, bookingsUpdate, bookingsCancel;
    private JMenuItem custView, custAdd, custDel;

    private FlightBookingSystem fbs;
    private JTable currentTable;
    private JLabel statusLabel;
    private JPanel mainPanel;
    private JLabel totalCountLabel; // Label to display total count

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TABLE_HEADER_COLOR = new Color(52, 73, 94);

    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }
    
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    private void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("Flight Booking Management System");
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

    private void createMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));

        // Admin Menu
        adminMenu = new JMenu("Admin");
        adminMenu.setForeground(Color.BLACK);
        adminMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        adminLogout = new JMenuItem("Logout");
        adminLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        adminLogout.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        adminLogout.addActionListener(this);
        
        adminExit = new JMenuItem("Exit");
        adminExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        adminExit.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        adminExit.addActionListener(this);
        
        adminMenu.add(adminLogout);
        adminMenu.addSeparator();
        adminMenu.add(adminExit);
        menuBar.add(adminMenu);

        // Flights Menu
        flightsMenu = new JMenu("Flights");
        flightsMenu.setForeground(Color.BLACK);
        flightsMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        flightsView = new JMenuItem("View All Flights");
        flightsView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flightsView.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        flightsAdd = new JMenuItem("Add New Flight");
        flightsAdd.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flightsAdd.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        flightsDel = new JMenuItem("Delete Flight");
        flightsDel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flightsDel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.addSeparator();
        flightsMenu.add(flightsDel);
        
        flightsView.addActionListener(this);
        flightsAdd.addActionListener(this);
        flightsDel.addActionListener(this);
        
        menuBar.add(flightsMenu);
        
        // Bookings Menu
        bookingsMenu = new JMenu("Bookings");
        bookingsMenu.setForeground(Color.BLACK);
        bookingsMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        bookingsIssue = new JMenuItem("Issue Booking");
        bookingsIssue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsIssue.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        bookingsUpdate = new JMenuItem("Update Booking");
        bookingsUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsUpdate.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        bookingsCancel = new JMenuItem("Cancel Booking");
        bookingsCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookingsCancel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsUpdate);
        bookingsMenu.addSeparator();
        bookingsMenu.add(bookingsCancel);
        
        bookingsIssue.addActionListener(this);
        bookingsUpdate.addActionListener(this);
        bookingsCancel.addActionListener(this);
        
        menuBar.add(bookingsMenu);

        // Customers Menu
        customersMenu = new JMenu("Customers");
        customersMenu.setForeground(Color.BLACK);
        customersMenu.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        custView = new JMenuItem("View All Customers");
        custView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        custView.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        custAdd = new JMenuItem("Add New Customer");
        custAdd.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        custAdd.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        custDel = new JMenuItem("Delete Customer");
        custDel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        custDel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.addSeparator();
        customersMenu.add(custDel);
        
        custView.addActionListener(this);
        custAdd.addActionListener(this);
        custDel.addActionListener(this);
        
        menuBar.add(customersMenu);

        setJMenuBar(menuBar);
        
        // Apply custom styling to center menu items properly
        centerMenuItems();
        
        // Configure menus based on user role
        configureMenusForUserRole();
    }
    
    /**
     * Centers menu items by removing icon space and applying centered alignment.
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
     * Configures menu visibility based on user role (Admin vs Customer).
     */
    private void configureMenusForUserRole() {
        boolean isAdmin = fbs.getAuthService().isAdmin();
        boolean isCustomer = fbs.getAuthService().isCustomer();
        
        if (isCustomer) {
            // Customers cannot add/delete flights or customers
            flightsAdd.setEnabled(false);
            flightsDel.setEnabled(false);
            custAdd.setEnabled(false);
            custDel.setEnabled(false);
            custView.setEnabled(false); // Customers can't see all customers
            
            // Update welcome message
            statusLabel.setText("Welcome, " + fbs.getAuthService().getCurrentUser().getUsername() + 
                        " (Customer ID: " + fbs.getAuthService().getCustomerId() + ")");
        } else if (isAdmin) {
            statusLabel.setText("Welcome, Administrator (" + fbs.getAuthService().getCurrentUser().getUsername() + ")");
        }
    }

    private void createStatusBar() {
        statusLabel = new JLabel("Welcome to Flight Booking System");
        statusLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(44, 62, 80));
        statusLabel.setForeground(Color.WHITE);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void showWelcomeScreen() {
        // Create welcome panel with background image
        JPanel welcomePanel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            
            {
                // Load background image from resources
                try {
                    java.net.URL imgURL = getClass().getClassLoader().getResource("images/admin_background.jpg");
                    if (imgURL != null) {
                        backgroundImage = new ImageIcon(imgURL).getImage();
                    } else {
                        // Try PNG format if JPG not found
                        imgURL = getClass().getClassLoader().getResource("images/admin_background.png");
                        if (imgURL != null) {
                            backgroundImage = new ImageIcon(imgURL).getImage();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Could not load admin background image: " + e.getMessage());
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

        JLabel titleLabel = new JLabel("Flight Booking Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TABLE_HEADER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.add(titleLabel);

        contentBox.add(Box.createVerticalStrut(15));

        JLabel subtitleLabel = new JLabel("Select a menu option above to get started");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.add(subtitleLabel);

        contentBox.add(Box.createVerticalStrut(25));

        // Auto-updating real-time date label (Nepal timezone)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        JLabel dateLabel = new JLabel();
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(PRIMARY_COLOR);
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Update date immediately using Nepal timezone
        LocalDate currentDate = FlightBookingSystem.getCurrentNepalDate();
        dateLabel.setText("System Date: " + currentDate.format(formatter));
        fbs.setSystemDate(currentDate); // Sync model with current date
        
        // Create timer to update date every minute (in case day changes while app is running)
        javax.swing.Timer dateTimer = new javax.swing.Timer(60000, e -> {
            LocalDate newDate = FlightBookingSystem.getCurrentNepalDate();
            dateLabel.setText("System Date: " + newDate.format(formatter));
            // Also update the system date in the model
            fbs.setSystemDate(newDate);
        });
        dateTimer.start();
        
        contentBox.add(dateLabel);

        welcomePanel.add(contentBox);
        
        mainPanel.removeAll();
        mainPanel.add(welcomePanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == adminLogout) {
            logoutAdmin();
        } else if (ae.getSource() == adminExit) {
            exitApplication();
        } else if (ae.getSource() == flightsView) {
            displayFlights();
        } else if (ae.getSource() == flightsAdd) {
            new AddFlightWindow(this);
        } else if (ae.getSource() == flightsDel) {
            deleteFlightDialog();
        } else if (ae.getSource() == bookingsIssue) {
            issueBookingDialog();
        } else if (ae.getSource() == bookingsUpdate) {
            updateBookingDialog();
        } else if (ae.getSource() == bookingsCancel) {
            cancelBookingDialog();
        } else if (ae.getSource() == custView) {
            displayCustomers();
        } else if (ae.getSource() == custAdd) {
            addCustomerDialog();
        } else if (ae.getSource() == custDel) {
            deleteCustomerDialog();
        }
    }

    public void displayFlights() {
        displayFlightsWithFilter(null, null, null, null, null, null, null, "none", "none");
    }
    
    private void displayFlightsWithFilter(Integer flightIdFilter, String flightNumberFilter,
                                         String originFilter, String destinationFilter, 
                                         Double minPrice, Double maxPrice, 
                                         LocalDate departureDate, 
                                         String dateSortOrder, String priceSortOrder) {
        mainPanel.removeAll();
        
        // Create header panel with search/filter controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel headerLabel = new JLabel("Upcoming Flights");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TABLE_HEADER_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Total flights count label
        totalCountLabel = new JLabel();
        totalCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalCountLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(totalCountLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
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
                JOptionPane.showMessageDialog(MainWindow.this, 
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
        
        List<Flight> allFlights = fbs.getFlights();
        LocalDate realTimeDate = FlightBookingSystem.getCurrentNepalDate();
        
        java.util.List<Flight> futureFlights = new java.util.ArrayList<>();
        for (Flight flight : allFlights) {
            if (!flight.isDeleted() && !flight.hasDeparted(realTimeDate)) {
                // Apply filters
                boolean matchesFlightId = flightIdFilter == null || 
                    flight.getId() == flightIdFilter.intValue();
                boolean matchesFlightNumber = flightNumberFilter == null || 
                    flight.getFlightNumber().toLowerCase().contains(flightNumberFilter.toLowerCase());
                boolean matchesOrigin = originFilter == null || 
                    flight.getOrigin().toLowerCase().contains(originFilter.toLowerCase());
                boolean matchesDestination = destinationFilter == null || 
                    flight.getDestination().toLowerCase().contains(destinationFilter.toLowerCase());
                
                // Calculate dynamic price for filtering
                double dynamicPrice = flight.calculateDynamicPrice(realTimeDate);
                boolean matchesMinPrice = minPrice == null || dynamicPrice >= minPrice;
                boolean matchesMaxPrice = maxPrice == null || dynamicPrice <= maxPrice;
                
                // Check departure date match
                boolean matchesDepartureDate = departureDate == null || 
                    flight.getDepartureDate().equals(departureDate);
                
                if (matchesFlightId && matchesFlightNumber && matchesOrigin && matchesDestination && matchesMinPrice && 
                    matchesMaxPrice && matchesDepartureDate) {
                    futureFlights.add(flight);
                }
            }
        }
        
        // Apply sorting - price sort takes precedence over date sort if both are set
        if (priceSortOrder.equals("asc")) {
            futureFlights.sort((f1, f2) -> {
                double p1 = f1.calculateDynamicPrice(realTimeDate);
                double p2 = f2.calculateDynamicPrice(realTimeDate);
                return Double.compare(p1, p2);
            });
        } else if (priceSortOrder.equals("desc")) {
            futureFlights.sort((f1, f2) -> {
                double p1 = f1.calculateDynamicPrice(realTimeDate);
                double p2 = f2.calculateDynamicPrice(realTimeDate);
                return Double.compare(p2, p1);
            });
        } else if (dateSortOrder.equals("asc")) {
            futureFlights.sort((f1, f2) -> f1.getDepartureDate().compareTo(f2.getDepartureDate()));
        } else if (dateSortOrder.equals("desc")) {
            futureFlights.sort((f1, f2) -> f2.getDepartureDate().compareTo(f1.getDepartureDate()));
        }
        
        // Update total count - show only future flights count
        int totalFutureFlights = 0;
        for (Flight f : allFlights) {
            if (!f.isDeleted() && !f.hasDeparted(realTimeDate)) {
                totalFutureFlights++;
            }
        }
        totalCountLabel.setText("Total Flights: " + totalFutureFlights);
        
        String[] columns = {"ID", "Flight No", "Origin", "Destination", "Departure Date", "Capacity", "Booked", "Available", "Current Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        for (Flight flight : futureFlights) {
            int booked = flight.getPassengers().size();
            int available = flight.getCapacity() - booked;
            double dynamicPrice = flight.calculateDynamicPrice(realTimeDate);
            model.addRow(new Object[]{
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate().format(formatter),
                flight.getCapacity(),
                booked,
                available,
                String.format("£%.2f", dynamicPrice)
            });
        }

        currentTable = new JTable(model);
        styleTable(currentTable);
        
        // Add double-click listener for flight details
        currentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = currentTable.getSelectedRow();
                    if (row != -1) {
                        int flightId = (Integer) currentTable.getValueAt(row, 0);
                        showFlightDetails(flightId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(currentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.revalidate();
        mainPanel.repaint();
        
        statusLabel.setText(futureFlights.size() + " upcoming flight(s) | Today: " + 
            realTimeDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")) + 
            " | Prices shown are current rates.");
    }

    public void displayCustomers() {
        displayCustomersWithFilter(null, null, null, null);
    }
    
    private void displayCustomersWithFilter(Integer customerIdFilter, String nameFilter, String phoneFilter, String emailFilter) {
        mainPanel.removeAll();
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel headerLabel = new JLabel("Customers");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(TABLE_HEADER_COLOR);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Total customers count label
        totalCountLabel = new JLabel();
        totalCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalCountLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(totalCountLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create search/filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel customerIdLabel = new JLabel("Customer ID:");
        customerIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField customerIdField = new JTextField(customerIdFilter != null ? customerIdFilter.toString() : "", 7);
        
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField nameField = new JTextField(nameFilter != null ? nameFilter : "", 15);
        
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField phoneField = new JTextField(phoneFilter != null ? phoneFilter : "", 12);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField emailField = new JTextField(emailFilter != null ? emailFilter : "", 20);
        
        JButton searchBtn = new JButton("Search/Filter");
        searchBtn.setBackground(PRIMARY_COLOR);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBackground(new Color(149, 165, 166));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        
        searchBtn.addActionListener(e -> {
            String customerIdStr = customerIdField.getText().trim();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            
            Integer customerIdVal = null;
            try {
                if (!customerIdStr.isEmpty()) {
                    customerIdVal = Integer.parseInt(customerIdStr);
                }
                displayCustomersWithFilter(
                    customerIdVal,
                    name.isEmpty() ? null : name,
                    phone.isEmpty() ? null : phone,
                    email.isEmpty() ? null : email
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainWindow.this, 
                    "Customer ID must be a number", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        clearBtn.addActionListener(e -> {
            displayCustomersWithFilter(null, null, null, null);
        });
        
        filterPanel.add(customerIdLabel);
        filterPanel.add(customerIdField);
        filterPanel.add(nameLabel);
        filterPanel.add(nameField);
        filterPanel.add(phoneLabel);
        filterPanel.add(phoneField);
        filterPanel.add(emailLabel);
        filterPanel.add(emailField);
        filterPanel.add(searchBtn);
        filterPanel.add(clearBtn);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        List<Customer> customersList = fbs.getCustomers();
        
        // Filter customers
        java.util.List<Customer> filteredCustomers = new java.util.ArrayList<>();
        for (Customer c : customersList) {
            if (!c.isDeleted()) {
                boolean matchesCustomerId = customerIdFilter == null || 
                    c.getId() == customerIdFilter.intValue();
                boolean matchesName = nameFilter == null || 
                    c.getName().toLowerCase().contains(nameFilter.toLowerCase());
                boolean matchesPhone = phoneFilter == null || 
                    c.getPhone().toLowerCase().contains(phoneFilter.toLowerCase());
                boolean matchesEmail = emailFilter == null || 
                    c.getEmail().toLowerCase().contains(emailFilter.toLowerCase());
                    
                if (matchesCustomerId && matchesName && matchesPhone && matchesEmail) {
                    filteredCustomers.add(c);
                }
            }
        }
        
        // Count total non-deleted customers
        int totalCustomers = 0;
        for (Customer c : customersList) {
            if (!c.isDeleted()) totalCustomers++;
        }
        totalCountLabel.setText("Total Customers: " + totalCustomers);
        
        String[] columns = {"ID", "Name", "Phone", "Email", "Total Bookings", "Active Bookings"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Customer customer : filteredCustomers) {
            int totalBookings = customer.getBookings().size();
            int activeBookings = 0;
            for (Booking b : customer.getBookings()) {
                if (!b.isCancelled()) activeBookings++;
            }
            
            model.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                totalBookings,
                activeBookings
            });
        }

        currentTable = new JTable(model);
        styleTable(currentTable);
        
        // Add double-click listener for customer details
        currentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = currentTable.getSelectedRow();
                    if (row != -1) {
                        int customerId = (Integer) currentTable.getValueAt(row, 0);
                        showCustomerDetails(customerId);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(currentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.revalidate();
        mainPanel.repaint();
        
        statusLabel.setText(model.getRowCount() + " customer(s) displayed | Double-click any row to view details");
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(189, 195, 199));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom header renderer
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new TableCellRenderer() {
            private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = renderer.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                c.setBackground(TABLE_HEADER_COLOR);
                c.setForeground(Color.WHITE);
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void displayTable(DefaultTableModel model, String title, String statusText, String tooltip) {
        currentTable = new JTable(model);
        currentTable.setRowHeight(30);
        currentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        currentTable.setSelectionBackground(SECONDARY_COLOR);
        currentTable.setSelectionForeground(Color.WHITE);
        currentTable.setGridColor(new Color(189, 195, 199));
        currentTable.setShowGrid(true);
        currentTable.setIntercellSpacing(new Dimension(1, 1));
        currentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // FIXED: Custom header renderer to force correct colors
        JTableHeader header = currentTable.getTableHeader();
        header.setDefaultRenderer(new TableCellRenderer() {
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
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < currentTable.getColumnCount(); i++) {
            currentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add double-click listener
        currentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = currentTable.getSelectedRow();
                    if (row != -1) {
                        int id = (Integer) currentTable.getValueAt(row, 0);
                        if (title.contains("Flight")) {
                            showFlightDetails(id);
                        } else {
                            showCustomerDetails(id);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(currentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            title,
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(236, 240, 241));
        infoPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel infoLabel = new JLabel(tooltip);
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(127, 140, 141));
        infoPanel.add(infoLabel);

        mainPanel.removeAll();
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.revalidate();
        mainPanel.repaint();
        
        statusLabel.setText(statusText);
    }

    private void showFlightDetails(int flightId) {
        try {
            Flight flight = fbs.getFlightByID(flightId);
            if (flight == null) return;
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
            infoPanel.setBackground(Color.WHITE);
            addDetailRow(infoPanel, "Flight ID:", String.valueOf(flight.getId()));
            addDetailRow(infoPanel, "Flight Number:", flight.getFlightNumber());
            addDetailRow(infoPanel, "Route:", flight.getOrigin() + " → " + flight.getDestination());
            addDetailRow(infoPanel, "Departure Date:", flight.getDepartureDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            addDetailRow(infoPanel, "Total Capacity:", String.valueOf(flight.getCapacity()));
            addDetailRow(infoPanel, "Seats Booked:", String.valueOf(flight.getPassengers().size()));
            addDetailRow(infoPanel, "Seats Available:", String.valueOf(flight.getCapacity() - flight.getPassengers().size()));
            addDetailRow(infoPanel, "Base Price:", String.format("£%.2f", flight.getPrice()));
            
            panel.add(infoPanel, BorderLayout.NORTH);
            
            JPanel passPanel = new JPanel(new BorderLayout());
            passPanel.setBorder(BorderFactory.createTitledBorder("Passenger List"));
            
            if (flight.getPassengers().isEmpty()) {
                JLabel noPass = new JLabel("No passengers booked yet");
                noPass.setHorizontalAlignment(SwingConstants.CENTER);
                noPass.setForeground(Color.GRAY);
                passPanel.add(noPass);
            } else {
                String[] cols = {"ID", "Name", "Phone", "Email"};
                DefaultTableModel passModel = new DefaultTableModel(cols, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                
                for (Customer p : flight.getPassengers()) {
                    passModel.addRow(new Object[]{p.getId(), p.getName(), p.getPhone(), p.getEmail()});
                }
                
                JTable passTable = new JTable(passModel);
                passTable.setEnabled(false);
                
                // Apply same styling as view flights table using styleTable method
                styleTable(passTable);
                
                // Set column widths
                passTable.getColumnModel().getColumn(0).setPreferredWidth(50);
                passTable.getColumnModel().getColumn(1).setPreferredWidth(150);
                passTable.getColumnModel().getColumn(2).setPreferredWidth(120);
                passTable.getColumnModel().getColumn(3).setPreferredWidth(200);
                
                // Set preferred size for the scroll pane based on number of rows
                // Calculate height: header (35px as per styleTable) + rows (30px each) + small buffer
                int numRows = passModel.getRowCount();
                int tableHeight = 35 + (numRows * 30) + 5;
                // Cap the height at a reasonable maximum
                tableHeight = Math.min(tableHeight, 250);
                
                JScrollPane passScrollPane = new JScrollPane(passTable);
                passScrollPane.setBorder(BorderFactory.createEmptyBorder());
                passScrollPane.setPreferredSize(new Dimension(520, tableHeight));
                passPanel.add(passScrollPane);
            }
            
            panel.add(passPanel, BorderLayout.CENTER);
            
            JOptionPane.showMessageDialog(this, panel, "Flight Details - " + flight.getFlightNumber(), 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showCustomerDetails(int customerId) {
        try {
            Customer customer = fbs.getCustomerByID(customerId);
            if (customer == null) return;
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 8));
            infoPanel.setBackground(Color.WHITE);
            addDetailRow(infoPanel, "Customer ID:", String.valueOf(customer.getId()));
            addDetailRow(infoPanel, "Name:", customer.getName());
            addDetailRow(infoPanel, "Phone:", customer.getPhone());
            addDetailRow(infoPanel, "Email:", customer.getEmail());
            addDetailRow(infoPanel, "Status:", customer.isDeleted() ? "Deleted" : "Active");
            addDetailRow(infoPanel, "Total Bookings:", String.valueOf(customer.getBookings().size()));
            
            int active = 0;
            for (Booking b : customer.getBookings()) {
                if (!b.isCancelled()) active++;
            }
            addDetailRow(infoPanel, "Active Bookings:", String.valueOf(active));
            
            panel.add(infoPanel, BorderLayout.NORTH);
            
            JPanel bookPanel = new JPanel(new BorderLayout());
            bookPanel.setBorder(BorderFactory.createTitledBorder("Booking History"));
            
            if (customer.getBookings().isEmpty()) {
                JLabel noBook = new JLabel("No bookings yet");
                noBook.setHorizontalAlignment(SwingConstants.CENTER);
                noBook.setForeground(Color.GRAY);
                bookPanel.add(noBook);
            } else {
                String[] cols = {"Flight", "Route", "Date", "Price", "Status"};
                DefaultTableModel bookModel = new DefaultTableModel(cols, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                
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
                final java.util.Map<Integer, Color> rowColors = new java.util.HashMap<>();
                final java.util.Set<Integer> bookingEndRows = new java.util.HashSet<>();
                int currentRow = 0;
                
                for (Booking b : customer.getBookings()) {
                    if (processedBookings.contains(b)) {
                        continue;
                    }
                    
                    if (b.isRoundTrip()) {
                        // This is a round trip - add both outbound and return as consecutive rows
                        Color roundTripColor = roundTripColors[colorIndex % roundTripColors.length];
                        colorIndex++;
                        
                        // Add outbound flight row
                        Flight outbound = b.getOutboundFlight();
                        String outboundRoute = outbound.getOrigin() + " → " + outbound.getDestination();
                        bookModel.addRow(new Object[]{
                            outbound.getFlightNumber(),
                            outboundRoute,
                            b.getBookingDate(),
                            String.format("£%.2f", b.getPrice()),
                            b.getStatus().toString()
                        });
                        rowColors.put(currentRow, roundTripColor);
                        currentRow++;
                        
                        // Add return flight row
                        Flight returnFlight = b.getReturnFlight();
                        String returnRoute = returnFlight.getOrigin() + " → " + returnFlight.getDestination();
                        bookModel.addRow(new Object[]{
                            returnFlight.getFlightNumber(),
                            returnRoute,
                            "-",
                            "-",
                            "-"
                        });
                        rowColors.put(currentRow, roundTripColor);
                        currentRow++;
                        
                        processedBookings.add(b);
                        bookingEndRows.add(currentRow - 1);
                    } else {
                        // One-way booking
                        Flight flight = b.getOutboundFlight();
                        String route = flight.getOrigin() + " → " + flight.getDestination();
                        bookModel.addRow(new Object[]{
                            flight.getFlightNumber(),
                            route,
                            b.getBookingDate(),
                            String.format("£%.2f", b.getPrice()),
                            b.getStatus().toString()
                        });
                        currentRow++;
                        bookingEndRows.add(currentRow - 1);
                    }
                }
                
                JTable bookTable = new JTable(bookModel);
                
                // Style the table
                bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                bookTable.setRowHeight(28);
                bookTable.setGridColor(new Color(220, 220, 220));
                bookTable.setShowGrid(true);
                bookTable.setIntercellSpacing(new Dimension(1, 1));
                
                // Style the header with improved design
                JTableHeader header = bookTable.getTableHeader();
                header.setDefaultRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        JLabel label = new JLabel(value.toString());
                        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        label.setBackground(TABLE_HEADER_COLOR);
                        label.setForeground(Color.WHITE);
                        label.setHorizontalAlignment(JLabel.CENTER);
                        label.setOpaque(true);
                        label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                            BorderFactory.createEmptyBorder(8, 5, 8, 5)
                        ));
                        return label;
                    }
                });
                header.setPreferredSize(new Dimension(header.getWidth(), 40));
                header.setReorderingAllowed(false);
                
                // Custom cell renderer for row coloring
                TableCellRenderer colorRenderer = new DefaultTableCellRenderer() {
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
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(SECONDARY_COLOR);
                            c.setForeground(Color.WHITE);
                        }
                        
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                        ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                        
                        // Add darker bottom border for booking end rows
                        if (bookingEndRows.contains(row)) {
                            ((JLabel) c).setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(100, 100, 100)),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)
                            ));
                        }
                        
                        return c;
                    }
                };
                
                // Apply custom renderer to all columns
                for (int i = 0; i < bookTable.getColumnCount(); i++) {
                    bookTable.getColumnModel().getColumn(i).setCellRenderer(colorRenderer);
                }
                
                bookTable.setEnabled(false);
                JScrollPane scrollPane = new JScrollPane(bookTable);
                scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                bookPanel.add(scrollPane);
            }
            
            panel.add(bookPanel, BorderLayout.CENTER);
            
            JOptionPane.showMessageDialog(this, panel, "Customer Details - " + customer.getName(), 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lblLabel);
        panel.add(lblValue);
    }

    /**
     * Opens a dialog for deleting a flight.
     * Prompts admin to enter a Flight ID and validates the input before deletion.
     * Validates that input is not empty and is a valid number.
     */
    private void deleteFlightDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Flight ID to delete:", 
            "Delete Flight", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            if (input.trim().isEmpty()) {
                showError("Please enter a Flight ID!");
                return;
            }
            try {
                int flightId = Integer.parseInt(input.trim());
                Command cmd = new DeleteFlight(flightId);
                cmd.execute(fbs);
                displayFlights();
                showSuccess("Flight deleted successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid Flight ID (number)!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Opens a dialog for issuing a new booking.
     * Allows admin to create one-way or round-trip bookings for customers.
     * Validates all required fields are filled and contain valid numeric IDs.
     */
    private void issueBookingDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextField custIdField = new JTextField();
        JTextField outboundFlightIdField = new JTextField();
        JTextField returnFlightIdField = new JTextField();
        JCheckBox roundTripCheckBox = new JCheckBox("Round-trip booking");
        
        panel.add(new JLabel("Customer ID:"));
        panel.add(custIdField);
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
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Issue Booking", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate empty fields first
                if (custIdField.getText().trim().isEmpty() || outboundFlightIdField.getText().trim().isEmpty()) {
                    showError("Please fill in all required fields!");
                    return;
                }
                
                if (roundTripCheckBox.isSelected() && returnFlightIdField.getText().trim().isEmpty()) {
                    showError("Please enter Return Flight ID for round-trip booking!");
                    return;
                }
                
                int custId = Integer.parseInt(custIdField.getText().trim());
                int outboundFlightId = Integer.parseInt(outboundFlightIdField.getText().trim());
                
                Command cmd;
                if (roundTripCheckBox.isSelected() && !returnFlightIdField.getText().trim().isEmpty()) {
                    int returnFlightId = Integer.parseInt(returnFlightIdField.getText().trim());
                    cmd = new AddBooking(custId, outboundFlightId, returnFlightId);
                } else {
                    cmd = new AddBooking(custId, outboundFlightId);
                }
                
                cmd.execute(fbs);
                showSuccess("Booking issued successfully!\nType: " + 
                    (roundTripCheckBox.isSelected() && !returnFlightIdField.getText().trim().isEmpty() 
                        ? "Round-trip" : "One-way"));
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for Customer ID and Flight ID!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Opens a dialog for updating an existing booking.
     * Allows admin to change the flight for a customer's booking.
     * Validates all required fields are filled and contain valid numeric IDs.
     * Note: Round-trip bookings cannot be updated.
     */
    private void updateBookingDialog() {
        // Create custom dialog for better control over spacing
        JDialog dialog = new JDialog(this, "Update Booking", true);
        dialog.setLayout(new BorderLayout());
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 15, 10)); // Added extra bottom padding
        
        JTextField custIdField = new JTextField();
        JTextField oldFlightField = new JTextField();
        JTextField newFlightField = new JTextField();
        contentPanel.add(new JLabel("Customer ID:"));
        contentPanel.add(custIdField);
        contentPanel.add(new JLabel("Current Flight ID:"));
        contentPanel.add(oldFlightField);
        contentPanel.add(new JLabel("New Flight ID:"));
        contentPanel.add(newFlightField);
        
        JLabel warningLabel = new JLabel("<html><i>Note: Round-trip bookings cannot be updated.</i></html>");
        warningLabel.setForeground(new Color(192, 57, 43));
        contentPanel.add(new JLabel(""));
        contentPanel.add(warningLabel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        // Button panel with bottom padding
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
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
        
        dialog.setMinimumSize(new Dimension(450, 200));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        // Process the result after dialog closes
        if (okClicked[0]) {
            try {
                // Validate empty fields first
                if (custIdField.getText().trim().isEmpty() || 
                    oldFlightField.getText().trim().isEmpty() || 
                    newFlightField.getText().trim().isEmpty()) {
                    showError("Please fill in all required fields!");
                    return;
                }
                
                int custId = Integer.parseInt(custIdField.getText().trim());
                int oldFlight = Integer.parseInt(oldFlightField.getText().trim());
                int newFlight = Integer.parseInt(newFlightField.getText().trim());
                Command cmd = new UpdateBooking(custId, oldFlight, newFlight);
                cmd.execute(fbs);
                showSuccess("Booking updated successfully!\nUpdate fee: £15.00");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for all fields!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Opens a dialog for canceling a booking.
     * Prompts for booking type (one-way or round-trip) and required flight IDs.
     * Validates all required fields are filled and contain valid numeric IDs.
     * Applies appropriate cancellation fees (£25 for one-way, £50 for round-trip).
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
        
        JPanel panel = new JPanel(new GridLayout(isRoundTrip ? 3 : 2, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextField custIdField = new JTextField();
        JTextField outboundFlightIdField = new JTextField();
        panel.add(new JLabel("Customer ID:"));
        panel.add(custIdField);
        panel.add(new JLabel("Outbound Flight ID:"));
        panel.add(outboundFlightIdField);
        
        JTextField returnFlightIdField = null;
        if (isRoundTrip) {
            returnFlightIdField = new JTextField();
            panel.add(new JLabel("Return Flight ID:"));
            panel.add(returnFlightIdField);
        }
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Cancel Booking", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate empty fields first
                if (custIdField.getText().trim().isEmpty() || 
                    outboundFlightIdField.getText().trim().isEmpty()) {
                    showError("Please fill in all required fields!");
                    return;
                }
                
                if (isRoundTrip && returnFlightIdField.getText().trim().isEmpty()) {
                    showError("Please enter Return Flight ID for round-trip cancellation!");
                    return;
                }
                
                int custId = Integer.parseInt(custIdField.getText().trim());
                int outboundFlightId = Integer.parseInt(outboundFlightIdField.getText().trim());
                Integer returnFlightId = null;
                
                if (isRoundTrip) {
                    returnFlightId = Integer.parseInt(returnFlightIdField.getText().trim());
                }
                
                double fee = isRoundTrip ? 50.0 : 25.0;
                Command cmd = new CancelBooking(custId, outboundFlightId, returnFlightId);
                cmd.execute(fbs);
                showSuccess(String.format("Booking cancelled!\nCancellation fee: £%.2f", fee));
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for all fields!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void addCustomerDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email Address:"));
        panel.add(emailField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Customer", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                
                // Validate inputs
                if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                    showError("All fields are required!");
                    return;
                }
                
                // Validate phone format
                if (!ValidationUtil.isValidPhone(phone)) {
                    showError(ValidationUtil.getPhoneErrorMessage());
                    return;
                }
                
                // Validate email format - accept all valid emails
                if (!ValidationUtil.isValidEmail(email)) {
                    showError(ValidationUtil.getEmailErrorMessage(false));
                    return;
                }
                
                int maxId = 0;
                for (Customer c : fbs.getCustomers()) {
                    if (c.getId() > maxId) maxId = c.getId();
                }
                
                // Create customer - this will validate uniqueness
                Customer customer = new Customer(maxId + 1, name, phone, email);
                fbs.addCustomer(customer);
                
                // Create default user account with username = lowercase first name, password = username123
                String firstName = name.split(" ")[0].toLowerCase();
                String defaultUsername = firstName;
                String defaultPassword = firstName + "123";
                
                // Register user account
                fbs.getAuthService().registerCustomer(defaultUsername, defaultPassword, customer.getId());
                
                FlightBookingSystemData.store(fbs);
                displayCustomers();
                showSuccess("Customer added successfully!\nCustomer ID: " + customer.getId() + 
                           "\nDefault username: " + defaultUsername + 
                           "\nDefault password: " + defaultPassword);
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Opens a dialog for deleting a customer.
     * Prompts admin to enter a Customer ID and validates the input before deletion.
     * Validates that input is not empty and is a valid number.
     */
    private void deleteCustomerDialog() {
        String input = JOptionPane.showInputDialog(this, "Enter Customer ID to delete:", 
            "Delete Customer", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            if (input.trim().isEmpty()) {
                showError("Please enter a Customer ID!");
                return;
            }
            try {
                int custId = Integer.parseInt(input.trim());
                Command cmd = new DeleteCustomer(custId);
                cmd.execute(fbs);
                displayCustomers();
                showSuccess("Customer deleted successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter a valid Customer ID (number)!");
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void logoutAdmin() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?\n\nThe admin window will close and you'll need to login again.",
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

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?\n\n" +
            "The admin window will close but the console will remain active.",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                FlightBookingSystemData.store(fbs);
                JOptionPane.showMessageDialog(this,
                    "Thank you for using Flight Booking System!\n" +
                    "Your data has been saved successfully.\n\n" +
                    "Have a wonderful day, Administrator!",
                    "Goodbye",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Error saving data: " + ex.getMessage());
            }
            dispose(); // Close GUI window but keep console running
        }
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
