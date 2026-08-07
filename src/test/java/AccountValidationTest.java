import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests registration and profile field validation in AccountValidation.
 *
 * @author Yoko Mohr
 * @since 7/31/2026
 */
class AccountValidationTest {

  private AccountValidation accountValidation;

  @BeforeEach
  void setUp() {
    accountValidation = new AccountValidation();
  }

  // Verifies that complete account register information for new user is accepted.
  @Test
  void completeRegistrationFieldsReturnTrue() {
    boolean result = accountValidation.areRegistrationFieldsValid(
        "unit_test",
        "unit_test",
        "Unit",
        "Test",
        "unit@test.edu",
        UserRole.STUDENT
    );
    assertTrue(result);
  }

  // Verifies that registration is rejected when the username is empty.
  @Test
  void emptyUsernameReturnsFalse() {
    boolean result = accountValidation.areRegistrationFieldsValid(
        "",
        "unit_test",
        "Unit",
        "Test",
        "unit@test.edu",
        UserRole.STUDENT
    );
    assertFalse(result);
  }

  // Verifies that registration is rejected when no role is selected.
  @Test
  void missingRoleReturnsFalse() {
    boolean result = accountValidation.areRegistrationFieldsValid(
        "unit_test",
        "unit_test",
        "Unit",
        "Test",
        "unit@test.edu",
        null
    );
    assertFalse(result);
  }

  // Verifies that complete account profile information update for existing user is accepted.
  @Test
  void completeProfileFieldsReturnTrue() {
    boolean result = accountValidation.areProfileFieldsValid(
        "unit_test",
        "unit_test",
        "Unit",
        "Test",
        "unit@test.edu"
    );
    assertTrue(result);
  }

  // Verifies that a profile update is rejected when the email is blank.
  @Test
  void blankProfileEmailReturnsFalse() {
    boolean result = accountValidation.areProfileFieldsValid(
        "unit_test",
        "unit_test",
        "Unit",
        "Test",
        ""
    );
    assertFalse(result);
  }

  @Test
  void shortPasswordReturnsFalse() {
    boolean result = accountValidation.areRegistrationFieldsValid(
        "unit_test",
        "short",
        "Unit",
        "Test",
        "unit@test.edu",
        UserRole.STUDENT
    );
    assertFalse(result);
  }
}