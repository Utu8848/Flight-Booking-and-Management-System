package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FlightBookingSystem.
 * Tests system-level operations including flight and customer management.
 */
public class FlightBookingSystemTest {
    
    /**
     * Default constructor.
     */
    public FlightBookingSystemTest() {
    }
    
    private FlightBookingSystem fbs;
    private Flight flight1;
    private Flight flight2;
    private Customer customer1;
    private Customer customer2;
    
    @BeforeEach
    /** Set up test fixtures. */
    public void setUp() {
        fbs = new FlightBookingSystem(true); // Test mode
        
        flight1 = new Flight(1, "BA101", "London", "Paris", LocalDate.of(2025, 3, 15));
        flight2 = new Flight(2, "BA102", "Paris", "London", LocalDate.of(2025, 3, 22));
        
        customer1 = new Customer(1, "Aarav Sharma", "9841234567", "aarav@gmail.com");
        customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
    }
    
    @AfterEach
    /** Clean up test fixtures. */
    public void tearDown() {
        fbs = null;
        flight1 = null;
        flight2 = null;
        customer1 = null;
        customer2 = null;
    }
    
    // Constructor Tests
    @Test
    @DisplayName("Default constructor should initialize system")
    /** Test method. */
    public void testDefaultConstructor() {
        FlightBookingSystem system = new FlightBookingSystem();
        assertNotNull(system);
        assertNotNull(system.getSystemDate());
        assertNotNull(system.getFlights());
        assertNotNull(system.getCustomers());
    }
    
    @Test
    @DisplayName("Test mode constructor should set test mode")
    /** Test method. */
    public void testTestModeConstructor() {
        assertTrue(fbs.isTestMode());
    }
    
    @Test
    @DisplayName("System date should be initialized")
    /** Test method. */
    public void testSystemDateInitialized() {
        assertNotNull(fbs.getSystemDate());
    }
    
    // System Date Tests
    @Test
    @DisplayName("getSystemDate should return current date initially")
    /** Test method. */
    public void testGetSystemDate() {
        LocalDate systemDate = fbs.getSystemDate();
        assertNotNull(systemDate);
    }
    
    @Test
    @DisplayName("setSystemDate should update system date")
    /** Test method. */
    public void testSetSystemDate() {
        LocalDate newDate = LocalDate.of(2025, 12, 31);
        fbs.setSystemDate(newDate);
        assertEquals(newDate, fbs.getSystemDate());
    }
    
    @Test
    @DisplayName("getCurrentNepalDate should return valid date")
    /** Test method. */
    public void testGetCurrentNepalDate() {
        LocalDate nepalDate = FlightBookingSystem.getCurrentNepalDate();
        assertNotNull(nepalDate);
    }
    
    @Test
    @DisplayName("getNepalZone should return correct timezone")
    /** Test method. */
    public void testGetNepalZone() {
        assertEquals("Asia/Kathmandu", FlightBookingSystem.getNepalZone().getId());
    }
    
    // Authentication Service Tests
    @Test
    @DisplayName("getAuthService should return authentication service")
    /** Test method. */
    public void testGetAuthService() {
        assertNotNull(fbs.getAuthService());
    }
    
    // Flight Management Tests
    @Test
    @DisplayName("getFlights should return empty list initially")
    /** Test method. */
    public void testGetFlightsEmpty() {
        List<Flight> flights = fbs.getFlights();
        assertNotNull(flights);
        assertTrue(flights.isEmpty());
    }
    
    @Test
    @DisplayName("addFlight should add flight to system")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddFlight() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        assertEquals(1, fbs.getFlights().size());
        assertTrue(fbs.getFlights().contains(flight1));
    }
    
    @Test
    @DisplayName("addFlight should add multiple flights")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddMultipleFlights() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        fbs.addFlight(flight2);
        assertEquals(2, fbs.getFlights().size());
    }
    
    @Test
    @DisplayName("addFlight should throw exception for duplicate ID")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddFlightDuplicateId() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        Flight duplicateFlight = new Flight(1, "BA999", "New York", "London", LocalDate.of(2025, 4, 1));
        
        assertThrows(IllegalArgumentException.class, () -> {
            fbs.addFlight(duplicateFlight);
        });
    }
    
    @Test
    @DisplayName("addFlight should allow same flight number for different flights")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddFlightSameFlightNumber() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        Flight sameFlight = new Flight(3, "BA101", "London", "Berlin", LocalDate.of(2025, 3, 20));
        
        assertDoesNotThrow(() -> fbs.addFlight(sameFlight));
        assertEquals(2, fbs.getFlights().size());
    }
    
    @Test
    @DisplayName("getFlightByID should return correct flight")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testGetFlightByID() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        Flight retrieved = fbs.getFlightByID(1);
        assertEquals(flight1, retrieved);
    }
    
    @Test
    @DisplayName("getFlightByID should return null for non-existent ID")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testGetFlightByIDNotFound() throws FlightBookingSystemException {
        assertNull(fbs.getFlightByID(999));
    }
    
    @Test
    @DisplayName("getFlights should return unmodifiable list")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testGetFlightsUnmodifiable() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        List<Flight> flights = fbs.getFlights();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            flights.add(flight2);
        });
    }
    
    // Customer Management Tests
    @Test
    @DisplayName("getCustomers should return empty list initially")
    /** Test method. */
    public void testGetCustomersEmpty() {
        List<Customer> customers = fbs.getCustomers();
        assertNotNull(customers);
        assertTrue(customers.isEmpty());
    }
    
    @Test
    @DisplayName("addCustomer should add customer to system")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddCustomer() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        assertEquals(1, fbs.getCustomers().size());
        assertTrue(fbs.getCustomers().contains(customer1));
    }
    
    @Test
    @DisplayName("addCustomer should add multiple customers")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddMultipleCustomers() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        fbs.addCustomer(customer2);
        assertEquals(2, fbs.getCustomers().size());
    }
    
    @Test
    @DisplayName("addCustomer should throw exception for duplicate ID")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddCustomerDuplicateId() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        Customer duplicateCustomer = new Customer(1, "Different Name", "9999999999", "different@gmail.com");
        
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> fbs.addCustomer(duplicateCustomer)
        );
        assertTrue(exception.getMessage().contains("Duplicate customer ID"));
    }
    
    @Test
    @DisplayName("addCustomer should throw exception for duplicate phone")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddCustomerDuplicatePhone() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        Customer duplicatePhone = new Customer(3, "Different Name", "9841234567", "different@gmail.com");
        
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> fbs.addCustomer(duplicatePhone)
        );
        assertTrue(exception.getMessage().contains("phone number"));
    }
    
    @Test
    @DisplayName("addCustomer should throw exception for duplicate email")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddCustomerDuplicateEmail() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        Customer duplicateEmail = new Customer(3, "Different Name", "9999999999", "aarav@gmail.com");
        
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> fbs.addCustomer(duplicateEmail)
        );
        assertTrue(exception.getMessage().contains("email"));
    }
    
    @Test
    @DisplayName("addCustomer should allow same phone/email for deleted customers")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddCustomerAllowDuplicateForDeleted() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        customer1.setDeleted(true);
        
        Customer newCustomer = new Customer(3, "New Customer", "9841234567", "aarav@gmail.com");
        assertDoesNotThrow(() -> fbs.addCustomer(newCustomer));
    }
    
    @Test
    @DisplayName("addCustomer should be case-insensitive for email")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testAddCustomerEmailCaseInsensitive() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        Customer upperCaseEmail = new Customer(3, "Different Name", "9999999999", "AARAV@GMAIL.COM");
        
        assertThrows(FlightBookingSystemException.class, () -> {
            fbs.addCustomer(upperCaseEmail);
        });
    }
    
    @Test
    @DisplayName("getCustomerByID should return correct customer")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testGetCustomerByID() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        Customer retrieved = fbs.getCustomerByID(1);
        assertEquals(customer1, retrieved);
    }
    
    @Test
    @DisplayName("getCustomerByID should return null for non-existent ID")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testGetCustomerByIDNotFound() throws FlightBookingSystemException {
        assertNull(fbs.getCustomerByID(999));
    }
    
    @Test
    @DisplayName("getCustomers should return unmodifiable list")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testGetCustomersUnmodifiable() throws FlightBookingSystemException {
        fbs.addCustomer(customer1);
        List<Customer> customers = fbs.getCustomers();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            customers.add(customer2);
        });
    }
    
    // Integration Tests
    @Test
    @DisplayName("System should handle both flights and customers")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testSystemHandlesBothFlightsAndCustomers() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        fbs.addFlight(flight2);
        fbs.addCustomer(customer1);
        fbs.addCustomer(customer2);
        
        assertEquals(2, fbs.getFlights().size());
        assertEquals(2, fbs.getCustomers().size());
    }
    
    @Test
    @DisplayName("System should maintain independent flight and customer lists")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testIndependentFlightAndCustomerLists() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        assertEquals(1, fbs.getFlights().size());
        assertEquals(0, fbs.getCustomers().size());
        
        fbs.addCustomer(customer1);
        assertEquals(1, fbs.getFlights().size());
        assertEquals(1, fbs.getCustomers().size());
    }
    
    @Test
    @DisplayName("System should handle large number of flights")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testSystemHandleManyFlights() throws FlightBookingSystemException {
        for (int i = 1; i <= 100; i++) {
            Flight f = new Flight(i, "BA" + i, "London", "Paris", LocalDate.of(2025, 3, i % 28 + 1));
            fbs.addFlight(f);
        }
        assertEquals(100, fbs.getFlights().size());
    }
    
    @Test
    @DisplayName("System should handle large number of customers")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testSystemHandleManyCustomers() throws FlightBookingSystemException {
        for (int i = 1; i <= 100; i++) {
            // Generate unique phone numbers by padding with zeros
            String phone = String.format("98%08d", i);
            Customer c = new Customer(i, "Customer" + i, phone, "c" + i + "@example.com");
            fbs.addCustomer(c);
        }
        assertEquals(100, fbs.getCustomers().size());
    }
    
    @Test
    @DisplayName("System date changes should not affect stored data")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testSystemDateChangeDoesNotAffectData() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        fbs.addCustomer(customer1);
        
        LocalDate newDate = LocalDate.of(2026, 1, 1);
        fbs.setSystemDate(newDate);
        
        assertEquals(1, fbs.getFlights().size());
        assertEquals(1, fbs.getCustomers().size());
        assertEquals(newDate, fbs.getSystemDate());
    }
    
    @Test
    @DisplayName("Test mode should be independent of data operations")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testTestModeIndependentOfData() throws FlightBookingSystemException {
        assertTrue(fbs.isTestMode());
        
        fbs.addFlight(flight1);
        fbs.addCustomer(customer1);
        
        assertTrue(fbs.isTestMode());
        assertEquals(1, fbs.getFlights().size());
        assertEquals(1, fbs.getCustomers().size());
    }
    
    @Test
    @DisplayName("System should retrieve flights and customers correctly after multiple operations")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testRetrievalAfterMultipleOperations() throws FlightBookingSystemException {
        fbs.addFlight(flight1);
        fbs.addCustomer(customer1);
        fbs.addFlight(flight2);
        fbs.addCustomer(customer2);
        
        assertEquals(flight1, fbs.getFlightByID(1));
        assertEquals(flight2, fbs.getFlightByID(2));
        assertEquals(customer1, fbs.getCustomerByID(1));
        assertEquals(customer2, fbs.getCustomerByID(2));
    }
    
    @Test
    @DisplayName("System should handle edge case of ID 0")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testHandleIdZero() throws FlightBookingSystemException {
        Flight zeroFlight = new Flight(0, "BA000", "London", "Paris", LocalDate.of(2025, 3, 15));
        Customer zeroCustomer = new Customer(0, "Zero Customer", "9841234567", "zero@gmail.com");
        
        fbs.addFlight(zeroFlight);
        fbs.addCustomer(zeroCustomer);
        
        assertEquals(zeroFlight, fbs.getFlightByID(0));
        assertEquals(zeroCustomer, fbs.getCustomerByID(0));
    }
    
    @Test
    @DisplayName("System should handle negative IDs")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testHandleNegativeIds() throws FlightBookingSystemException {
        Flight negativeFlight = new Flight(-1, "BA-1", "London", "Paris", LocalDate.of(2025, 3, 15));
        Customer negativeCustomer = new Customer(-1, "Negative Customer", "9841234567", "negative@gmail.com");
        
        fbs.addFlight(negativeFlight);
        fbs.addCustomer(negativeCustomer);
        
        assertEquals(negativeFlight, fbs.getFlightByID(-1));
        assertEquals(negativeCustomer, fbs.getCustomerByID(-1));
    }
}
