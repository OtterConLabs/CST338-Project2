import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import java.util.concurrent.TimeUnit;

/**
 * Tests the Login scene transitions using TestFX.
 *
 * <p>The initial test structure was drafted with assistance from ChatGPT.
 * The code was reviewed, modified, and verified by the author.</p>
 *
 * @author Yoko Mohr
 * @since 8/1/2026
 */

@ExtendWith(ApplicationExtension.class)
class LoginSceneTest {
  private static final String TEST_USERNAME = "ui_test_user";
  private static final String TEST_PASSWORD = "ui_test_password";

  private Stage stage;
  private UserDao userDao;
  private User testUser;
  private static final String NEW_USERNAME = "new_ui_test_user";
  private static final String NEW_PASSWORD = "new_password";

  @AfterEach
  void tearDown() {
    deleteUserIfPresent(TEST_USERNAME, TEST_PASSWORD);
    deleteUserIfPresent(NEW_USERNAME, NEW_PASSWORD);
  }

  private void deleteUserIfPresent(
      String username,
      String password
  ) {
    User savedUser = userDao.checkLogin(username, password);

    if (savedUser != null) {
      assertTrue(
          userDao.deleteUser(savedUser),
          "Test user should be deleted after the test"
      );
    }
  }

  @Start
  void start(Stage stage) {
    this.stage = stage;
    userDao = new UserDao();

    // Remove leftover test data from an interrupted previous test run.
    deleteUserIfPresent(TEST_USERNAME, TEST_PASSWORD);
    deleteUserIfPresent(NEW_USERNAME, NEW_PASSWORD);

    testUser = new User(
        TEST_USERNAME,
        "UI",
        "Test",
        "ui_test_user@csumb.edu",
        TEST_PASSWORD,
        UserRole.STUDENT
    );

    assertTrue(
        userDao.insertUser(testUser),
        "Test user should be inserted successfully"
    );

    stage.setScene(SceneFactory.create(SceneType.LOGIN, stage));
    stage.show();
  }

  @Test
  void clickingRegister_opensRegistrationScene(FxRobot robot) {
    robot.clickOn("#registerButton");

    WaitForAsyncUtils.waitForFxEvents();

    assertNotNull(
        stage.getScene().lookup("#registrationRoot"),
        "Registration scene should open after clicking Register"
    );
  }

  @Test
  void validLogin_opensDashboardScene(FxRobot robot) {
    robot.clickOn("#usernameField")
        .write(testUser.getUsername());

    robot.clickOn("#passwordField")
        .write(testUser.getPassword());

    robot.clickOn("#loginButton");

    WaitForAsyncUtils.waitForFxEvents();

    assertNotNull(
        stage.getScene().lookup("#dashboardRoot"),
        "Dashboard scene should open after a valid login"
    );
  }

  @Test
  void duplicateUser_thenNewUserRegistration_succeeds(FxRobot robot) {
    // Open the Registration scene.
    robot.clickOn("#registerButton");
    WaitForAsyncUtils.waitForFxEvents();

    assertNotNull(
        stage.getScene().lookup("#registrationRoot"),
        "Registration scene should open"
    );

    // First attempt: use a username that already exists.
    fillRegistrationForm(
        robot,
        TEST_USERNAME,
        "duplicate_email@csumb.edu"
    );

    robot.clickOn("#registerSubmitButton");
    WaitForAsyncUtils.waitForFxEvents();

    Label messageLabel =
        robot.lookup("#registerMessageLabel")
            .queryAs(Label.class);

    assertEquals(
        "Unable to create account. "
            + "The username or email may already be in use.",
        messageLabel.getText()
    );

    assertTrue(
        messageLabel.getStyle().contains("-fx-text-fill: red"),
        "Duplicate account error should be displayed in red"
    );

    WaitForAsyncUtils.sleep(3, TimeUnit.SECONDS);

    // Clear the form and enter unique account information.
    clearRegistrationForm(robot);

    fillRegistrationForm(
        robot,
        "new_ui_test_user",
        "new_ui_test_user@csumb.edu"
    );

    robot.clickOn("#registerSubmitButton");
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(
        "Account created successfully.",
        messageLabel.getText()
    );

    WaitForAsyncUtils.sleep(3, TimeUnit.SECONDS);
  }

  private void fillRegistrationForm(
      FxRobot robot,
      String username,
      String email
  ) {
    robot.clickOn("#registerUsernameInput")
        .write(username);

    robot.clickOn("#registerPasswordInput")
        .write(NEW_PASSWORD);

    robot.clickOn("#firstNameInput")
        .write("New");

    robot.clickOn("#lastNameInput")
        .write("User");

    robot.clickOn("#emailInput")
        .write(email);

    robot.clickOn("#roleInput");
    robot.clickOn(UserRole.STUDENT.toString());
  }

  private void clearRegistrationForm(FxRobot robot) {
    robot.lookup("#registerUsernameInput")
        .queryAs(TextField.class)
        .clear();

    robot.lookup("#registerPasswordInput")
        .queryAs(PasswordField.class)
        .clear();

    robot.lookup("#firstNameInput")
        .queryAs(TextField.class)
        .clear();

    robot.lookup("#lastNameInput")
        .queryAs(TextField.class)
        .clear();

    robot.lookup("#emailInput")
        .queryAs(TextField.class)
        .clear();
  }

  @Test
  void invalidLogin_thenValidLogin_opensDashboardScene(FxRobot robot) {
    // First, enter incorrect login information.
    robot.clickOn("#usernameField")
        .write("wrong_username");

    robot.clickOn("#passwordField")
        .write("wrong_password");

    robot.clickOn("#loginButton");

    WaitForAsyncUtils.waitForFxEvents();

    Label loginMessage =
        robot.lookup("#loginMessageLabel").queryAs(Label.class);

    assertEquals(
        "Incorrect username or password.",
        loginMessage.getText()
    );
    // Pause for visual confirmation.
    WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS);

    // Clear the incorrect information.
    TextField usernameField =
        robot.lookup("#usernameField").queryAs(TextField.class);

    PasswordField passwordField =
        robot.lookup("#passwordField").queryAs(PasswordField.class);

    usernameField.clear();
    passwordField.clear();

    // Enter the valid login information.
    robot.clickOn("#usernameField")
        .write(testUser.getUsername());

    robot.clickOn("#passwordField")
        .write(testUser.getPassword());

    robot.clickOn("#loginButton");
    // Pause for visual confirmation.
    WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS);

    assertNotNull(
        stage.getScene().lookup("#dashboardRoot"),
        "Dashboard scene should open after correcting the login information"
    );
  }
}