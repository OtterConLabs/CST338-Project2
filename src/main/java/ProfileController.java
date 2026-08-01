import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the Profile screen, displays the logged-in user's information,
 * and handles profile updates, account deletion, and scene navigation.
 *
 * @author Yoko Mohr
 * @since 7/29/2026
 */
public class ProfileController {
  // Stores the primary Stage used for scene navigation.
  private Stage stage;

  // Displays validation, update, or deletion error messages.
  @FXML
  private Label profileMessageLabel;

  // Displays and edits the logged-in user's username.
  @FXML
  private TextField profileUsernameInput;

  // Displays and edits the logged-in user's password.
  @FXML
  private PasswordField profilePasswordInput;

  // Displays and edits the logged-in user's first name.
  @FXML
  private TextField profileFirstNameInput;

  // Displays and edits the logged-in user's last name.
  @FXML
  private TextField profileLastNameInput;

  // Displays and edits the logged-in user's email address.
  @FXML
  private TextField profileEmailInput;

  // Displays the logged-in user's role.
  // The role is not editable from the Profile screen.
  @FXML
  private Label profileRoleLabel;

  // Stores the primary application Stage and loads the logged-in
  // user's information into the Profile form.
  public void setStage(Stage stage) {
    this.stage = stage;
    displayLoggedInUser();
  }

  //  Displays the currently logged-in user's account information.
  private void displayLoggedInUser() {
    User user = SceneFactory.getLoggedInUser();
    if (user != null) {
      profileUsernameInput.setText(user.getUsername());
      profilePasswordInput.setText(user.getPassword());
      profileFirstNameInput.setText(user.getFirstName());
      profileLastNameInput.setText(user.getLastName());
      profileEmailInput.setText(user.getEmail());
      profileRoleLabel.setText(user.getRole().name());
    } else {
      profileMessageLabel.setText("No User Profile");
    }

  }

  // Handles the Save Changes button action.
  // Validates the form, updates the user's database row,
  // and replaces the stored logged-in User with the updated User.
  @FXML
  private void handleSave() {
    String username = profileUsernameInput.getText().trim();
    String password = profilePasswordInput.getText();
    String firstName = profileFirstNameInput.getText().trim();
    String lastName = profileLastNameInput.getText().trim();
    String email = profileEmailInput.getText().trim();

    // Stop the update if any required field is empty.
    if (username.isEmpty() ||
        password.isEmpty() ||
        firstName.isEmpty() ||
        lastName.isEmpty() ||
        email.isEmpty()) {
      profileMessageLabel.setText("Please complete all fields.");
      return;
    }
    // Retrieve the User whose account is currently logged in.
    User currentUser = SceneFactory.getLoggedInUser();
    // Create an updated User object while preserving the original
    // database ID, role, and account creation date.
    User user = new User(
          currentUser.getId(),
          username,
          firstName,
          lastName,
          email,
          password,
          currentUser.getRole(),
          currentUser.getDatetime()
    );
    UserDao userDao = new UserDao();
    // Update the matching database row through UserDao.
    boolean result = userDao.updateUser(user);
    // Replace the stored session User so other scenes display
    // the updated account information.
    if (result) {
      profileMessageLabel.setText("Profile updated successfully.");
      SceneFactory.setLoggedInUser(user);
    } else {
      profileMessageLabel.setText("The username or email may already be in use. "
          + "Please try again.");
    }
  }

  // Handles the Delete Account button action.
  // Deletes the logged-in user's database row, clears the login session,
  // and returns to the Login screen.
  @FXML
  private void handleDelete() {
    // TODO: Add a confirmation dialog before deleting the account.
    User user = SceneFactory.getLoggedInUser();
    UserDao userDao = new UserDao();
    boolean result = userDao.deleteUser(user);

    if (result) {
      // Clear the deleted user from the current login session.
      SceneFactory.setLoggedInUser(null);
      // Return to the Login screen because the account no longer exists.
      stage.setScene(
          SceneFactory.create(SceneType.LOGIN, stage)
      );
    } else {
      profileMessageLabel.setText("Unable to delete the user profile.");
    }
  }

  // Handles the button action and returns to the Dashboard screen.
  @FXML
  private void handleBackToDashboard() {
    // Replace the current Profile Scene with the Dashboard Scene.
    stage.setScene(
        SceneFactory.create(SceneType.DASHBOARD, stage)
    );
  }
}
