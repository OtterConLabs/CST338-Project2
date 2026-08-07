/**
 * Validates user account input before registration.
 *
 * @author Yoko Mohr
 * @since 7/31/2026
 */
public class AccountValidation {
  // Validates user account input before registration.
  public boolean areRegistrationFieldsValid(
      String username,
      String password,
      String firstName,
      String lastName,
      String email,
      UserRole role
  ) {
    return !username.isBlank()
        && isPasswordValid(password)
        && !firstName.isBlank()
        && !lastName.isBlank()
        && !email.isBlank()
        && role != null;
  }

  private boolean isPasswordValid(String password) {
    return password != null && password.length() >= 8;
  }

  public boolean areProfileFieldsValid(
      String username,
      String password,
      String firstName,
      String lastName,
      String email
  ) {
    return !username.isBlank()
        && isPasswordValid(password)
        && !firstName.isBlank()
        && !lastName.isBlank()
        && !email.isBlank();
  }
}
