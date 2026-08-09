import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * TestFX UI tests for the two enhancements that the original CourseSceneUiTest
 * does not cover: the live search/filter box on the Course List, and the
 * extra-credit seat limit on the Enrollment screen. Kept in a separate class so
 * the existing UI test is left untouched.
 *
 * <p>Both scenes are loaded with DAOs pointed at a fresh in-memory database,
 * injected through a controller factory, so nothing here touches app.db. The
 * seed data is built inside {@link #start(Stage)} for the same reason the
 * original test documents: extension callbacks run before the class's own
 * {@code @BeforeEach} would.</p>
 *
 * <p>Run headless with:
 * {@code ./gradlew test -Dtestfx.headless=true -Dglass.platform=Monocle}</p>
 *
 * @author Brent Brewington
 * @since 8/7/2026
 */
@ExtendWith(ApplicationExtension.class)
class CourseCapacityUiTest {

    private static final String CREATE_USERS = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE COLLATE NOCASE,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE COLLATE NOCASE,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK (role IN ('STUDENT', 'TEACHER')),
                created TEXT DEFAULT (datetime('now'))
            )
            """;

    private Connection connection;
    private Stage stage;

    private int teacherId;

    // The capacity-1 course, seeded already full for the capacity test.
    private Course fullCourse;

    /**
     * Prepares the stage and seeds the in-memory database with one teacher,
     * two students, and two courses: an unlimited CST338 and a capacity-1
     * CST499 that already holds one student (so it starts full).
     *
     * @param stage the stage supplied by TestFX
     * @throws SQLException if the test database cannot be created
     */
    @Start
    void start(Stage stage) throws SQLException {
        this.stage = stage;
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_USERS);
        }
        CourseSchema.create(connection);

        teacherId = insertUser("mlarkin", "Morgan", "Larkin", "TEACHER");
        int studentOneId = insertUser("asinclair", "Ava", "Sinclair", "STUDENT");
        insertUser("mbellamy", "Marcus", "Bellamy", "STUDENT");

        CourseDao courseDao = new CourseDao(connection);
        // Unlimited course for the search test.
        courseDao.insert(new Course("CST338", "Software Design", "", teacherId));
        // Capacity-1 course for the seat-limit test; fill it so it starts full.
        fullCourse = new Course("CST499", "Capstone", "", teacherId, 1);
        courseDao.insert(fullCourse);
        new EnrollmentDao(connection)
                .enroll(new Enrollment(fullCourse.getCourseId(), studentOneId), 1);

        // The Course List scopes its table to the logged-in teacher.
        SceneFactory.setLoggedInUser(new User(teacherId, "mlarkin", "Morgan", "Larkin",
                "mlarkin@otterconlabs.edu", "pass123", UserRole.TEACHER, null));
        stage.show();
    }

    /**
     * Clears the shared logged-in user and closes the test database.
     *
     * @throws SQLException if the connection cannot be closed
     */
    @AfterEach
    void tearDown() throws SQLException {
        SceneFactory.setLoggedInUser(null);
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // ---- Course List: search / filter enhancement -----------------------

    @Test
    @DisplayName("UI: the search box filters the Course List live")
    void searchBoxFiltersCourseList(FxRobot robot) {
        loadCourseListScene(robot);

        TableView<?> table = robot.lookup("#courseTable").queryAs(TableView.class);
        TextField search = robot.lookup("#searchField").queryAs(TextField.class);

        assertEquals(2, table.getItems().size(), "Both seeded courses start visible");

        robot.clickOn(search).write("capstone");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1, table.getItems().size(), "Only CST499 matches 'capstone'");

        robot.interact(search::clear);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, table.getItems().size(), "Clearing the search restores both rows");
    }

    // ---- Enrollment: extra-credit seat limit ----------------------------

    @Test
    @DisplayName("UI: enrolling into a full course is blocked with a 'full' message")
    void enrollingIntoFullCourseIsBlocked(FxRobot robot) {
        loadEnrollmentScene(robot, fullCourse);

        ListView<User> available = robot.lookup("#availableList").queryAs(ListView.class);
        ListView<User> enrolled = robot.lookup("#enrolledList").queryAs(ListView.class);
        Label message = robot.lookup("#enrollmentMessageLabel").queryAs(Label.class);

        // One seat, already taken: one student enrolled, one still available.
        assertEquals(1, enrolled.getItems().size());
        assertEquals(1, available.getItems().size());

        robot.interact(() -> available.getSelectionModel().select(0));
        robot.clickOn("#enrollButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(message.getText().toLowerCase().contains("full"),
                "Expected a 'full' message, got: " + message.getText());
        assertEquals(1, enrolled.getItems().size(), "No extra student should be enrolled");
    }

    // ---- scene loaders --------------------------------------------------

    private void loadCourseListScene(FxRobot robot) {
        robot.interact(() -> {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/CourseListScene.fxml"));
                loader.setControllerFactory(type -> {
                    CourseListController controller = new CourseListController();
                    controller.setStage(stage);
                    controller.setCourseDao(new CourseDao(connection));
                    return controller;
                });
                Parent root = loader.load();
                stage.setScene(new Scene(root, 700, 500));
                stage.show();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load CourseListScene.fxml", e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void loadEnrollmentScene(FxRobot robot, Course course) {
        robot.interact(() -> {
            try {
                FXMLLoader loader =
                        new FXMLLoader(getClass().getResource("/EnrollmentScene.fxml"));
                loader.setControllerFactory(type -> {
                    EnrollmentController controller = new EnrollmentController();
                    controller.setStage(stage);
                    controller.setDependencies(new EnrollmentDao(connection), course);
                    return controller;
                });
                Parent root = loader.load();
                stage.setScene(new Scene(root, 700, 500));
                stage.show();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load EnrollmentScene.fxml", e);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    // ---- database helper ------------------------------------------------

    private int insertUser(String username, String firstName, String lastName, String role)
            throws SQLException {
        String sql = """
                INSERT INTO users (username, first_name, last_name, email, password, role)
                VALUES (?, ?, ?, ?, 'pass123', ?)
                """;
        try (PreparedStatement pstmt =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, username + "@otterconlabs.edu");
            pstmt.setString(5, role);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }
}
