import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Tests navigation to the Attendance Roster through TestFX.
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
class AttendanceSceneTest extends ApplicationTest{
    private Stage stage;

    @Override
    public void start(Stage stage){
        this.stage = stage;

        User teacher = new User(
            99,
            "yoko",
            "Yoko",
            "Mohr",
            "y@csumb.edu",
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
    void dashboardAttendanceButtonOpensAttendanceRoster(){
        Button attendanceButton = lookup("Attendance").queryButton();

        interact(attendanceButton::fire);

        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(
                stage.getScene().lookup(
                    "#attendanceRosterRoot"
                ),
            "Attendance Roster should open from Dashboard"
        );
    }
}