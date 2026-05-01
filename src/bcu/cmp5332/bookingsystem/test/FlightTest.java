package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for Flight model.
 * Tests all flight functionality including constructors, getters, setters,
 * passenger management, pricing, and detail formatting.
 */
public class FlightTest {
    
    private Flight flight;
    private Customer customer;
    private LocalDate departureDate;
    
    @BeforeEach
    public void setUp() {
        departureDate = LocalDate.of(2025, 3, 15);
        flight = new Flight(1, "BA101", "London", "Paris", departureDate, 150, 200.0);
        customer = new Customer(1, "Aarav Sharma", "9841234567", "aarav@gmail.com");
    }
    
    @AfterEach
    public void tearDown() {
        flight = null;
        customer = null;
        departureDate = null;
    }
    
    // Constructor Tests
    @Test
    @DisplayName("Full constructor should initialize flight with all parameters")
    public void testFullConstructor() {
        assertEquals(1, flight.getId());
        assertEquals("BA101", flight.getFlightNumber());
        assertEquals("London", flight.getOrigin());
        assertEquals("Paris", flight.getDestination());
        assertEquals(departureDate, flight.getDepartureDate());
        assertEquals(150, flight.getCapacity());
        assertEquals(200.0, flight.getPrice());
        assertFalse(flight.isDeleted());
        assertNotNull(flight.getPassengers());
        assertTrue(flight.getPassengers().isEmpty());
    }
    
    @Test
    @DisplayName("Short constructor should use default capacity and price")
    public void testShortConstructor() {
        Flight shortFlight = new Flight(2, "BA102", "Paris", "London", departureDate);
        assertEquals(2, shortFlight.getId());
        assertEquals("BA102", shortFlight.getFlightNumber());
        assertEquals("Paris", shortFlight.getOrigin());
        assertEquals("London", shortFlight.getDestination());
        assertEquals(departureDate, shortFlight.getDepartureDate());
        assertEquals(100, shortFlight.getCapacity()); // Default capacity
        assertEquals(150.0, shortFlight.getPrice()); // Default price
        assertFalse(shortFlight.isDeleted());
    }
    
    @Test
    @DisplayName("Constructor should initialize empty passengers set")
    public void testConstructorInitializesEmptyPassengers() {
        Flight newFlight = new Flight(3, "BA103", "New York", "London", departureDate);
        assertNotNull(newFlight.getPassengers());
        assertEquals(0, newFlight.getPassengers().size());
    }
    
    @Test
    @DisplayName("Constructor should set deleted to false by default")
    public void testConstructorSetsDeletedToFalse() {
        Flight newFlight = new Flight(4, "BA104", "Tokyo", "Seoul", departureDate);
        assertFalse(newFlight.isDeleted());
    }
    
    // ID Tests
    @Test
    @DisplayName("getId should return correct flight ID")
    public void testGetId() {
        assertEquals(1, flight.getId());
    }
    
    @Test
    @DisplayName("setId should update flight ID")
    public void testSetId() {
        flight.setId(100);
        assertEquals(100, flight.getId());
    }
    
    @Test
    @DisplayName("setId should handle negative IDs")
    public void testSetIdWithNegativeValue() {
        flight.setId(-1);
        assertEquals(-1, flight.getId());
    }
    
    @Test
    @DisplayName("setId should handle zero ID")
    public void testSetIdWithZero() {
        flight.setId(0);
        assertEquals(0, flight.getId());
    }
    
    // Flight Number Tests
    @Test
    @DisplayName("getFlightNumber should return correct flight number")
    public void testGetFlightNumber() {
        assertEquals("BA101", flight.getFlightNumber());
    }
    
    @Test
    @DisplayName("setFlightNumber should update flight number")
    public void testSetFlightNumber() {
        flight.setFlightNumber("LH202");
        assertEquals("LH202", flight.getFlightNumber());
    }
    
    @Test
    @DisplayName("setFlightNumber should handle numeric flight numbers")
    public void testSetFlightNumberNumeric() {
        flight.setFlightNumber("1234");
        assertEquals("1234", flight.getFlightNumber());
    }
    
    @Test
    @DisplayName("setFlightNumber should handle mixed format")
    public void testSetFlightNumberMixedFormat() {
        flight.setFlightNumber("AA123B");
        assertEquals("AA123B", flight.getFlightNumber());
    }
    
    // Origin Tests
    @Test
    @DisplayName("getOrigin should return correct origin city")
    public void testGetOrigin() {
        assertEquals("London", flight.getOrigin());
    }
    
    @Test
    @DisplayName("setOrigin should update origin city")
    public void testSetOrigin() {
        flight.setOrigin("Manchester");
        assertEquals("Manchester", flight.getOrigin());
    }
    
    @Test
    @DisplayName("setOrigin should handle empty string")
    public void testSetOriginEmptyString() {
        flight.setOrigin("");
        assertEquals("", flight.getOrigin());
    }
    
    @Test
    @DisplayName("setOrigin should handle multi-word cities")
    public void testSetOriginMultiWord() {
        flight.setOrigin("New York");
        assertEquals("New York", flight.getOrigin());
    }
    
    // Destination Tests
    @Test
    @DisplayName("getDestination should return correct destination city")
    public void testGetDestination() {
        assertEquals("Paris", flight.getDestination());
    }
    
    @Test
    @DisplayName("setDestination should update destination city")
    public void testSetDestination() {
        flight.setDestination("Berlin");
        assertEquals("Berlin", flight.getDestination());
    }
    
    @Test
    @DisplayName("setDestination should handle same as origin")
    public void testSetDestinationSameAsOrigin() {
        flight.setDestination("London");
        assertEquals("London", flight.getDestination());
        assertEquals(flight.getOrigin(), flight.getDestination());
    }
    
    // Departure Date Tests
    @Test
    @DisplayName("getDepartureDate should return correct departure date")
    public void testGetDepartureDate() {
        assertEquals(departureDate, flight.getDepartureDate());
    }
    
    @Test
    @DisplayName("setDepartureDate should update departure date")
    public void testSetDepartureDate() {
        LocalDate newDate = LocalDate.of(2025, 4, 20);
        flight.setDepartureDate(newDate);
        assertEquals(newDate, flight.getDepartureDate());
    }
    
    @Test
    @DisplayName("setDepartureDate should handle past dates")
    public void testSetDepartureDateInPast() {
        LocalDate pastDate = LocalDate.of(2020, 1, 1);
        flight.setDepartureDate(pastDate);
        assertEquals(pastDate, flight.getDepartureDate());
    }
    
    @Test
    @DisplayName("setDepartureDate should handle future dates")
    public void testSetDepartureDateInFuture() {
        LocalDate futureDate = LocalDate.of(2030, 12, 31);
        flight.setDepartureDate(futureDate);
        assertEquals(futureDate, flight.getDepartureDate());
    }
    
    // Capacity Tests
    @Test
    @DisplayName("getCapacity should return correct capacity")
    public void testGetCapacity() {
        assertEquals(150, flight.getCapacity());
    }
    
    @Test
    @DisplayName("setCapacity should update capacity")
    public void testSetCapacity() {
        flight.setCapacity(200);
        assertEquals(200, flight.getCapacity());
    }
    
    @Test
    @DisplayName("setCapacity should handle zero capacity")
    public void testSetCapacityZero() {
        flight.setCapacity(0);
        assertEquals(0, flight.getCapacity());
    }
    
    @Test
    @DisplayName("setCapacity should handle negative capacity")
    public void testSetCapacityNegative() {
        flight.setCapacity(-10);
        assertEquals(-10, flight.getCapacity());
    }
    
    @Test
    @DisplayName("setCapacity should handle very large capacity")
    public void testSetCapacityVeryLarge() {
        flight.setCapacity(1000);
        assertEquals(1000, flight.getCapacity());
    }
    
    // Price Tests
    @Test
    @DisplayName("getPrice should return correct price")
    public void testGetPrice() {
        assertEquals(200.0, flight.getPrice());
    }
    
    @Test
    @DisplayName("setPrice should update price")
    public void testSetPrice() {
        flight.setPrice(350.50);
        assertEquals(350.50, flight.getPrice());
    }
    
    @Test
    @DisplayName("setPrice should handle zero price")
    public void testSetPriceZero() {
        flight.setPrice(0.0);
        assertEquals(0.0, flight.getPrice());
    }
    
    @Test
    @DisplayName("setPrice should handle negative price")
    public void testSetPriceNegative() {
        flight.setPrice(-100.0);
        assertEquals(-100.0, flight.getPrice());
    }
    
    @Test
    @DisplayName("setPrice should handle decimal precision")
    public void testSetPriceDecimalPrecision() {
        flight.setPrice(123.456);
        assertEquals(123.456, flight.getPrice(), 0.001);
    }
    
    // Deleted Status Tests
    @Test
    @DisplayName("isDeleted should return false for active flight")
    public void testIsDeletedReturnsFalse() {
        assertFalse(flight.isDeleted());
    }
    
    @Test
    @DisplayName("setDeleted should mark flight as deleted")
    public void testSetDeletedToTrue() {
        flight.setDeleted(true);
        assertTrue(flight.isDeleted());
    }
    
    @Test
    @DisplayName("setDeleted should mark flight as active")
    public void testSetDeletedToFalse() {
        flight.setDeleted(true);
        flight.setDeleted(false);
        assertFalse(flight.isDeleted());
    }
    
    @Test
    @DisplayName("setDeleted should toggle status multiple times")
    public void testSetDeletedToggleMultipleTimes() {
        flight.setDeleted(true);
        assertTrue(flight.isDeleted());
        flight.setDeleted(false);
        assertFalse(flight.isDeleted());
        flight.setDeleted(true);
        assertTrue(flight.isDeleted());
    }
    
    // Passenger Management Tests
    @Test
    @DisplayName("getPassengers should return empty list initially")
    public void testGetPassengersReturnsEmptyListInitially() {
        List<Customer> passengers = flight.getPassengers();
        assertNotNull(passengers);
        assertTrue(passengers.isEmpty());
    }
    
    @Test
    @DisplayName("getPassengers should return unmodifiable list")
    public void testGetPassengersReturnsUnmodifiableList() {
        assertThrows(UnsupportedOperationException.class, () -> {
            flight.getPassengers().add(customer);
        });
    }
    
    @Test
    @DisplayName("addPassenger should add passenger successfully")
    public void testAddPassenger() throws FlightBookingSystemException {
        flight.addPassenger(customer);
        assertEquals(1, flight.getPassengers().size());
        assertTrue(flight.getPassengers().contains(customer));
    }
    
    @Test
    @DisplayName("addPassenger should add multiple passengers")
    public void testAddMultiplePassengers() throws FlightBookingSystemException {
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        Customer customer3 = new Customer(3, "Chiranjivi Gurung", "9861234567", "chiranjivi@gmail.com");
        
        flight.addPassenger(customer);
        flight.addPassenger(customer2);
        flight.addPassenger(customer3);
        
        assertEquals(3, flight.getPassengers().size());
        assertTrue(flight.getPassengers().contains(customer));
        assertTrue(flight.getPassengers().contains(customer2));
        assertTrue(flight.getPassengers().contains(customer3));
    }
    
    @Test
    @DisplayName("addPassenger should prevent duplicate passengers")
    public void testAddPassengerPreventsDuplicates() throws FlightBookingSystemException {
        flight.addPassenger(customer);
        flight.addPassenger(customer);
        assertEquals(1, flight.getPassengers().size());
    }
    
    @Test
    @DisplayName("addPassenger should throw exception when at capacity")
    public void testAddPassengerAtCapacity() throws FlightBookingSystemException {
        Flight smallFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 2, 200.0);
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        Customer customer3 = new Customer(3, "Chiranjivi Gurung", "9861234567", "chiranjivi@gmail.com");
        
        smallFlight.addPassenger(customer);
        smallFlight.addPassenger(customer2);
        
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> smallFlight.addPassenger(customer3)
        );
        assertTrue(exception.getMessage().contains("full capacity"));
    }
    
    @Test
    @DisplayName("addPassenger should work up to exact capacity")
    public void testAddPassengerUpToCapacity() throws FlightBookingSystemException {
        Flight smallFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 3, 200.0);
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        Customer customer3 = new Customer(3, "Chiranjivi Gurung", "9861234567", "chiranjivi@gmail.com");
        
        smallFlight.addPassenger(customer);
        smallFlight.addPassenger(customer2);
        smallFlight.addPassenger(customer3);
        
        assertEquals(3, smallFlight.getPassengers().size());
    }
    
    @Test
    @DisplayName("removePassenger should remove existing passenger")
    public void testRemovePassenger() throws FlightBookingSystemException {
        flight.addPassenger(customer);
        assertEquals(1, flight.getPassengers().size());
        
        flight.removePassenger(customer);
        assertEquals(0, flight.getPassengers().size());
        assertFalse(flight.getPassengers().contains(customer));
    }
    
    @Test
    @DisplayName("removePassenger should handle non-existent passenger")
    public void testRemoveNonExistentPassenger() {
        flight.removePassenger(customer);
        assertEquals(0, flight.getPassengers().size());
    }
    
    @Test
    @DisplayName("removePassenger should handle multiple passengers correctly")
    public void testRemovePassengerWithMultiplePassengers() throws FlightBookingSystemException {
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        
        flight.addPassenger(customer);
        flight.addPassenger(customer2);
        assertEquals(2, flight.getPassengers().size());
        
        flight.removePassenger(customer);
        assertEquals(1, flight.getPassengers().size());
        assertFalse(flight.getPassengers().contains(customer));
        assertTrue(flight.getPassengers().contains(customer2));
    }
    
    @Test
    @DisplayName("removePassenger should not throw exception for non-existent passenger")
    public void testRemovePassengerDoesNotThrowException() {
        assertDoesNotThrow(() -> flight.removePassenger(customer));
    }
    
    // Departure Check Tests
    @Test
    @DisplayName("hasDeparted should return false for future flight")
    public void testHasDepartedReturnsFalseForFuture() {
        LocalDate systemDate = LocalDate.of(2025, 3, 1);
        assertFalse(flight.hasDeparted(systemDate));
    }
    
    @Test
    @DisplayName("hasDeparted should return true for past flight")
    public void testHasDepartedReturnsTrueForPast() {
        LocalDate systemDate = LocalDate.of(2025, 3, 20);
        assertTrue(flight.hasDeparted(systemDate));
    }
    
    @Test
    @DisplayName("hasDeparted should return false on departure day")
    public void testHasDepartedOnDepartureDay() {
        LocalDate systemDate = departureDate;
        assertFalse(flight.hasDeparted(systemDate));
    }
    
    @Test
    @DisplayName("hasDeparted should handle day before departure")
    public void testHasDepartedDayBeforeDeparture() {
        LocalDate systemDate = departureDate.minusDays(1);
        assertFalse(flight.hasDeparted(systemDate));
    }
    
    @Test
    @DisplayName("hasDeparted should handle day after departure")
    public void testHasDepartedDayAfterDeparture() {
        LocalDate systemDate = departureDate.plusDays(1);
        assertTrue(flight.hasDeparted(systemDate));
    }
    
    // Dynamic Pricing Tests
    @Test
    @DisplayName("calculateDynamicPrice should return base price for booking 31+ days ahead")
    public void testCalculateDynamicPriceMoreThan30Days() {
        LocalDate bookingDate = departureDate.minusDays(35);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        assertEquals(200.0, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice should apply 15% increase for 15-29 days ahead")
    public void testCalculateDynamicPrice15To29Days() {
        LocalDate bookingDate = departureDate.minusDays(20);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        double expected = 200.0 * 1.15;
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice should apply 30% increase for 8-14 days ahead")
    public void testCalculateDynamicPrice8To14Days() {
        LocalDate bookingDate = departureDate.minusDays(10);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        double expected = 200.0 * 1.30;
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice should apply 50% increase for 1-7 days ahead")
    public void testCalculateDynamicPrice1To7Days() {
        LocalDate bookingDate = departureDate.minusDays(5);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        double expected = 200.0 * 1.50;
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice should include capacity factor with no passengers")
    public void testCalculateDynamicPriceWithNoPassengers() {
        LocalDate bookingDate = departureDate.minusDays(35);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        assertEquals(200.0, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice should increase with half capacity")
    public void testCalculateDynamicPriceHalfCapacity() throws FlightBookingSystemException {
        Flight testFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 10, 200.0);
        for (int i = 1; i <= 5; i++) {
            Customer c = new Customer(i, "Customer" + i, "984123456" + i, "c" + i + "@gmail.com");
            testFlight.addPassenger(c);
        }
        
        LocalDate bookingDate = departureDate.minusDays(35);
        double dynamicPrice = testFlight.calculateDynamicPrice(bookingDate);
        double occupancyRate = 5.0 / 10.0;
        double expected = 200.0 * (1 + occupancyRate * 0.4);
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice should increase with near full capacity")
    public void testCalculateDynamicPriceNearFullCapacity() throws FlightBookingSystemException {
        Flight testFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 10, 200.0);
        for (int i = 1; i <= 9; i++) {
            Customer c = new Customer(i, "Customer" + i, "984123456" + i, "c" + i + "@gmail.com");
            testFlight.addPassenger(c);
        }
        
        LocalDate bookingDate = departureDate.minusDays(35);
        double dynamicPrice = testFlight.calculateDynamicPrice(bookingDate);
        double occupancyRate = 9.0 / 10.0;
        double expected = 200.0 * (1 + occupancyRate * 0.4);
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice should combine urgency and capacity factors")
    public void testCalculateDynamicPriceCombinedFactors() throws FlightBookingSystemException {
        Flight testFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 10, 200.0);
        for (int i = 1; i <= 5; i++) {
            Customer c = new Customer(i, "Customer" + i, "984123456" + i, "c" + i + "@gmail.com");
            testFlight.addPassenger(c);
        }
        
        LocalDate bookingDate = departureDate.minusDays(5);
        double dynamicPrice = testFlight.calculateDynamicPrice(bookingDate);
        double urgencyFactor = 0.5;
        double occupancyRate = 5.0 / 10.0;
        double capacityFactor = occupancyRate * 0.4;
        double expected = 200.0 * (1 + urgencyFactor + capacityFactor);
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("calculateDynamicPrice on departure day")
    public void testCalculateDynamicPriceOnDepartureDay() {
        LocalDate bookingDate = departureDate;
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        double expected = 200.0 * 1.50;
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    // Details Tests
    @Test
    @DisplayName("getDetailsShort should return correct format")
    public void testGetDetailsShort() {
        String details = flight.getDetailsShort();
        assertTrue(details.contains("Flight #1"));
        assertTrue(details.contains("BA101"));
        assertTrue(details.contains("London"));
        assertTrue(details.contains("Paris"));
        assertTrue(details.contains("15/03/2025"));
        assertTrue(details.contains("150/150"));
        assertTrue(details.contains("£200.00"));
    }
    
    @Test
    @DisplayName("getDetailsShort should show available seats correctly")
    public void testGetDetailsShortWithPassengers() throws FlightBookingSystemException {
        flight.addPassenger(customer);
        String details = flight.getDetailsShort();
        assertTrue(details.contains("149/150"));
    }
    
    @Test
    @DisplayName("getDetailsShort should handle full flight")
    public void testGetDetailsShortFullFlight() throws FlightBookingSystemException {
        Flight smallFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 1, 200.0);
        smallFlight.addPassenger(customer);
        String details = smallFlight.getDetailsShort();
        assertTrue(details.contains("0/1"));
    }
    
    @Test
    @DisplayName("getDetailsLongNoPassengers should include flight information")
    public void testGetDetailsLongNoPassengers() {
        String details = flight.getDetailsLongNoPassengers();
        assertTrue(details.contains("Flight Details:"));
        assertTrue(details.contains("ID: 1"));
        assertTrue(details.contains("Flight Number: BA101"));
        assertTrue(details.contains("Origin: London"));
        assertTrue(details.contains("Destination: Paris"));
        assertTrue(details.contains("Departure Date: 15/03/2025"));
        assertTrue(details.contains("Capacity: 150"));
        assertTrue(details.contains("Passengers: 0"));
        assertTrue(details.contains("Available Seats: 150"));
        assertTrue(details.contains("Price: £200.00"));
    }
    
    @Test
    @DisplayName("getDetailsLongNoPassengers should not show passenger list")
    public void testGetDetailsLongNoPassengersDoesNotShowList() {
        String details = flight.getDetailsLongNoPassengers();
        assertFalse(details.contains("Passenger List:"));
        assertFalse(details.contains("No passengers booked"));
    }
    
    @Test
    @DisplayName("getDetailsLong should include flight information")
    public void testGetDetailsLong() {
        String details = flight.getDetailsLong();
        assertTrue(details.contains("Flight Details:"));
        assertTrue(details.contains("ID: 1"));
        assertTrue(details.contains("Flight Number: BA101"));
        assertTrue(details.contains("Passenger List:"));
    }
    
    @Test
    @DisplayName("getDetailsLong should show no passengers message")
    public void testGetDetailsLongNoPassengersMessage() {
        String details = flight.getDetailsLong();
        assertTrue(details.contains("No passengers booked."));
    }
    
    @Test
    @DisplayName("getDetailsLong should list passengers")
    public void testGetDetailsLongWithPassengers() throws FlightBookingSystemException {
        flight.addPassenger(customer);
        String details = flight.getDetailsLong();
        assertTrue(details.contains("Passengers: 1"));
        assertTrue(details.contains("Aarav Sharma"));
    }
    
    @Test
    @DisplayName("getDetailsLong should list multiple passengers")
    public void testGetDetailsLongWithMultiplePassengers() throws FlightBookingSystemException {
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        flight.addPassenger(customer);
        flight.addPassenger(customer2);
        
        String details = flight.getDetailsLong();
        assertTrue(details.contains("Passengers: 2"));
        assertTrue(details.contains("Aarav Sharma"));
        assertTrue(details.contains("Binita Rai"));
    }
    
    // Edge Cases and Integration Tests
    @Test
    @DisplayName("Flight should handle null values gracefully")
    public void testFlightWithNullValues() {
        Flight nullFlight = new Flight(1, null, null, null, null, 100, 150.0);
        assertNull(nullFlight.getFlightNumber());
        assertNull(nullFlight.getOrigin());
        assertNull(nullFlight.getDestination());
        assertNull(nullFlight.getDepartureDate());
    }
    
    @Test
    @DisplayName("Flight should maintain passenger count correctly after multiple operations")
    public void testPassengerCountAfterMultipleOperations() throws FlightBookingSystemException {
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        Customer customer3 = new Customer(3, "Chiranjivi Gurung", "9861234567", "chiranjivi@gmail.com");
        
        flight.addPassenger(customer);
        flight.addPassenger(customer2);
        flight.addPassenger(customer3);
        assertEquals(3, flight.getPassengers().size());
        
        flight.removePassenger(customer2);
        assertEquals(2, flight.getPassengers().size());
        
        flight.addPassenger(customer2);
        assertEquals(3, flight.getPassengers().size());
    }
    
    @Test
    @DisplayName("Flight details should update when properties change")
    public void testDetailsUpdateWhenPropertiesChange() {
        String initialDetails = flight.getDetailsShort();
        assertTrue(initialDetails.contains("BA101"));
        assertTrue(initialDetails.contains("London"));
        
        flight.setFlightNumber("LH202");
        flight.setOrigin("Berlin");
        
        String updatedDetails = flight.getDetailsShort();
        assertTrue(updatedDetails.contains("LH202"));
        assertTrue(updatedDetails.contains("Berlin"));
        assertFalse(updatedDetails.contains("BA101"));
        assertFalse(updatedDetails.contains("London to Paris"));
    }
    
    @Test
    @DisplayName("Passengers set should be independent for different flights")
    public void testPassengersSetIndependenceAcrossFlights() throws FlightBookingSystemException {
        Flight flight2 = new Flight(2, "BA102", "Paris", "London", departureDate);
        
        flight.addPassenger(customer);
        assertEquals(1, flight.getPassengers().size());
        assertEquals(0, flight2.getPassengers().size());
        
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        flight2.addPassenger(customer2);
        
        assertEquals(1, flight.getPassengers().size());
        assertEquals(1, flight2.getPassengers().size());
    }
    
    @Test
    @DisplayName("Flight should handle large capacity")
    public void testFlightWithLargeCapacity() {
        Flight largeFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 500, 200.0);
        assertEquals(500, largeFlight.getCapacity());
    }
    
    @Test
    @DisplayName("Flight should handle adding many passengers")
    public void testFlightWithManyPassengers() throws FlightBookingSystemException {
        Flight largeFlight = new Flight(2, "BA102", "Paris", "London", departureDate, 100, 200.0);
        for (int i = 1; i <= 50; i++) {
            Customer c = new Customer(i, "Customer" + i, "984123456" + i, "c" + i + "@gmail.com");
            largeFlight.addPassenger(c);
        }
        assertEquals(50, largeFlight.getPassengers().size());
    }
    
    @Test
    @DisplayName("Dynamic pricing should handle edge case at 30 days")
    public void testCalculateDynamicPriceAt30Days() {
        LocalDate bookingDate = departureDate.minusDays(30);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        double expected = 200.0 * 1.15;
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("Dynamic pricing should handle edge case at 14 days")
    public void testCalculateDynamicPriceAt14Days() {
        LocalDate bookingDate = departureDate.minusDays(14);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        double expected = 200.0 * 1.30;
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("Dynamic pricing should handle edge case at 7 days")
    public void testCalculateDynamicPriceAt7Days() {
        LocalDate bookingDate = departureDate.minusDays(7);
        double dynamicPrice = flight.calculateDynamicPrice(bookingDate);
        double expected = 200.0 * 1.50;
        assertEquals(expected, dynamicPrice, 0.01);
    }
    
    @Test
    @DisplayName("Flight equality based on object reference")
    public void testFlightEquality() {
        Flight flight2 = new Flight(1, "BA101", "London", "Paris", departureDate, 150, 200.0);
        assertNotEquals(flight, flight2);
        assertEquals(flight, flight);
    }
    
    @Test
    @DisplayName("Available seats calculation should be accurate")
    public void testAvailableSeatsCalculation() throws FlightBookingSystemException {
        assertEquals(150, flight.getCapacity());
        assertEquals(0, flight.getPassengers().size());
        
        flight.addPassenger(customer);
        assertEquals(1, flight.getPassengers().size());
        
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        flight.addPassenger(customer2);
        assertEquals(2, flight.getPassengers().size());
        
        String details = flight.getDetailsShort();
        assertTrue(details.contains("148/150"));
    }
}
