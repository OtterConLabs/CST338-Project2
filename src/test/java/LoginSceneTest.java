import static org.junit.jupiter.api.Assertions.assertNotNull;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

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
  private Stage stage;

  @Start
  void start(Stage stage) {
    this.stage = stage;
    stage.setScene(SceneFactory.create(SceneType.LOGIN, stage));
    stage.show();
  }

  @Test
  void clickingRegister_opensRegistrationScene(FxRobot robot) {
    robot.clickOn("#registerButton");

    assertNotNull(
        stage.getScene().lookup("#registrationRoot")
    );


  }
}