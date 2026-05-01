package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.util.ValidationUtil;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ValidationUtil.
 */
public class ValidationUtilTest {
    
    // Email Validation Tests
    @Test
    @DisplayName("Valid email should pass validation")
    public void testValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("user@example.com"));
        assertTrue(ValidationUtil.isValidEmail("test.user@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user_name@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user-name@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user123@example.com"));
    }
    
    @Test
    @DisplayName("Valid email with multiple dots should pass")
    public void testValidEmailMultipleDots() {
        assertTrue(ValidationUtil.isValidEmail("user@mail.example.com"));
        assertTrue(ValidationUtil.isValidEmail("user@example.co.uk"));
    }
    
    @Test
    @DisplayName("Invalid email should fail validation")
    public void testInvalidEmail() {
        assertFalse(ValidationUtil.isValidEmail("notanemail"));
        assertFalse(ValidationUtil.isValidEmail("@example.com"));
        assertFalse(ValidationUtil.isValidEmail("user@"));
        assertFalse(ValidationUtil.isValidEmail("user@.com"));
        assertFalse(ValidationUtil.isValidEmail("user@example"));
        assertFalse(ValidationUtil.isValidEmail("user example@example.com"));
    }
    
    @Test
    @DisplayName("Null email should fail validation")
    public void testNullEmail() {
        assertFalse(ValidationUtil.isValidEmail(null));
    }
    
    @Test
    @DisplayName("Empty email should fail validation")
    public void testEmptyEmail() {
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail("   "));
    }
    
    @Test
    @DisplayName("Email with whitespace should be trimmed and validated")
    public void testEmailWithWhitespace() {
        assertTrue(ValidationUtil.isValidEmail("  user@example.com  "));
    }
    
    // Gmail Validation Tests
    @Test
    @DisplayName("Valid Gmail should pass validation")
    public void testValidGmail() {
        assertTrue(ValidationUtil.isValidGmail("user@gmail.com"));
        assertTrue(ValidationUtil.isValidGmail("test.user@gmail.com"));
        assertTrue(ValidationUtil.isValidGmail("user_name@gmail.com"));
        assertTrue(ValidationUtil.isValidGmail("user123@gmail.com"));
    }
    
    @Test
    @DisplayName("Valid Googlemail should pass validation")
    public void testValidGooglemail() {
        assertTrue(ValidationUtil.isValidGmail("user@googlemail.com"));
    }
    
    @Test
    @DisplayName("Valid Gmail with country domains should pass")
    public void testValidGmailCountryDomains() {
        assertTrue(ValidationUtil.isValidGmail("user@gmail.co.uk"));
        assertTrue(ValidationUtil.isValidGmail("user@gmail.co.in"));
        assertTrue(ValidationUtil.isValidGmail("user@gmail.de"));
        assertTrue(ValidationUtil.isValidGmail("user@gmail.fr"));
    }
    
    @Test
    @DisplayName("Gmail validation should be case insensitive")
    public void testGmailCaseInsensitive() {
        assertTrue(ValidationUtil.isValidGmail("user@GMAIL.COM"));
        assertTrue(ValidationUtil.isValidGmail("user@Gmail.com"));
        assertTrue(ValidationUtil.isValidGmail("user@GmAiL.CoM"));
    }
    
    @Test
    @DisplayName("Non-Gmail should fail Gmail validation")
    public void testInvalidGmail() {
        assertFalse(ValidationUtil.isValidGmail("user@yahoo.com"));
        assertFalse(ValidationUtil.isValidGmail("user@hotmail.com"));
        assertFalse(ValidationUtil.isValidGmail("user@example.com"));
    }
    
    @Test
    @DisplayName("Null Gmail should fail validation")
    public void testNullGmail() {
        assertFalse(ValidationUtil.isValidGmail(null));
    }
    
    @Test
    @DisplayName("Empty Gmail should fail validation")
    public void testEmptyGmail() {
        assertFalse(ValidationUtil.isValidGmail(""));
        assertFalse(ValidationUtil.isValidGmail("   "));
    }
    
    // Phone Validation Tests
    @Test
    @DisplayName("Valid 10-digit phone should pass validation")
    public void testValidPhone() {
        assertTrue(ValidationUtil.isValidPhone("9841234567"));
        assertTrue(ValidationUtil.isValidPhone("1234567890"));
        assertTrue(ValidationUtil.isValidPhone("0000000000"));
        assertTrue(ValidationUtil.isValidPhone("9999999999"));
    }
    
    @Test
    @DisplayName("Invalid phone format should fail validation")
    public void testInvalidPhoneFormat() {
        assertFalse(ValidationUtil.isValidPhone("123-456-7890"));
        assertFalse(ValidationUtil.isValidPhone("123 456 7890"));
        assertFalse(ValidationUtil.isValidPhone("+1234567890"));
        assertFalse(ValidationUtil.isValidPhone("(123) 456-7890"));
    }
    
    @Test
    @DisplayName("Phone with wrong length should fail validation")
    public void testInvalidPhoneLength() {
        assertFalse(ValidationUtil.isValidPhone("123"));
        assertFalse(ValidationUtil.isValidPhone("12345"));
        assertFalse(ValidationUtil.isValidPhone("123456789"));
        assertFalse(ValidationUtil.isValidPhone("12345678901"));
    }
    
    @Test
    @DisplayName("Phone with letters should fail validation")
    public void testPhoneWithLetters() {
        assertFalse(ValidationUtil.isValidPhone("123456789a"));
        assertFalse(ValidationUtil.isValidPhone("abcdefghij"));
    }
    
    @Test
    @DisplayName("Null phone should fail validation")
    public void testNullPhone() {
        assertFalse(ValidationUtil.isValidPhone(null));
    }
    
    @Test
    @DisplayName("Empty phone should fail validation")
    public void testEmptyPhone() {
        assertFalse(ValidationUtil.isValidPhone(""));
        assertFalse(ValidationUtil.isValidPhone("   "));
    }
    
    @Test
    @DisplayName("Phone with whitespace should be trimmed and validated")
    public void testPhoneWithWhitespace() {
        assertTrue(ValidationUtil.isValidPhone("  9841234567  "));
    }
    
    @Test
    @DisplayName("Phone with special characters should fail")
    public void testPhoneWithSpecialCharacters() {
        assertFalse(ValidationUtil.isValidPhone("984-123-4567"));
        assertFalse(ValidationUtil.isValidPhone("984.123.4567"));
        assertFalse(ValidationUtil.isValidPhone("984*123*4567"));
    }
    
    // Error Message Tests
    @Test
    @DisplayName("getEmailErrorMessage should return general error for standard email")
    public void testGetEmailErrorMessageGeneral() {
        String message = ValidationUtil.getEmailErrorMessage(false);
        assertTrue(message.contains("valid format"));
        assertTrue(message.contains("user@example.com"));
    }
    
    @Test
    @DisplayName("getEmailErrorMessage should return Gmail error when required")
    public void testGetEmailErrorMessageGmail() {
        String message = ValidationUtil.getEmailErrorMessage(true);
        assertTrue(message.contains("Gmail"));
        assertTrue(message.contains("gmail.com"));
    }
    
    @Test
    @DisplayName("getPhoneErrorMessage should return correct error")
    public void testGetPhoneErrorMessage() {
        String message = ValidationUtil.getPhoneErrorMessage();
        assertTrue(message.contains("10 digits"));
        assertTrue(message.contains("No spaces"));
    }
    
    // Edge Cases
    @Test
    @DisplayName("Email with consecutive dots should fail")
    public void testEmailConsecutiveDots() {
        assertFalse(ValidationUtil.isValidEmail("user..name@example.com"));
    }
    
    @Test
    @DisplayName("Email starting with dot should fail")
    public void testEmailStartingWithDot() {
        assertFalse(ValidationUtil.isValidEmail(".user@example.com"));
    }
    
    @Test
    @DisplayName("Email ending with dot should fail")
    public void testEmailEndingWithDot() {
        assertFalse(ValidationUtil.isValidEmail("user.@example.com"));
    }
    
    @Test
    @DisplayName("Email with special characters should pass if valid")
    public void testEmailWithSpecialCharacters() {
        assertTrue(ValidationUtil.isValidEmail("user+filter@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user_name@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user-name@example.com"));
    }
    
    @Test
    @DisplayName("Phone with leading zeros should be valid")
    public void testPhoneWithLeadingZeros() {
        assertTrue(ValidationUtil.isValidPhone("0012345678"));
    }
    
    @Test
    @DisplayName("Real world email examples should validate correctly")
    public void testRealWorldEmails() {
        assertTrue(ValidationUtil.isValidEmail("aarav.sharma@gmail.com"));
        assertTrue(ValidationUtil.isValidEmail("binita_rai@yahoo.com"));
        assertTrue(ValidationUtil.isValidEmail("chiranjivi123@hotmail.com"));
        assertTrue(ValidationUtil.isValidEmail("diya.thapa@company.co.uk"));
    }
    
    @Test
    @DisplayName("Real world phone examples should validate correctly")
    public void testRealWorldPhones() {
        assertTrue(ValidationUtil.isValidPhone("9841234567"));
        assertTrue(ValidationUtil.isValidPhone("9851234567"));
        assertTrue(ValidationUtil.isValidPhone("9801234567"));
        assertTrue(ValidationUtil.isValidPhone("9861234567"));
    }
    
    @Test
    @DisplayName("Email with subdomain should validate")
    public void testEmailWithSubdomain() {
        assertTrue(ValidationUtil.isValidEmail("user@mail.example.com"));
        assertTrue(ValidationUtil.isValidEmail("user@support.company.co.uk"));
    }
    
    @Test
    @DisplayName("Gmail with plus addressing should validate")
    public void testGmailWithPlusAddressing() {
        assertTrue(ValidationUtil.isValidGmail("user+tag@gmail.com"));
        assertTrue(ValidationUtil.isValidGmail("user+filter@googlemail.com"));
    }
}
