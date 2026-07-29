import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the Login screen, validates user input, and handles navigation
 * to the Dashboard or Registration screen.
 *
 * @author Yoko Mohr
 * @since 7/24/2026
 */

public class LoginController {

    // Connected to the username TextField in LoginScene.fxml.
    @FXML
    private TextField usernameField;

    // Connected to the password PasswordField in LoginScene.fxml.
    @FXML
    private PasswordField passwordField;

    // Displays validation or login error messages on the Login screen.
    @FXML
    private Label loginMessageLabel;

    // Stores the primary Stage passed from SceneFactory.
    // The controller uses this Stage to switch between scenes.
    private Stage stage;

    // Stores the primary application Stage for scene navigation.
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Handles the Login button action.
    // Validates the input, checks the database, and opens the Dashboard
    // when the credentials are correct.
    @FXML
    private void handleLogin() {
        // Read and trim the values entered in the Login form.
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Stop the login attempt if either required field is empty.
        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText(
                    "Please enter your username and password."
            );
            return;
        }
        UserDao userDao = new UserDao();
        User loggedInUser = userDao.checkLogin(username, password);

        if (loggedInUser != null) {
            // Store the authenticated User so other scenes can access it.
            SceneFactory.setLoggedInUser(loggedInUser);
            // Replace the current Login Scene with the Dashboard Scene.
            stage.setScene(
                    SceneFactory.create(SceneType.DASHBOARD, stage)
            );
        } else {
            // Keep the user on the Login screen and display an error.
            loginMessageLabel.setText(
                    "Incorrect username or password."
            );
        }
    }

    // Handles the Register button action and opens the Registration screen.
    @FXML
    private void handleRegister() {
        // Replace the current Login Scene with the Registration Scene.
        stage.setScene(
                SceneFactory.create(SceneType.REGISTER, stage)
        );
    }
}