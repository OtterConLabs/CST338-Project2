import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;



/**
 * Tests navigation to the Grades scene using TestFX
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
class GradeSceneTest extends ApplicationTest{
    private Stage stage;
    @Override
    public void start(Stage stage){
        this.stage = stage;
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
        Button gradesButton = lookup("Grades").queryButton();
    
        interact(gradesButton::fire);
    
        WaitForAsyncUtils.waitForFxEvents();
    
        assertNotNull(
                stage.getScene().lookup(
                    "#gradeEntryRoot"
                ),

                "Grade Entry is to be opened from Dashboard"
        );
    }
}