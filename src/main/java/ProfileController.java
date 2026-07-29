import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * [CST338 Brief one-sentence description of what this class does]
 *
 * @author Yoko Mohr
 * @since 7/29/2026
 */
public class ProfileController {
  // Stores the primary Stage passed from SceneFactory.
  // The controller uses this Stage to navigate between scenes.
  private Stage stage;

  // Stores the primary Stage for scene navigation.
  public void setStage(Stage stage) {
    this.stage = stage;
    displayLoggedInUser();
  }

  @FXML
  private Label profileMessageLabel;

  // Connected to the username TextField in ProfileScene.fxml.
  @FXML
  private TextField profileUsernameInput;

  // Connected to the password PasswordField in ProfileScene.fxml.
  @FXML
  private PasswordField profilePasswordInput;

  // Connected to the first-name TextField in ProfileScene.fxml.
  @FXML
  private TextField profileFirstNameInput;

  // Connected to the last-name TextField in ProfileScene.fxml.
  @FXML
  private TextField profileLastNameInput;

  // Connected to the email TextField in ProfileScene.fxml.
  @FXML
  private TextField profileEmailInput;

  // Connected to the user role TextField in ProfileScene.fxml.
  @FXML
  private Label profileRoleLabel;

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

  // Handles the save action and returns to the log in screen.
  @FXML
  private void handleSave() {
    System.out.println("Save Changes clicked.");
    // Replace the current Profile Scene with the Login Scene.
    stage.setScene(
        SceneFactory.create(SceneType.LOGIN, stage)
    );
  }

  // Handles the delete action and returns to the log in screen.
  @FXML
  private void handleDelete() {
    System.out.println("Delete clicked.");
    // Replace the current Profile Scene with the Login Scene.
    stage.setScene(
        SceneFactory.create(SceneType.LOGIN, stage)
    );
  }

  // Handles the button action and returns to the Dashboard screen.
  @FXML
  private void handleBackToDashboard() {
    System.out.println("Back clicked.");
    // Replace the current Profile Scene with the Dashboard Scene.
    stage.setScene(
        SceneFactory.create(SceneType.DASHBOARD, stage)
    );
  }


}
