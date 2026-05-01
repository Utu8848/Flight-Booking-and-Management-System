package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.model.User.UserType;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for User model.
 */
public class UserTest {
    
    private User adminUser;
    private User customerUser;
    
    @BeforeEach
    public void setUp() {
        adminUser = new User("admin", "admin123", UserType.ADMIN);
        customerUser = new User("aarav", "aarav123", UserType.CUSTOMER, 1);
    }
    
    @AfterEach
    public void tearDown() {
        adminUser = null;
        customerUser = null;
    }
    
    // Constructor Tests
    @Test
    @DisplayName("Admin constructor should initialize user correctly")
    public void testAdminConstructor() {
        assertEquals("admin", adminUser.getUsername());
        assertEquals("admin123", adminUser.getPassword());
        assertEquals(UserType.ADMIN, adminUser.getUserType());
        assertNull(adminUser.getCustomerId());
        assertFalse(adminUser.isDeleted());
    }
    
    @Test
    @DisplayName("Customer constructor should initialize user correctly")
    public void testCustomerConstructor() {
        assertEquals("aarav", customerUser.getUsername());
        assertEquals("aarav123", customerUser.getPassword());
        assertEquals(UserType.CUSTOMER, customerUser.getUserType());
        assertEquals(1, customerUser.getCustomerId());
        assertFalse(customerUser.isDeleted());
    }
    
    @Test
    @DisplayName("Full constructor should initialize all fields")
    public void testFullConstructor() {
        User fullUser = new User("test", "test123", UserType.CUSTOMER, 5, true);
        assertEquals("test", fullUser.getUsername());
        assertEquals("test123", fullUser.getPassword());
        assertEquals(UserType.CUSTOMER, fullUser.getUserType());
        assertEquals(5, fullUser.getCustomerId());
        assertTrue(fullUser.isDeleted());
    }
    
    // Getter Tests
    @Test
    @DisplayName("getUsername should return correct username")
    public void testGetUsername() {
        assertEquals("admin", adminUser.getUsername());
        assertEquals("aarav", customerUser.getUsername());
    }
    
    @Test
    @DisplayName("getPassword should return correct password")
    public void testGetPassword() {
        assertEquals("admin123", adminUser.getPassword());
        assertEquals("aarav123", customerUser.getPassword());
    }
    
    @Test
    @DisplayName("getUserType should return correct type")
    public void testGetUserType() {
        assertEquals(UserType.ADMIN, adminUser.getUserType());
        assertEquals(UserType.CUSTOMER, customerUser.getUserType());
    }
    
    @Test
    @DisplayName("getCustomerId should return null for admin")
    public void testGetCustomerIdForAdmin() {
        assertNull(adminUser.getCustomerId());
    }
    
    @Test
    @DisplayName("getCustomerId should return correct ID for customer")
    public void testGetCustomerIdForCustomer() {
        assertEquals(1, customerUser.getCustomerId());
    }
    
    // Type Check Tests
    @Test
    @DisplayName("isAdmin should return true for admin user")
    public void testIsAdminReturnsTrueForAdmin() {
        assertTrue(adminUser.isAdmin());
    }
    
    @Test
    @DisplayName("isAdmin should return false for customer user")
    public void testIsAdminReturnsFalseForCustomer() {
        assertFalse(customerUser.isAdmin());
    }
    
    @Test
    @DisplayName("isCustomer should return false for admin user")
    public void testIsCustomerReturnsFalseForAdmin() {
        assertFalse(adminUser.isCustomer());
    }
    
    @Test
    @DisplayName("isCustomer should return true for customer user")
    public void testIsCustomerReturnsTrueForCustomer() {
        assertTrue(customerUser.isCustomer());
    }
    
    // Deleted Status Tests
    @Test
    @DisplayName("isDeleted should return false initially")
    public void testIsDeletedInitiallyFalse() {
        assertFalse(adminUser.isDeleted());
        assertFalse(customerUser.isDeleted());
    }
    
    @Test
    @DisplayName("setDeleted should update deleted status")
    public void testSetDeleted() {
        adminUser.setDeleted(true);
        assertTrue(adminUser.isDeleted());
        
        adminUser.setDeleted(false);
        assertFalse(adminUser.isDeleted());
    }
    
    // toString Tests
    @Test
    @DisplayName("toString should format admin user correctly")
    public void testToStringAdmin() {
        String expected = "admin::admin123::ADMIN::false";
        assertEquals(expected, adminUser.toString());
    }
    
    @Test
    @DisplayName("toString should format customer user correctly")
    public void testToStringCustomer() {
        String expected = "aarav::aarav123::CUSTOMER::1::false";
        assertEquals(expected, customerUser.toString());
    }
    
    @Test
    @DisplayName("toString should include deleted status")
    public void testToStringWithDeletedStatus() {
        adminUser.setDeleted(true);
        assertTrue(adminUser.toString().endsWith("::true"));
        
        customerUser.setDeleted(true);
        assertTrue(customerUser.toString().endsWith("::true"));
    }
    
    // Edge Cases
    @Test
    @DisplayName("User should handle null username")
    public void testNullUsername() {
        User nullUser = new User(null, "password", UserType.ADMIN);
        assertNull(nullUser.getUsername());
    }
    
    @Test
    @DisplayName("User should handle null password")
    public void testNullPassword() {
        User nullUser = new User("username", null, UserType.ADMIN);
        assertNull(nullUser.getPassword());
    }
    
    @Test
    @DisplayName("User should handle empty strings")
    public void testEmptyStrings() {
        User emptyUser = new User("", "", UserType.ADMIN);
        assertEquals("", emptyUser.getUsername());
        assertEquals("", emptyUser.getPassword());
    }
    
    @Test
    @DisplayName("Customer user should handle null customer ID")
    public void testCustomerWithNullId() {
        User nullIdUser = new User("test", "test123", UserType.CUSTOMER, null, false);
        assertNull(nullIdUser.getCustomerId());
        assertTrue(nullIdUser.isCustomer());
    }
    
    @Test
    @DisplayName("User should handle special characters in credentials")
    public void testSpecialCharacters() {
        User specialUser = new User("user@name", "p@ss!w0rd#", UserType.ADMIN);
        assertEquals("user@name", specialUser.getUsername());
        assertEquals("p@ss!w0rd#", specialUser.getPassword());
    }
}
