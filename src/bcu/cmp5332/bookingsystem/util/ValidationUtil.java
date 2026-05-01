package bcu.cmp5332.bookingsystem.util;

import java.util.regex.Pattern;

/**
 * Utility class for validation operations.
 * Provides methods to validate email addresses and phone numbers.
 * 
 * @author M9
 */
public class ValidationUtil {
    
    /** 
     * Pattern for validating standard email addresses.
     * Allows alphanumeric characters, dots, underscores, hyphens, and plus signs in username.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9]+([._+-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.-][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$"
    );
    
    /** 
     * Pattern for validating Gmail-specific email addresses.
     * Matches gmail.com and googlemail.com domains with various country extensions.
     */
    private static final Pattern GMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9]+([._+-][a-zA-Z0-9]+)*@(gmail|googlemail)\\.(com|co\\.uk|co\\.in|com\\.au|de|fr|es|it|nl|be|ch|at|dk|se|no|fi|pl|ro|cz|ru|jp|cn|br|mx|ar)$",
        Pattern.CASE_INSENSITIVE
    );
    
    /** 
     * Pattern for validating phone numbers.
     * Requires exactly 10 digits with no formatting characters.
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10}$"
    );
    
    /**
     * Validates an email address.
     * Checks if the email follows standard email format.
     * 
     * @param email the email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates a Gmail address.
     * Checks if the email is specifically a Gmail address.
     * 
     * @param email the email address to validate
     * @return true if valid Gmail address, false otherwise
     */
    public static boolean isValidGmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return GMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates a phone number.
     * Checks if the phone number contains exactly 10 digits (no formatting allowed).
     * 
     * @param phone the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        String trimmedPhone = phone.trim();
        
        // Must match the phone pattern (exactly 10 digits, no formatting)
        return PHONE_PATTERN.matcher(trimmedPhone).matches();
    }
    
    /**
     * Gets a user-friendly error message for invalid email.
     * 
     * @param requireGmail if true, checks for Gmail specifically
     * @return error message
     */
    public static String getEmailErrorMessage(boolean requireGmail) {
        if (requireGmail) {
            return "Email must be a valid Gmail address (e.g., example@gmail.com, example@googlemail.com)";
        } else {
            return "Email must be in valid format (e.g., user@example.com)";
        }
    }
    
    /**
     * Gets a user-friendly error message for invalid phone.
     * 
     * @return error message
     */
    public static String getPhoneErrorMessage() {
        return "Phone number must be exactly 10 digits. " +
               "No spaces, hyphens, or special characters allowed.";
    }
}
