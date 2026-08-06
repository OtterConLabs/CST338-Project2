import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import javafx.scene.control.Button;
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

/**
 * Tests the Login scene transitions using TestFX.
 *
 * <p>The initial test structure was drafted with assistance from ChatGPT.
 * The code was reviewed, modified, and verified by the author.</p>
 *
 * The prompt I used:
 * Create a TestFX test class for my JavaFX login scene.
 * The tests should verify that:
 * Clicking the Register button opens the registration scene.
 * Entering a valid username and password and clicking Login opens the dashboard scene.
 * Use ApplicationExtension, and FxRobot
 * The test should create a temporary test user before each test and delete it afterward.
 *
 * @author Yoko Mohr
 * @since 8/1/2026
 */

@ExtendWith(ApplicationExtension.class)
class LoginSceneTest {
  // Test account credentials reused during setup, login, and cleanup.
  private static final String TEST_PASSWORD = "ui_test_password";

  private Stage stage;
  private UserDao userDao;
  private User testUser;

  /**
   * Prepares the JavaFX stage and creates a unique test user before each test.
   *
   * @param stage the JavaFX stage provided by TestFX
   */
  @Start
  void start(Stage stage) {
    this.stage = stage;
    // Reset shared session state so one test cannot affect another.
    SceneFactory.setLoggedInUser(null);
    // Use the application's shared database connection.
    userDao = new UserDao();

    String uniqueId = Long.toString(System.nanoTime());
    String testUsername = "ui_test_user_" + uniqueId;
    String testEmail = testUsername + "@csumb.edu";

    // Create a unique account for the valid-login test.
    testUser = new User(
        testUsername,
        "UI",
        "Test",
        testEmail,
        TEST_PASSWORD,
        UserRole.STUDENT
    );

    assertTrue(
        userDao.insertUser(testUser),
        "Test user should be inserted successfully"
    );
    // Start every test from the Login scene.
    stage.setScene(SceneFactory.create(SceneType.LOGIN, stage));
    stage.show();
  }

  /**
   * Clears shared login state and removes the test account after each test.
   */
  @AfterEach
  void tearDown() {
    SceneFactory.setLoggedInUser(null);

    if (userDao == null || testUser == null) {
      return;
    }

    User savedUser = userDao.checkLogin(
        testUser.getUsername(),
        testUser.getPassword());

    if (savedUser != null) {
      assertTrue(
          userDao.deleteUser(savedUser),
          "Test user should be deleted after the test"
      );
    }
  }

  /**
   * Verifies that activating Register opens the Registration scene.
   *
   * @param robot the TestFX robot used to locate and activate the Register button
   */
  @Test
  void activatingRegister_opensRegistrationScene(FxRobot robot) {
    Button registerButton =
        robot.lookup("#registerButton").queryAs(Button.class);

    // Run the button action on the JavaFX Application Thread.
    robot.interact(registerButton::fire);

    WaitForAsyncUtils.waitForFxEvents();

    assertNotNull(
        stage.getScene().lookup("#registrationRoot"),
        "Registration scene should open after activating Register"
    );
  }

  /**
   * Verifies that valid credentials open the Dashboard scene.
   *
   * @param robot the TestFX robot used to locate and interact with UI controls
   */
  @Test
  void validLogin_opensDashboardScene(FxRobot robot) {
    TextField usernameField =
        robot.lookup("#usernameField").queryAs(TextField.class);

    PasswordField passwordField =
        robot.lookup("#passwordField").queryAs(PasswordField.class);

    // Set the login credentials on the JavaFX Application Thread.
    robot.interact(() -> {
      usernameField.setText(testUser.getUsername());
      passwordField.setText(testUser.getPassword());
    });

    WaitForAsyncUtils.waitForFxEvents();

    Button loginButton =
        robot.lookup("#loginButton").queryAs(Button.class);

    // Run the login action without depending on OS mouse focus.
    robot.interact(loginButton::fire);

    WaitForAsyncUtils.waitForFxEvents();

    assertNotNull(
        stage.getScene().lookup("#dashboardRoot"),
        "Dashboard scene should open after a valid login"
    );
  }
}