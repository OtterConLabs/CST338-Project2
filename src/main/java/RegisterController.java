import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controls the Registration screen, validates user input,
 * creates new User objects, and saves them to the database.
 *
 * @author Yoko Mohr
 * @since 7/24/2026
 */

public class RegisterController {

    // Connected to the username TextField in RegisterScene.fxml.
    @FXML
    private TextField registerUsernameInput;

    // Connected to the password PasswordField in RegisterScene.fxml.
    @FXML
    private PasswordField registerPasswordInput;

    // Connected to the first-name TextField in RegisterScene.fxml.
    @FXML
    private TextField firstNameInput;

    // Connected to the last-name TextField in RegisterScene.fxml.
    @FXML
    private TextField lastNameInput;

    // Connected to the email TextField in RegisterScene.fxml.
    @FXML
    private TextField emailInput;

    // Displays the available UserRole (STUDENT/TEACHER) values in the Registration form.
    @FXML
    private ComboBox<UserRole> roleInput;

    // Displays validation or error messages on the screen.
    @FXML
    private Label registerMessageLabel;

    // Stores the primary Stage passed from SceneFactory.
    // The controller uses this Stage to navigate between scenes.
    private Stage stage;

    // Stores the primary Stage for scene navigation.
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Initializes the role ComboBox after the FXML file is loaded.
    // All values from the UserRole enum are added to the dropdown menu.
    @FXML
    private void initialize() {
        roleInput.setItems(
                FXCollections.observableArrayList(UserRole.values())
        );
    }

    // Handles the Register button action.
    // Validates the form, creates a new User object,
    // and inserts the user into the database.
    @FXML
    private void handleRegister() {
        // Read and trim the values entered in the Registration form.
        String username = registerUsernameInput.getText().trim();
        String firstName = firstNameInput.getText().trim();
        String lastName = lastNameInput.getText().trim();
        String email = emailInput.getText().trim();
        String password = registerPasswordInput.getText(); // no trim needed
        UserRole role = roleInput.getValue();

        AccountValidation accountValidation = new AccountValidation();

        if (!accountValidation.isPasswordValid(password)) {
            registerMessageLabel.setText(
                "Password must be at least 8 characters."
            );
            registerMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!accountValidation.areRegistrationFieldsValid(username,password,firstName,lastName,email,role)) {
            registerMessageLabel.setText("Please complete all fields.");
            registerMessageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        // Create a User object using the values entered in the form.
        User newUser = new User(
            username,
            firstName,
            lastName,
            email,
            password,
            role
        );
        UserDao userDao = new UserDao();
        boolean inserted = userDao.insertUser(newUser);
        if (inserted) {
            registerMessageLabel.setText(
                "Account created successfully."
            );
            registerMessageLabel.setStyle("-fx-text-fill: black;");
        } else {
            registerMessageLabel.setText(
                "Unable to create account. "
                    + "The username or email may already be in use."
            );
            registerMessageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    // Handles the Back button action and returns to the Login screen.
    @FXML
    private void handleBack() {
        // Replace the current Registration Scene with the Login Scene.
        stage.setScene(
                SceneFactory.create(SceneType.LOGIN, stage)
        );
    }
}