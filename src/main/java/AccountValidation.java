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
        && !password.isBlank()
        && !firstName.isBlank()
        && !lastName.isBlank()
        && !email.isBlank()
        && role != null;
  }

  public boolean areProfileFieldsValid(
      String username,
      String password,
      String firstName,
      String lastName,
      String email
  ) {
    return !username.isBlank()
        && !password.isBlank()
        && !firstName.isBlank()
        && !lastName.isBlank()
        && !email.isBlank();
  }
}
