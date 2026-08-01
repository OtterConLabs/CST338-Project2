import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Start
  void start(Stage stage) {
    this.stage = stage;
    userDao = new UserDao();

    // Remove leftover test data from an interrupted previous test run.
    User existingUser = userDao.checkLogin(TEST_USERNAME, TEST_PASSWORD);
    if (existingUser != null) {
      userDao.deleteUser(existingUser);
    }

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

  @AfterEach
  void tearDown() {
    User savedUser = userDao.checkLogin(TEST_USERNAME, TEST_PASSWORD);

    if (savedUser != null) {
      assertTrue(
          userDao.deleteUser(savedUser),
          "Test user should be deleted after the test"
      );
    }
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
}