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

    // setting the dropdown items
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
        String password = registerPasswordInput.getText().trim();
        UserRole role = roleInput.getValue();

        // Stop the registration process if any required field is empty.
        if (username.isEmpty()
                || firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || role == null) {
            registerMessageLabel.setText("Please complete all fields.");
//            System.out.println("Please complete all fields.");
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
        // Retrieve the shared DatabaseManager Singleton.
        DatabaseManager db = DatabaseManager.getInstance();
        // Insert the new user's account information into the users table.
        db.insertUser(newUser);
        registerMessageLabel.setText("Account created successfully.");
//        System.out.println("User registration submitted.");
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