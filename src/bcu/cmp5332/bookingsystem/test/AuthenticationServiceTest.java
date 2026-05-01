package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.AuthenticationService;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AuthenticationService.
 */
public class AuthenticationServiceTest {
    
    /**
     * Default constructor.
     */
    public AuthenticationServiceTest() {
    }
    
    private FlightBookingSystem fbs;
    private AuthenticationService authService;
    
    /** Set up test fixtures. @throws FlightBookingSystemException if test fails
     */
    @BeforeEach
    public void setUp() throws FlightBookingSystemException {
        fbs = new FlightBookingSystem(true); // Test mode
        authService = fbs.getAuthService();
        
        // Add sample customers
        Customer customer1 = new Customer(1, "Aarav Sharma", "9841234567", "aarav@gmail.com");
        Customer customer2 = new Customer(2, "Binita Rai", "9851234567", "binita@gmail.com");
        fbs.addCustomer(customer1);
        fbs.addCustomer(customer2);
    }
    
    /** Clean up test fixtures. */
    @AfterEach
    public void tearDown() {
        if (authService.isLoggedIn()) {
            authService.logout();
        }
        fbs = null;
        authService = null;
    }
    
    // Login Tests
    /** Test method. */
    @Test
    @DisplayName("Login should succeed with correct admin credentials")
    public void testLoginAdminSuccess() throws FlightBookingSystemException  {
        boolean result = authService.login("admin", "admin123");
        assertTrue(result);
        assertTrue(authService.isLoggedIn());
        assertTrue(authService.isAdmin());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Login should succeed with correct customer credentials")
    public void testLoginCustomerSuccess() throws FlightBookingSystemException  {
        boolean result = authService.login("aarav", "aarav123");
        assertTrue(result);
        assertTrue(authService.isLoggedIn());
        assertTrue(authService.isCustomer());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Login should fail with invalid username")
    public void testLoginInvalidUsername() {
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> authService.login("nonexistent", "password")
        );
        assertTrue(exception.getMessage().contains("Invalid username or password"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("Login should fail with invalid password")
    public void testLoginInvalidPassword() {
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> authService.login("admin", "wrongpassword")
        );
        assertTrue(exception.getMessage().contains("Invalid username or password"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("Login should fail for deleted user")
    public void testLoginDeletedUser() throws FlightBookingSystemException  {
        authService.registerCustomer("testuser", "test123", 100);
        authService.markCustomerUserAsDeleted(100, true);
        
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> authService.login("testuser", "test123")
        );
        assertTrue(exception.getMessage().contains("deleted"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("Login should fail for customer with deleted customer record")
    public void testLoginDeletedCustomer() throws FlightBookingSystemException  {
        Customer customer = fbs.getCustomerByID(1);
        customer.setDeleted(true);
        
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> authService.login("aarav", "aarav123")
        );
        assertTrue(exception.getMessage().contains("deleted"));
    }
    
    // Logout Tests
    /** Test method. */
    @Test
    @DisplayName("Logout should clear current user")
    public void testLogout() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        assertTrue(authService.isLoggedIn());
        
        authService.logout();
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentUser());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Logout should work when not logged in")
    public void testLogoutWhenNotLoggedIn() {
        assertDoesNotThrow(() -> authService.logout());
        assertFalse(authService.isLoggedIn());
    }
    
    // Current User Tests
    /** Test method. */
    @Test
    @DisplayName("getCurrentUser should return null when not logged in")
    public void testGetCurrentUserNotLoggedIn() {
        assertNull(authService.getCurrentUser());
    }
    
    /** Test method. */
    @Test
    @DisplayName("getCurrentUser should return logged in user")
    public void testGetCurrentUserLoggedIn() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        User currentUser = authService.getCurrentUser();
        assertNotNull(currentUser);
        assertEquals("admin", currentUser.getUsername());
    }
    
    // Login Status Tests
    /** Test method. */
    @Test
    @DisplayName("isLoggedIn should return false initially")
    public void testIsLoggedInInitially() {
        assertFalse(authService.isLoggedIn());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isLoggedIn should return true after login")
    public void testIsLoggedInAfterLogin() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        assertTrue(authService.isLoggedIn());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isLoggedIn should return false after logout")
    public void testIsLoggedInAfterLogout() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        authService.logout();
        assertFalse(authService.isLoggedIn());
    }
    
    // User Type Tests
    /** Test method. */
    @Test
    @DisplayName("isAdmin should return false when not logged in")
    public void testIsAdminWhenNotLoggedIn() {
        assertFalse(authService.isAdmin());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isAdmin should return true for admin user")
    public void testIsAdminForAdminUser() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        assertTrue(authService.isAdmin());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isAdmin should return false for customer user")
    public void testIsAdminForCustomerUser() throws FlightBookingSystemException  {
        authService.login("aarav", "aarav123");
        assertFalse(authService.isAdmin());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isCustomer should return false when not logged in")
    public void testIsCustomerWhenNotLoggedIn() {
        assertFalse(authService.isCustomer());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isCustomer should return false for admin user")
    public void testIsCustomerForAdminUser() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        assertFalse(authService.isCustomer());
    }
    
    /** Test method. */
    @Test
    @DisplayName("isCustomer should return true for customer user")
    public void testIsCustomerForCustomerUser() throws FlightBookingSystemException  {
        authService.login("aarav", "aarav123");
        assertTrue(authService.isCustomer());
    }
    
    // Customer ID Tests
    /** Test method. */
    @Test
    @DisplayName("getCustomerId should return null when not logged in")
    public void testGetCustomerIdNotLoggedIn() {
        assertNull(authService.getCustomerId());
    }
    
    /** Test method. */
    @Test
    @DisplayName("getCustomerId should return null for admin")
    public void testGetCustomerIdForAdmin() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        assertNull(authService.getCustomerId());
    }
    
    /** Test method. */
    @Test
    @DisplayName("getCustomerId should return correct ID for customer")
    public void testGetCustomerIdForCustomer() throws FlightBookingSystemException  {
        authService.login("aarav", "aarav123");
        assertEquals(1, authService.getCustomerId());
    }
    
    // Registration Tests
    /** Test method. */
    @Test
    @DisplayName("registerCustomer should create new customer user")
    public void testRegisterCustomer() throws FlightBookingSystemException  {
        authService.registerCustomer("newuser", "password123", 10);
        
        boolean loginSuccess = authService.login("newuser", "password123");
        assertTrue(loginSuccess);
        assertTrue(authService.isCustomer());
        assertEquals(10, authService.getCustomerId());
    }
    
    /** Test method. */
    @Test
    @DisplayName("registerCustomer should throw exception for duplicate username")
    public void testRegisterCustomerDuplicateUsername() {
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> authService.registerCustomer("admin", "newpass", 10)
        );
        assertTrue(exception.getMessage().contains("already exists"));
    }
    
    // Mark Customer as Deleted Tests
    /** Test method. */
    @Test
    @DisplayName("markCustomerUserAsDeleted should mark user as deleted")
    public void testMarkCustomerUserAsDeleted() throws FlightBookingSystemException  {
        authService.markCustomerUserAsDeleted(1);
        
        FlightBookingSystemException exception = assertThrows(
            FlightBookingSystemException.class,
            () -> authService.login("aarav", "aarav123")
        );
        assertTrue(exception.getMessage().contains("deleted"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("markCustomerUserAsDeleted should allow undeleting")
    public void testMarkCustomerUserAsDeletedUndelete() throws FlightBookingSystemException  {
        authService.markCustomerUserAsDeleted(1, true);
        authService.markCustomerUserAsDeleted(1, false);
        
        boolean loginSuccess = authService.login("aarav", "aarav123");
        assertTrue(loginSuccess);
    }
    
    /** Test method. */
    @Test
    @DisplayName("markCustomerUserAsDeleted should handle non-existent customer")
    public void testMarkCustomerUserAsDeletedNonExistent() {
        assertDoesNotThrow(() -> authService.markCustomerUserAsDeleted(999));
    }
    
    // Integration Tests
    /** Test method. */
    @Test
    @DisplayName("Should switch between admin and customer login")
    public void testSwitchBetweenUsers() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        assertTrue(authService.isAdmin());
        
        authService.logout();
        authService.login("aarav", "aarav123");
        assertTrue(authService.isCustomer());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Should handle multiple login attempts")
    public void testMultipleLoginAttempts() throws FlightBookingSystemException  {
        assertThrows(FlightBookingSystemException.class, 
            () -> authService.login("admin", "wrong"));
        assertThrows(FlightBookingSystemException.class, 
            () -> authService.login("admin", "wrong2"));
        
        boolean success = authService.login("admin", "admin123");
        assertTrue(success);
    }
    
    /** Test method. */
    @Test
    @DisplayName("Should maintain login state across operations")
    public void testMaintainLoginState() throws FlightBookingSystemException  {
        authService.login("admin", "admin123");
        assertTrue(authService.isLoggedIn());
        
        // Perform other operations
        User user = authService.getCurrentUser();
        assertNotNull(user);
        
        // Should still be logged in
        assertTrue(authService.isLoggedIn());
        assertTrue(authService.isAdmin());
    }
    
    /** Test method. */
    @Test
    @DisplayName("Case sensitivity in username")
    public void testUsernamesCaseSensitive() {
        assertThrows(FlightBookingSystemException.class,
            () -> authService.login("ADMIN", "admin123"));
        assertThrows(FlightBookingSystemException.class,
            () -> authService.login("Admin", "admin123"));
    }
    
    /** Test method. */
    @Test
    @DisplayName("Password is case sensitive")
    public void testPasswordCaseSensitive() {
        assertThrows(FlightBookingSystemException.class,
            () -> authService.login("admin", "ADMIN123"));
        assertThrows(FlightBookingSystemException.class,
            () -> authService.login("admin", "Admin123"));
    }
}
