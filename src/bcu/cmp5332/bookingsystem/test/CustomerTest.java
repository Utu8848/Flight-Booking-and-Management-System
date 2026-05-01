package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for Customer model.
 * Tests all customer functionality including constructors, getters, setters,
 * booking management, and detail formatting.
 */
public class CustomerTest {
    
    /**
     * Default constructor.
     */
    public CustomerTest() {
    }
    
    private Customer customer;
    private Flight flight;
    private Booking booking;
    
    @BeforeEach
    /** Set up test fixtures. */
    public void setUp() {
        customer = new Customer(1, "Aarav Sharma", "9841234567", "aarav@gmail.com");
        flight = new Flight(1, "BA101", "London", "Paris", LocalDate.of(2025, 3, 15), 150, 200.0);
        booking = new Booking(customer, flight, LocalDate.of(2025, 2, 1));
    }
    
    @AfterEach
    /** Clean up test fixtures. */
    public void tearDown() {
        customer = null;
        flight = null;
        booking = null;
    }
    
    // Constructor Tests
    @Test
    @DisplayName("Constructor should initialize customer with valid data")
    /** Test method. */
    public void testConstructorWithValidData() {
        assertEquals(1, customer.getId());
        assertEquals("Aarav Sharma", customer.getName());
        assertEquals("9841234567", customer.getPhone());
        assertEquals("aarav@gmail.com", customer.getEmail());
        assertFalse(customer.isDeleted());
        assertNotNull(customer.getBookings());
        assertTrue(customer.getBookings().isEmpty());
    }
    
    @Test
    @DisplayName("Constructor should create customer with empty bookings list")
    /** Test method. */
    public void testConstructorInitializesEmptyBookingsList() {
        Customer newCustomer = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        assertNotNull(newCustomer.getBookings());
        assertEquals(0, newCustomer.getBookings().size());
    }
    
    @Test
    @DisplayName("Constructor should set deleted status to false by default")
    /** Test method. */
    public void testConstructorSetsDeletedToFalse() {
        Customer newCustomer = new Customer(3, "Chiranjivi Gurung", "9861234567", "chiranjivi@gmail.com");
        assertFalse(newCustomer.isDeleted());
    }
    
    // ID Tests
    @Test
    @DisplayName("getId should return correct customer ID")
    /** Test method. */
    public void testGetId() {
        assertEquals(1, customer.getId());
    }
    
    @Test
    @DisplayName("setId should update customer ID")
    /** Test method. */
    public void testSetId() {
        customer.setId(100);
        assertEquals(100, customer.getId());
    }
    
    @Test
    @DisplayName("setId should handle negative IDs")
    /** Test method. */
    public void testSetIdWithNegativeValue() {
        customer.setId(-1);
        assertEquals(-1, customer.getId());
    }
    
    @Test
    @DisplayName("setId should handle zero ID")
    /** Test method. */
    public void testSetIdWithZero() {
        customer.setId(0);
        assertEquals(0, customer.getId());
    }
    
    // Name Tests
    @Test
    @DisplayName("getName should return correct customer name")
    /** Test method. */
    public void testGetName() {
        assertEquals("Aarav Sharma", customer.getName());
    }
    
    @Test
    @DisplayName("setName should update customer name")
    /** Test method. */
    public void testSetName() {
        customer.setName("Diya Thapa");
        assertEquals("Diya Thapa", customer.getName());
    }
    
    @Test
    @DisplayName("setName should handle empty string")
    /** Test method. */
    public void testSetNameWithEmptyString() {
        customer.setName("");
        assertEquals("", customer.getName());
    }
    
    @Test
    @DisplayName("setName should handle names with special characters")
    /** Test method. */
    public void testSetNameWithSpecialCharacters() {
        customer.setName("O'Brien-Smith");
        assertEquals("O'Brien-Smith", customer.getName());
    }
    
    @Test
    @DisplayName("setName should handle long names")
    /** Test method. */
    public void testSetNameWithLongName() {
        String longName = "Aarav Kumar Sharma Poudel Adhikari";
        customer.setName(longName);
        assertEquals(longName, customer.getName());
    }
    
    // Phone Tests
    @Test
    @DisplayName("getPhone should return correct phone number")
    /** Test method. */
    public void testGetPhone() {
        assertEquals("9841234567", customer.getPhone());
    }
    
    @Test
    @DisplayName("setPhone should update phone number")
    /** Test method. */
    public void testSetPhone() {
        customer.setPhone("9851234567");
        assertEquals("9851234567", customer.getPhone());
    }
    
    @Test
    @DisplayName("setPhone should handle different phone formats")
    /** Test method. */
    public void testSetPhoneWithDifferentFormat() {
        customer.setPhone("9801234567");
        assertEquals("9801234567", customer.getPhone());
    }
    
    @Test
    @DisplayName("setPhone should accept any string as phone")
    /** Test method. */
    public void testSetPhoneWithInvalidFormat() {
        customer.setPhone("invalid");
        assertEquals("invalid", customer.getPhone());
    }
    
    // Email Tests
    @Test
    @DisplayName("getEmail should return correct email address")
    /** Test method. */
    public void testGetEmail() {
        assertEquals("aarav@gmail.com", customer.getEmail());
    }
    
    @Test
    @DisplayName("setEmail should update email address")
    /** Test method. */
    public void testSetEmail() {
        customer.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", customer.getEmail());
    }
    
    @Test
    @DisplayName("setEmail should handle different email domains")
    /** Test method. */
    public void testSetEmailWithDifferentDomain() {
        customer.setEmail("customer@yahoo.com");
        assertEquals("customer@yahoo.com", customer.getEmail());
    }
    
    @Test
    @DisplayName("setEmail should accept any string as email")
    /** Test method. */
    public void testSetEmailWithInvalidFormat() {
        customer.setEmail("notanemail");
        assertEquals("notanemail", customer.getEmail());
    }
    
    // Deleted Status Tests
    @Test
    @DisplayName("isDeleted should return false for active customer")
    /** Test method. */
    public void testIsDeletedReturnsFalse() {
        assertFalse(customer.isDeleted());
    }
    
    @Test
    @DisplayName("setDeleted should mark customer as deleted")
    /** Test method. */
    public void testSetDeletedToTrue() {
        customer.setDeleted(true);
        assertTrue(customer.isDeleted());
    }
    
    @Test
    @DisplayName("setDeleted should mark customer as active")
    /** Test method. */
    public void testSetDeletedToFalse() {
        customer.setDeleted(true);
        customer.setDeleted(false);
        assertFalse(customer.isDeleted());
    }
    
    @Test
    @DisplayName("setDeleted should toggle status multiple times")
    /** Test method. */
    public void testSetDeletedToggleMultipleTimes() {
        customer.setDeleted(true);
        assertTrue(customer.isDeleted());
        customer.setDeleted(false);
        assertFalse(customer.isDeleted());
        customer.setDeleted(true);
        assertTrue(customer.isDeleted());
    }
    
    // Booking Management Tests
    @Test
    @DisplayName("getBookings should return empty list initially")
    /** Test method. */
    public void testGetBookingsReturnsEmptyListInitially() {
        Customer newCustomer = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        List<Booking> bookings = newCustomer.getBookings();
        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }
    
    @Test
    @DisplayName("getBookings should return defensive copy")
    /** Test method. */
    public void testGetBookingsReturnsDefensiveCopy() {
        customer.addBooking(booking);
        List<Booking> bookings1 = customer.getBookings();
        List<Booking> bookings2 = customer.getBookings();
        assertNotSame(bookings1, bookings2);
    }
    
    @Test
    @DisplayName("getBookings defensive copy should prevent external modification")
    /** Test method. */
    public void testGetBookingsDefensiveCopyPreventsModification() {
        customer.addBooking(booking);
        List<Booking> bookings = customer.getBookings();
        int originalSize = bookings.size();
        
        Flight newFlight = new Flight(2, "BA102", "Paris", "London", LocalDate.of(2025, 3, 20));
        Booking newBooking = new Booking(customer, newFlight, LocalDate.of(2025, 2, 5));
        
        assertThrows(UnsupportedOperationException.class, () -> {
            bookings.add(newBooking);
        });
        
        assertEquals(originalSize, customer.getBookings().size());
    }
    
    @Test
    @DisplayName("addBooking should add booking to customer")
    /** Test method. */
    public void testAddBooking() {
        customer.addBooking(booking);
        assertEquals(1, customer.getBookings().size());
        assertTrue(customer.getBookings().contains(booking));
    }
    
    @Test
    @DisplayName("addBooking should add multiple bookings")
    /** Test method. */
    public void testAddMultipleBookings() {
        Flight flight2 = new Flight(2, "BA102", "Paris", "London", LocalDate.of(2025, 3, 20));
        Booking booking2 = new Booking(customer, flight2, LocalDate.of(2025, 2, 5));
        
        customer.addBooking(booking);
        customer.addBooking(booking2);
        
        assertEquals(2, customer.getBookings().size());
        assertTrue(customer.getBookings().contains(booking));
        assertTrue(customer.getBookings().contains(booking2));
    }
    
    @Test
    @DisplayName("addBooking should handle same booking added multiple times")
    /** Test method. */
    public void testAddSameBookingMultipleTimes() {
        customer.addBooking(booking);
        customer.addBooking(booking);
        assertEquals(2, customer.getBookings().size());
    }
    
    @Test
    @DisplayName("cancelBooking should remove existing booking")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testCancelBookingSuccess() throws FlightBookingSystemException {
        customer.addBooking(booking);
        assertEquals(1, customer.getBookings().size());
        
        customer.cancelBooking(booking);
        assertEquals(0, customer.getBookings().size());
        assertFalse(customer.getBookings().contains(booking));
    }
    
    @Test
    @DisplayName("cancelBooking should throw exception for non-existent booking")
    /** Test method. */
    public void testCancelNonExistentBooking() {
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> customer.cancelBooking(booking)
        );
        assertEquals("Booking not found.", exception.getMessage());
    }
    
    @Test
    @DisplayName("cancelBooking should handle multiple bookings correctly")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testCancelBookingWithMultipleBookings() throws FlightBookingSystemException {
        Flight flight2 = new Flight(2, "BA102", "Paris", "London", LocalDate.of(2025, 3, 20));
        Booking booking2 = new Booking(customer, flight2, LocalDate.of(2025, 2, 5));
        
        customer.addBooking(booking);
        customer.addBooking(booking2);
        assertEquals(2, customer.getBookings().size());
        
        customer.cancelBooking(booking);
        assertEquals(1, customer.getBookings().size());
        assertFalse(customer.getBookings().contains(booking));
        assertTrue(customer.getBookings().contains(booking2));
    }
    
    @Test
    @DisplayName("cancelBooking should throw exception when cancelling same booking twice")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testCancelBookingTwice() throws FlightBookingSystemException {
        customer.addBooking(booking);
        customer.cancelBooking(booking);
        
        assertThrows(FlightBookingSystemException.class, () -> {
            customer.cancelBooking(booking);
        });
    }
    
    // Details Tests
    @Test
    @DisplayName("getDetailsShort should return correct format")
    /** Test method. */
    public void testGetDetailsShort() {
        String expected = "Customer #1 - Aarav Sharma - 9841234567 - aarav@gmail.com";
        assertEquals(expected, customer.getDetailsShort());
    }
    
    @Test
    @DisplayName("getDetailsShort should handle different customer data")
    /** Test method. */
    public void testGetDetailsShortWithDifferentData() {
        Customer customer2 = new Customer(100, "Binita Rai", "9851234567", "binita@yahoo.com");
        String expected = "Customer #100 - Binita Rai - 9851234567 - binita@yahoo.com";
        assertEquals(expected, customer2.getDetailsShort());
    }
    
    @Test
    @DisplayName("getDetailsLong should include customer information")
    /** Test method. */
    public void testGetDetailsLong() {
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Customer Details:"));
        assertTrue(details.contains("ID: 1"));
        assertTrue(details.contains("Name: Aarav Sharma"));
        assertTrue(details.contains("Phone: 9841234567"));
        assertTrue(details.contains("Email: aarav@gmail.com"));
        assertTrue(details.contains("Status: Active"));
        assertTrue(details.contains("Total Bookings: 0"));
    }
    
    @Test
    @DisplayName("getDetailsLong should show deleted status")
    /** Test method. */
    public void testGetDetailsLongWithDeletedStatus() {
        customer.setDeleted(true);
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Status: Deleted"));
    }
    
    @Test
    @DisplayName("getDetailsLong should show no bookings message")
    /** Test method. */
    public void testGetDetailsLongWithNoBookings() {
        String details = customer.getDetailsLong();
        assertTrue(details.contains("No bookings found."));
    }
    
    @Test
    @DisplayName("getDetailsLong should list bookings")
    /** Test method. */
    public void testGetDetailsLongWithBookings() {
        customer.addBooking(booking);
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Total Bookings: 1"));
        assertTrue(details.contains("Bookings:"));
        assertTrue(details.contains("BA101"));
    }
    
    @Test
    @DisplayName("getDetailsLong should list multiple bookings")
    /** Test method. */
    public void testGetDetailsLongWithMultipleBookings() {
        Flight flight2 = new Flight(2, "BA102", "Paris", "London", LocalDate.of(2025, 3, 20));
        Booking booking2 = new Booking(customer, flight2, LocalDate.of(2025, 2, 5));
        
        customer.addBooking(booking);
        customer.addBooking(booking2);
        
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Total Bookings: 2"));
        assertTrue(details.contains("BA101"));
        assertTrue(details.contains("BA102"));
    }
    
    // Edge Cases and Integration Tests
    @Test
    @DisplayName("Customer should handle null values gracefully")
    /** Test method. */
    public void testCustomerWithNullValues() {
        Customer nullCustomer = new Customer(1, null, null, null);
        assertNull(nullCustomer.getName());
        assertNull(nullCustomer.getPhone());
        assertNull(nullCustomer.getEmail());
    }
    
    @Test
    @DisplayName("Customer should maintain booking count correctly after multiple operations")
    /** Test method. @throws FlightBookingSystemException if test fails
     */
    public void testBookingCountAfterMultipleOperations() throws FlightBookingSystemException {
        Flight flight2 = new Flight(2, "BA102", "Paris", "London", LocalDate.of(2025, 3, 20));
        Flight flight3 = new Flight(3, "BA103", "London", "New York", LocalDate.of(2025, 3, 25));
        
        Booking booking2 = new Booking(customer, flight2, LocalDate.of(2025, 2, 5));
        Booking booking3 = new Booking(customer, flight3, LocalDate.of(2025, 2, 10));
        
        customer.addBooking(booking);
        customer.addBooking(booking2);
        customer.addBooking(booking3);
        assertEquals(3, customer.getBookings().size());
        
        customer.cancelBooking(booking2);
        assertEquals(2, customer.getBookings().size());
        
        customer.addBooking(booking2);
        assertEquals(3, customer.getBookings().size());
    }
    
    @Test
    @DisplayName("Customer details should update when properties change")
    /** Test method. */
    public void testDetailsUpdateWhenPropertiesChange() {
        String initialDetails = customer.getDetailsShort();
        assertTrue(initialDetails.contains("Aarav Sharma"));
        
        customer.setName("Updated Name");
        customer.setPhone("9999999999");
        
        String updatedDetails = customer.getDetailsShort();
        assertTrue(updatedDetails.contains("Updated Name"));
        assertTrue(updatedDetails.contains("9999999999"));
        assertFalse(updatedDetails.contains("Aarav Sharma"));
        assertFalse(updatedDetails.contains("9841234567"));
    }
    
    @Test
    @DisplayName("Bookings list should be independent for different customers")
    /** Test method. */
    public void testBookingsListIndependenceAcrossCustomers() {
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        
        customer.addBooking(booking);
        assertEquals(1, customer.getBookings().size());
        assertEquals(0, customer2.getBookings().size());
        
        Flight flight2 = new Flight(2, "BA102", "Paris", "London", LocalDate.of(2025, 3, 20));
        Booking booking2 = new Booking(customer2, flight2, LocalDate.of(2025, 2, 5));
        customer2.addBooking(booking2);
        
        assertEquals(1, customer.getBookings().size());
        assertEquals(1, customer2.getBookings().size());
    }
    
    @Test
    @DisplayName("Customer should handle large number of bookings")
    /** Test method. */
    public void testCustomerWithManyBookings() {
        for (int i = 1; i <= 100; i++) {
            Flight f = new Flight(i, "BA" + i, "London", "Paris", LocalDate.of(2025, 3, i % 28 + 1));
            Booking b = new Booking(customer, f, LocalDate.of(2025, 2, 1));
            customer.addBooking(b);
        }
        assertEquals(100, customer.getBookings().size());
    }
    
    @Test
    @DisplayName("getDetailsLong should handle edge case of exactly one booking")
    /** Test method. */
    public void testGetDetailsLongWithOneBooking() {
        customer.addBooking(booking);
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Total Bookings: 1"));
        assertFalse(details.contains("No bookings found"));
    }
    
    @Test
    @DisplayName("Customer equality based on object reference")
    /** Test method. */
    public void testCustomerEquality() {
        Customer customer2 = new Customer(1, "Aarav Sharma", "9841234567", "aarav@gmail.com");
        assertNotEquals(customer, customer2);
        assertEquals(customer, customer);
    }
    
    @Test
    @DisplayName("Customer with special characters in all fields")
    /** Test method. */
    public void testCustomerWithSpecialCharacters() {
        Customer specialCustomer = new Customer(
            999,
            "O'Brien-Smith & Co.",
            "1234567890",
            "test+filter@example.com"
        );
        assertEquals("O'Brien-Smith & Co.", specialCustomer.getName());
        assertEquals("test+filter@example.com", specialCustomer.getEmail());
    }
}
