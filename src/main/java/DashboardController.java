import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controls the Dashboard screen, displays the logged-in user's information,
 * and handles navigation to the application's feature screens.
 *
 * @author Yoko Mohr
 * @since 7/24/2026
 */
public class DashboardController {

  // Stores the primary Stage passed from SceneFactory.
  // The controller uses this Stage to switch between scenes.
  private Stage stage;

  @FXML
  private ImageView dashboardLogoImageView;

  @FXML
  private void initialize() {
    Image logo = new Image(
        getClass().getResourceAsStream(
            "/images/ottercon-logo.png"
        )
    );

    dashboardLogoImageView.setImage(logo);
  }

  // Stores the primary application Stage for scene navigation.
  public void setStage(Stage stage) {
    this.stage = stage;
    displayLoggedInUser();
  }

  // Displays the logged-in user's full name.
  @FXML
  private Label userDisplayName;

  // Displays the logged-in user's role.
  @FXML
  private Label userRoleName;

  // Displays the currently logged-in user's name and role.
  private void displayLoggedInUser() {
    User user = SceneFactory.getLoggedInUser();

    if (user != null) {
      userDisplayName.setText(user.getFirstName() + " " + user.getLastName());
      String role = user.getRole().name().toLowerCase();
      role = role.substring(0, 1).toUpperCase()
          + role.substring(1);
      userRoleName.setText(role);
    } else {
      userDisplayName.setText("User information unavailable.");
      userRoleName.setText("");
      System.out.println("No user is currently logged in.");
    }
  }

  @FXML
  private void handleProfile () {
    stage.setScene(
        SceneFactory.create(SceneType.PROFILE, stage)
    );
  }

  @FXML
  private void handleCourses () {
    stage.setScene(
        SceneFactory.create(SceneType.COURSES, stage)
    );
  }

  @FXML
  private void handleAssignments () {
    stage.setScene(
        SceneFactory.create(SceneType.ASSIGNMENTS, stage)
    );
  }

  @FXML
  private void handleGrades () {
    stage.setScene(
        SceneFactory.create(SceneType.GRADES, stage)
    );
  }

  @FXML
  private void handleAttendance () {
    stage.setScene(
        SceneFactory.create(SceneType.ATTENDANCE, stage)
    );
  }

  // Handles the Logout button action and returns to the Login screen.
  @FXML
  private void handleLogout() {
    SceneFactory.setLoggedInUser(null);
    stage.setScene(
        SceneFactory.create(SceneType.LOGIN, stage)
    );
  }
}
