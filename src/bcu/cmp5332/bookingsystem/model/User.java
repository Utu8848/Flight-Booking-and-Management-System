package bcu.cmp5332.bookingsystem.model;

/**
 * Represents a user in the system (Admin or Customer).
 * 
 * @author M9
 */

public class User {
    
    public enum UserType {
        ADMIN,
        CUSTOMER
    }
    
    /** Username field */
    private String username;
    /** Password field */
    private String password;
    /** Usertype field */
    private UserType userType;
    /** Customerid field */
    private Integer customerId; // Only for CUSTOMER type
    /** Deleted field */
    private boolean deleted; // Track if user is soft-deleted
    
    /**
     * Constructor for Admin user.
     */
    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.customerId = null;
        this.deleted = false;
    }
    
    /**
     * Constructor for Customer user (linked to a Customer record).
     */
    public User(String username, String password, UserType userType, int customerId) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.customerId = customerId;
        this.deleted = false;
    }
    
    /**
     * Full constructor including deleted status.
     */
    public User(String username, String password, UserType userType, Integer customerId, boolean deleted) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.customerId = customerId;
        this.deleted = deleted;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public boolean isAdmin() {
        return userType == UserType.ADMIN;
    }
    
    public boolean isCustomer() {
        return userType == UserType.CUSTOMER;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    @Override
    public String toString() {
        return username + "::" + password + "::" + userType + 
               (customerId != null ? "::" + customerId : "") +
               "::" + deleted;
    }
}
