package bcu.cmp5332.bookingsystem.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Master test suite that runs all test classes.
 * This suite ensures comprehensive testing of the Flight Booking System.
 * 
 * To run all tests, execute this class.
 */
@Suite
@SuiteDisplayName("Flight Booking System - All Tests")
@SelectClasses({
    CustomerTest.class,
    FlightTest.class,
    BookingTest.class,
    FlightBookingSystemTest.class,
    UserTest.class,
    AuthenticationServiceTest.class,
    ValidationUtilTest.class
})
public class AllTests {
    /**
     * Default constructor.
     */
    public AllTests() {
    }
    // This class remains empty, used only as a holder for the above annotations
}
