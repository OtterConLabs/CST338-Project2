import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

/**
 * Tests navigation to the Grades scene using TestFX
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
class GradeSceneTest extends ApplicationTest{
    @Override
    public void start(Stage stage){
        User teacher = new User(
                91,
                "jay",
                "Jay",
                "Tee",
                "jt@csumb.edu",
                "password",
                UserRole.TEACHER,
                null
        );

        SceneFactory.setLoggedInUser(teacher);

        Scene dashboardScene = SceneFactory.create(
                SceneType.DASHBOARD,
                stage
        );

        stage.setScene(dashboardScene);
        stage.show();
    }

    @AfterEach
    void tearDown(){
        SceneFactory.setLoggedInUser(null);
        DatabaseManager.getInstance().close();
    }

    @Test
    void dashboardGradesButtonOpensGradeEntry(){
        clickOn("Grades");

        WaitForAsyncUtils.waitForFxEvents();

        verifyThat(
                "#gradeEntryRoot",
                isVisible()
        );
    }
}