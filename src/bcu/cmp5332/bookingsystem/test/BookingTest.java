package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Booking.BookingStatus;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import org.junit.jupiter.api.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for Booking model.
 * Tests all booking functionality including one-way, round-trip,
 * status management, pricing, and calculations.
 */
public class BookingTest {
    
    /**
     * Default constructor.
     */
    public BookingTest() {
    }
    
    private Customer customer;
    private Flight outboundFlight;
    private Flight returnFlight;
    private Booking oneWayBooking;
    private Booking roundTripBooking;
    private LocalDate bookingDate;
    
    /** Set up test fixtures. */
    @BeforeEach
    public void setUp() {
        customer = new Customer(1, "Aarav Sharma", "9841234567", "aarav@gmail.com");
        outboundFlight = new Flight(1, "BA101", "London", "Paris", 
            LocalDate.of(2025, 3, 15), 150, 200.0);
        returnFlight = new Flight(2, "BA102", "Paris", "London", 
            LocalDate.of(2025, 3, 22), 150, 200.0);
        bookingDate = LocalDate.of(2025, 2, 1);
        
        oneWayBooking = new Booking(customer, outboundFlight, bookingDate);
        roundTripBooking = new Booking(customer, outboundFlight, returnFlight, bookingDate);
    }
    
    /** Clean up test fixtures. */
    @AfterEach
    public void tearDown() {
        customer = null;
        outboundFlight = null;
        returnFlight = null;
        oneWayBooking = null;
        roundTripBooking = null;
        bookingDate = null;
    }
    
    // Constructor Tests
    /** Test method. */
    @Test
    @DisplayName("One-way constructor should initialize booking correctly")
    public void testOneWayConstructor() {
        assertNotNull(oneWayBooking);
        assertEquals(customer, oneWayBooking.getCustomer());
        assertEquals(outboundFlight, oneWayBooking.getOutboundFlight());
        assertNull(oneWayBooking.getReturnFlight());
        assertEquals(bookingDate, oneWayBooking.getBookingDate());
        assertEquals(BookingStatus.BOOKED, oneWayBooking.getStatus());
        assertEquals(bookingDate, oneWayBooking.getActionDate());
        assertEquals(0.0, oneWayBooking.getCancellationFee());
        assertFalse(oneWayBooking.isPartialCancellation());
        assertFalse(oneWayBooking.isRoundTrip());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Round-trip constructor should initialize booking correctly")
    public void testRoundTripConstructor() {
        assertNotNull(roundTripBooking);
        assertEquals(customer, roundTripBooking.getCustomer());
        assertEquals(outboundFlight, roundTripBooking.getOutboundFlight());
        assertEquals(returnFlight, roundTripBooking.getReturnFlight());
        assertEquals(bookingDate, roundTripBooking.getBookingDate());
        assertEquals(BookingStatus.BOOKED, roundTripBooking.getStatus());
        assertTrue(roundTripBooking.isRoundTrip());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Constructor should calculate dynamic price automatically")
    public void testConstructorCalculatesDynamicPrice() {
        assertTrue(oneWayBooking.getPrice() > 0);
        assertTrue(roundTripBooking.getPrice() > oneWayBooking.getPrice());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Constructor should set initial status to BOOKED")
    public void testConstructorSetsStatusToBooked() {
        assertEquals(BookingStatus.BOOKED, oneWayBooking.getStatus());
        assertEquals(BookingStatus.BOOKED, roundTripBooking.getStatus());
    }
    
    // Customer Tests
    /** Test method. */
    @Test
    @DisplayName("getCustomer should return correct customer")
    public void testGetCustomer() {
        assertEquals(customer, oneWayBooking.getCustomer());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setCustomer should update customer")
    public void testSetCustomer() {
        Customer newCustomer = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        oneWayBooking.setCustomer(newCustomer);
        assertEquals(newCustomer, oneWayBooking.getCustomer());
    }
    
    // Flight Tests
    /** Test method. */
    @Test
    @DisplayName("getOutboundFlight should return correct flight")
    public void testGetOutboundFlight() {
        assertEquals(outboundFlight, oneWayBooking.getOutboundFlight());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setOutboundFlight should update outbound flight")
    public void testSetOutboundFlight() {
        Flight newFlight = new Flight(3, "BA103", "London", "Berlin", LocalDate.of(2025, 3, 20));
        oneWayBooking.setOutboundFlight(newFlight);
        assertEquals(newFlight, oneWayBooking.getOutboundFlight());
    }
    
    /** Test method. */
    @Test
    @DisplayName("getReturnFlight should return null for one-way booking")
    public void testGetReturnFlightOneWay() {
        assertNull(oneWayBooking.getReturnFlight());
    }
    
    /** Test method. */
    @Test
    @DisplayName("getReturnFlight should return correct flight for round-trip")
    public void testGetReturnFlightRoundTrip() {
        assertEquals(returnFlight, roundTripBooking.getReturnFlight());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setReturnFlight should update return flight")
    public void testSetReturnFlight() {
        Flight newReturnFlight = new Flight(4, "BA104", "Paris", "London", LocalDate.of(2025, 3, 25));
        roundTripBooking.setReturnFlight(newReturnFlight);
        assertEquals(newReturnFlight, roundTripBooking.getReturnFlight());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setReturnFlight should convert one-way to round-trip")
    public void testSetReturnFlightConvertsToRoundTrip() {
        assertNull(oneWayBooking.getReturnFlight());
        assertFalse(oneWayBooking.isRoundTrip());
        
        oneWayBooking.setReturnFlight(returnFlight);
        assertEquals(returnFlight, oneWayBooking.getReturnFlight());
        assertTrue(oneWayBooking.isRoundTrip());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Deprecated getFlight should return outbound flight")
    public void testDeprecatedGetFlight() {
        assertEquals(outboundFlight, oneWayBooking.getFlight());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Deprecated setFlight should update outbound flight")
    public void testDeprecatedSetFlight() {
        Flight newFlight = new Flight(3, "BA103", "London", "Berlin", LocalDate.of(2025, 3, 20));
        oneWayBooking.setFlight(newFlight);
        assertEquals(newFlight, oneWayBooking.getOutboundFlight());
    }
    
    // Round-trip Tests
    /** Test method. */
    @Test
    @DisplayName("isRoundTrip should return false for one-way booking")
    public void testIsRoundTripReturnsFalseForOneWay() {
        assertFalse(oneWayBooking.isRoundTrip());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isRoundTrip should return true for round-trip booking")
    public void testIsRoundTripReturnsTrueForRoundTrip() {
        assertTrue(roundTripBooking.isRoundTrip());
    }
    
    // Booking Date Tests
    /** Test method. */
    @Test
    @DisplayName("getBookingDate should return correct date")
    public void testGetBookingDate() {
        assertEquals(bookingDate, oneWayBooking.getBookingDate());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setBookingDate should update booking date")
    public void testSetBookingDate() {
        LocalDate newDate = LocalDate.of(2025, 2, 15);
        oneWayBooking.setBookingDate(newDate);
        assertEquals(newDate, oneWayBooking.getBookingDate());
    }
    
    // Price Tests
    /** Test method. */
    @Test
    @DisplayName("getPrice should return calculated price")
    public void testGetPrice() {
        assertTrue(oneWayBooking.getPrice() > 0);
    }
    
    /** Test method. */
    @Test
    @DisplayName("setPrice should update price")
    public void testSetPrice() {
        oneWayBooking.setPrice(500.0);
        assertEquals(500.0, oneWayBooking.getPrice());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Round-trip price should be higher than one-way")
    public void testRoundTripPriceHigherThanOneWay() {
        assertTrue(roundTripBooking.getPrice() > oneWayBooking.getPrice());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Price should increase when booking closer to departure")
    public void testPriceIncreasesCloserToDeparture() {
        LocalDate earlyBooking = LocalDate.of(2025, 1, 1);
        LocalDate lateBooking = LocalDate.of(2025, 3, 10);
        
        Booking earlyBook = new Booking(customer, outboundFlight, earlyBooking);
        Booking lateBook = new Booking(customer, outboundFlight, lateBooking);
        
        assertTrue(lateBook.getPrice() > earlyBook.getPrice());
    }
    
    // Cancellation Fee Tests
    /** Test method. */
    @Test
    @DisplayName("getCancellationFee should return zero initially")
    public void testGetCancellationFeeInitiallyZero() {
        assertEquals(0.0, oneWayBooking.getCancellationFee());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setCancellationFee should update fee")
    public void testSetCancellationFee() {
        oneWayBooking.setCancellationFee(50.0);
        assertEquals(50.0, oneWayBooking.getCancellationFee());
    }
    
    // Status Tests
    /** Test method. */
    @Test
    @DisplayName("getStatus should return BOOKED initially")
    public void testGetStatusInitiallyBooked() {
        assertEquals(BookingStatus.BOOKED, oneWayBooking.getStatus());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setStatus should update status")
    public void testSetStatus() {
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, oneWayBooking.getStatus());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isCancelled should return false for BOOKED status")
    public void testIsCancelledReturnsFalseForBooked() {
        assertFalse(oneWayBooking.isCancelled());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isCancelled should return true for CANCELLED status")
    public void testIsCancelledReturnsTrueForCancelled() {
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        assertTrue(oneWayBooking.isCancelled());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Deprecated setCancelled should set status to CANCELLED")
    public void testDeprecatedSetCancelledTrue() {
        oneWayBooking.setCancelled(true);
        assertEquals(BookingStatus.CANCELLED, oneWayBooking.getStatus());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Deprecated setCancelled should set status to BOOKED")
    public void testDeprecatedSetCancelledFalse() {
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        oneWayBooking.setCancelled(false);
        assertEquals(BookingStatus.BOOKED, oneWayBooking.getStatus());
    }
    
    // Action Date Tests
    /** Test method. */
    @Test
    @DisplayName("getActionDate should return booking date initially")
    public void testGetActionDateInitially() {
        assertEquals(bookingDate, oneWayBooking.getActionDate());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setActionDate should update action date")
    public void testSetActionDate() {
        LocalDate newActionDate = LocalDate.of(2025, 2, 10);
        oneWayBooking.setActionDate(newActionDate);
        assertEquals(newActionDate, oneWayBooking.getActionDate());
    }
    
    // Partial Cancellation Tests
    /** Test method. */
    @Test
    @DisplayName("isPartialCancellation should return false initially")
    public void testIsPartialCancellationInitiallyFalse() {
        assertFalse(oneWayBooking.isPartialCancellation());
    }
    
    /** Test method. */
    @Test
    @DisplayName("setPartialCancellation should update flag")
    public void testSetPartialCancellation() {
        oneWayBooking.setPartialCancellation(true);
        assertTrue(oneWayBooking.isPartialCancellation());
    }
    
    // Refund Calculation Tests
    /** Test method. */
    @Test
    @DisplayName("getRefundAmount should return zero for active booking")
    public void testGetRefundAmountForActiveBooking() {
        assertEquals(0.0, oneWayBooking.getRefundAmount());
    }
    
    /** Test method. */
    @Test
    @DisplayName("getRefundAmount should return price minus fee for cancelled booking")
    public void testGetRefundAmountForCancelledBooking() {
        double price = oneWayBooking.getPrice();
        oneWayBooking.setCancellationFee(50.0);
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        
        double expectedRefund = price - 50.0;
        assertEquals(expectedRefund, oneWayBooking.getRefundAmount(), 0.01);
    }
    
    /** Test method. */
    @Test
    @DisplayName("getRefundAmount should not be negative")
    public void testGetRefundAmountNotNegative() {
        oneWayBooking.setPrice(100.0);
        oneWayBooking.setCancellationFee(150.0);
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        
        assertEquals(0.0, oneWayBooking.getRefundAmount());
    }
    
    /** Test method. */
    @Test
    @DisplayName("getRefundAmount should handle zero fee")
    public void testGetRefundAmountWithZeroFee() {
        double price = oneWayBooking.getPrice();
        oneWayBooking.setCancellationFee(0.0);
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        
        assertEquals(price, oneWayBooking.getRefundAmount(), 0.01);
    }
    
    // Total Cost Tests
    /** Test method. */
    @Test
    @DisplayName("getTotalCost should return price plus fee")
    public void testGetTotalCost() {
        double price = oneWayBooking.getPrice();
        oneWayBooking.setCancellationFee(50.0);
        
        assertEquals(price + 50.0, oneWayBooking.getTotalCost(), 0.01);
    }
    
    /** Test method. */
    @Test
    @DisplayName("getTotalCost should handle zero fee")
    public void testGetTotalCostWithZeroFee() {
        double price = oneWayBooking.getPrice();
        assertEquals(price, oneWayBooking.getTotalCost(), 0.01);
    }
    
    // Amount Paid Tests
    /** Test method. */
    @Test
    @DisplayName("getAmountPaid should return price for BOOKED status")
    public void testGetAmountPaidForBooked() {
        double price = oneWayBooking.getPrice();
        assertEquals(price, oneWayBooking.getAmountPaid(), 0.01);
    }
    
    /** Test method. */
    @Test
    @DisplayName("getAmountPaid should return fee for CANCELLED status")
    public void testGetAmountPaidForCancelled() {
        oneWayBooking.setCancellationFee(50.0);
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        assertEquals(50.0, oneWayBooking.getAmountPaid(), 0.01);
    }
    
    /** Test method. */
    @Test
    @DisplayName("getAmountPaid should return price plus fee for REBOOKED status")
    public void testGetAmountPaidForRebooked() {
        double price = oneWayBooking.getPrice();
        oneWayBooking.setCancellationFee(50.0);
        oneWayBooking.setStatus(BookingStatus.REBOOKED);
        assertEquals(price + 50.0, oneWayBooking.getAmountPaid(), 0.01);
    }
    
    // Details Tests
    /** Test method. */
    @Test
    @DisplayName("getDetailsShort should include flight number and date")
    public void testGetDetailsShort() {
        String details = oneWayBooking.getDetailsShort();
        assertTrue(details.contains("BA101"));
        assertTrue(details.contains("01/02/2025"));
        assertTrue(details.contains("£"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsShort should show round-trip indicator")
    public void testGetDetailsShortRoundTrip() {
        String details = roundTripBooking.getDetailsShort();
        assertTrue(details.contains("Round-trip"));
        assertTrue(details.contains("BA102"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsShort should show status for cancelled booking")
    public void testGetDetailsShortCancelledStatus() {
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        String details = oneWayBooking.getDetailsShort();
        assertTrue(details.contains("CANCELLED"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsShort should not show status for active booking")
    public void testGetDetailsShortActiveNoStatus() {
        String details = oneWayBooking.getDetailsShort();
        assertFalse(details.contains("BOOKED"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should include customer information")
    public void testGetDetailsLong() {
        String details = oneWayBooking.getDetailsLong();
        assertTrue(details.contains("Booking Details:"));
        assertTrue(details.contains("Aarav Sharma"));
        assertTrue(details.contains("Customer: "));
        assertTrue(details.contains("ID: 1"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should show one-way type")
    public void testGetDetailsLongOneWay() {
        String details = oneWayBooking.getDetailsLong();
        assertTrue(details.contains("Booking Type: One-way"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should show round-trip type")
    public void testGetDetailsLongRoundTrip() {
        String details = roundTripBooking.getDetailsLong();
        assertTrue(details.contains("Booking Type: Round-trip"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should include outbound flight information")
    public void testGetDetailsLongOutboundFlight() {
        String details = oneWayBooking.getDetailsLong();
        assertTrue(details.contains("Outbound Flight:"));
        assertTrue(details.contains("BA101"));
        assertTrue(details.contains("London -> Paris"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should include return flight for round-trip")
    public void testGetDetailsLongReturnFlight() {
        String details = roundTripBooking.getDetailsLong();
        assertTrue(details.contains("Return Flight:"));
        assertTrue(details.contains("BA102"));
        assertTrue(details.contains("Paris -> London"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should not include return flight for one-way")
    public void testGetDetailsLongNoReturnFlight() {
        String details = oneWayBooking.getDetailsLong();
        assertFalse(details.contains("Return Flight:"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should show cancellation fee if present")
    public void testGetDetailsLongWithCancellationFee() {
        oneWayBooking.setCancellationFee(50.0);
        String details = oneWayBooking.getDetailsLong();
        assertTrue(details.contains("Fee: £50.00"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should not show fee if zero")
    public void testGetDetailsLongNoFee() {
        String details = oneWayBooking.getDetailsLong();
        assertFalse(details.contains("Fee:"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should show refund for cancelled booking")
    public void testGetDetailsLongRefund() {
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        oneWayBooking.setCancellationFee(50.0);
        String details = oneWayBooking.getDetailsLong();
        assertTrue(details.contains("Refund:"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("getDetailsLong should show status")
    public void testGetDetailsLongStatus() {
        String details = oneWayBooking.getDetailsLong();
        assertTrue(details.contains("Status: BOOKED"));
    }
    
    // Edge Cases and Integration Tests
    /** Test method. */
    @Test
    @DisplayName("Booking should handle null customer")
    public void testBookingWithNullCustomer() {
        Booking nullBooking = new Booking(null, outboundFlight, bookingDate);
        assertNull(nullBooking.getCustomer());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Dynamic pricing should reflect flight occupancy")
    public void testDynamicPricingReflectsOccupancy() throws Exception  {
        Flight fullFlight = new Flight(3, "BA103", "London", "Berlin", 
            LocalDate.of(2025, 3, 15), 10, 200.0);
        
        Booking emptyFlightBooking = new Booking(customer, fullFlight, bookingDate);
        double priceWithNoPassengers = emptyFlightBooking.getPrice();
        
        for (int i = 1; i <= 8; i++) {
            Customer c = new Customer(i + 100, "Customer" + i, "984123456" + i, "c" + i + "@gmail.com");
            fullFlight.addPassenger(c);
        }
        
        Booking nearFullBooking = new Booking(customer, fullFlight, bookingDate);
        double priceWithPassengers = nearFullBooking.getPrice();
        
        assertTrue(priceWithPassengers > priceWithNoPassengers);
    }
    
    /** Test method. */
    @Test
    @DisplayName("Booking status transitions should work correctly")
    public void testStatusTransitions() {
        assertEquals(BookingStatus.BOOKED, oneWayBooking.getStatus());
        
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CANCELLED, oneWayBooking.getStatus());
        
        oneWayBooking.setStatus(BookingStatus.REBOOKED);
        assertEquals(BookingStatus.REBOOKED, oneWayBooking.getStatus());
        
        oneWayBooking.setStatus(BookingStatus.BOOKED);
        assertEquals(BookingStatus.BOOKED, oneWayBooking.getStatus());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Financial calculations should be consistent")
    public void testFinancialCalculationsConsistency() {
        double price = 300.0;
        double fee = 50.0;
        
        oneWayBooking.setPrice(price);
        oneWayBooking.setCancellationFee(fee);
        
        assertEquals(price + fee, oneWayBooking.getTotalCost(), 0.01);
        
        oneWayBooking.setStatus(BookingStatus.CANCELLED);
        assertEquals(price - fee, oneWayBooking.getRefundAmount(), 0.01);
        assertEquals(fee, oneWayBooking.getAmountPaid(), 0.01);
    }
    
    /** Test method. */
    @Test
    @DisplayName("Booking should handle very early booking")
    public void testVeryEarlyBooking() {
        LocalDate veryEarly = LocalDate.of(2025, 1, 1);
        Booking earlyBooking = new Booking(customer, outboundFlight, veryEarly);
        
        double basePrice = outboundFlight.getPrice();
        assertTrue(earlyBooking.getPrice() >= basePrice);
        assertTrue(earlyBooking.getPrice() <= basePrice * 1.5);
    }
    
    /** Test method. */
    @Test
    @DisplayName("Booking should handle last minute booking")
    public void testLastMinuteBooking() {
        LocalDate lastMinute = LocalDate.of(2025, 3, 14);
        Booking lateBooking = new Booking(customer, outboundFlight, lastMinute);
        
        double basePrice = outboundFlight.getPrice();
        assertTrue(lateBooking.getPrice() >= basePrice * 1.5);
    }
}
