import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * [CST338 Brief one-sentence description of what this class does]
 *
 * @author Yoko Mohr
 * @since 7/24/2026
 */

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessageLabel;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            loginMessageLabel.setText(
                    "Please enter your username and password."
            );
            return;
        }

        DatabaseManager db = DatabaseManager.getInstance();
        User loggedInUser = db.checkLogin(username, password);

        if (loggedInUser != null) {
            SceneFactory.setLoggedInUser(loggedInUser);
            stage.setScene(
                    SceneFactory.create(SceneType.DASHBOARD, stage)
            );
        } else {
            loginMessageLabel.setText(
                    "Incorrect username or password."
            );
        }
    }

    @FXML
    private void handleRegister() {
        stage.setScene(
                SceneFactory.create(SceneType.REGISTER, stage)
        );
    }
}