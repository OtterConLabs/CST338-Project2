import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * [CST338 Brief one-sentence description of what this class does]
 *
 * @author Yoko Mohr
 * @since 7/24/2026
 */


public class RegisterController {

    @FXML
    private TextField registerUsernameInput;

    @FXML
    private PasswordField registerPasswordInput;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private TextField emailInput;

    @FXML
    private ComboBox<UserRole> roleInput;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        roleInput.setItems(
                FXCollections.observableArrayList(UserRole.values())
        );
    }

    @FXML
    private void handleRegister() {
        User newUser = new User(
            registerUsernameInput.getText(),
            firstNameInput.getText(),
            lastNameInput.getText(),
            emailInput.getText(),
            registerPasswordInput.getText(),
            roleInput.getValue()
        );
        DatabaseManager db = DatabaseManager.getInstance();
        db.insertUser(newUser);
        System.out.println("Register button clicked.");
    }

    @FXML
    private void handleBack() {
        stage.setScene(
                SceneFactory.create(SceneType.LOGIN, stage)
        );
    }
}