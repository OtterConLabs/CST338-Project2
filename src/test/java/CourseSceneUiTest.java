import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

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
 * [CST338 Project2 - Slice 2: Courses & Enrollment]
 * TestFX UI tests for the Enrollment scene.
 *
 * <p>The scene is loaded with a DAO pointed at an in-memory database, injected
 * through a controller factory. Nothing here touches app.db, so the tests do
 * not depend on the order they run in or on any leftover application state.</p>
 *
 * <p>The seed data is built inside {@link #start(Stage)} rather than in a
 * {@code @BeforeEach} method. ApplicationExtension is a JUnit extension, and
 * extension callbacks run before the test class's own lifecycle methods, so a
 * {@code @BeforeEach} would not finish until after the scene had already been
 * loaded with null dependencies.</p>
 *
 * <p>Run headless with:
 * {@code ./gradlew test -Dtestfx.headless=true -Dglass.platform=Monocle}</p>
 *
 * @author Brent Brewington
 * @since 8/6/2026
 */
@ExtendWith(ApplicationExtension.class)
class CourseSceneUiTest {

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
    private EnrollmentDao enrollmentDao;
    private Course course;
    private Stage stage;

    /**
     * Loads the Enrollment scene onto the test stage, injecting a controller
     * that already holds the in-memory DAO.
     *
     * <p>The database is seeded first, in this same method, so the controller
     * factory has a real DAO and Course to hand to the controller before
     * FXMLLoader calls initialize().</p>
     *
     * @param stage the stage supplied by TestFX
     * @throws Exception if the test database or the FXML file cannot be loaded
     */
    @Start
    void start(Stage stage) throws Exception {
        this.stage = stage;

        // Must run before load() so setDependencies() gets non-null values.
        setUpDatabase();

        FXMLLoader loader = new FXMLLoader(
                CourseSceneUiTest.class.getResource("/EnrollmentScene.fxml")
        );

        loader.setControllerFactory(type -> {
            EnrollmentController controller = new EnrollmentController();
            controller.setStage(stage);
            controller.setDependencies(enrollmentDao, course);
            return controller;
        });

        Parent root = loader.load();
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    /**
     * Builds the in-memory test database and seeds one teacher, two students,
     * and one course. Called directly from start() rather than annotated with
     * @BeforeEach so that it is guaranteed to run before the scene loads.
     *
     * @throws SQLException if the test database cannot be created
     */
    private void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_USERS);
        }
        CourseSchema.create(connection);

        int teacherId = insertUser("mlarkin", "Morgan", "Larkin", "TEACHER");
        insertUser("asinclair", "Ava", "Sinclair", "STUDENT");
        insertUser("mbellamy", "Marcus", "Bellamy", "STUDENT");

        CourseDao courseDao = new CourseDao(connection);
        course = new Course("CST338", "Software Design", "", teacherId);
        courseDao.insert(course);

        enrollmentDao = new EnrollmentDao(connection);
    }

    /**
     * Closes the test database.
     *
     * @throws SQLException if the connection cannot be closed
     */
    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @DisplayName("UI: the scene opens showing both students available and none enrolled")
    void enrollmentScene_loadsRosterForSelectedCourse(FxRobot robot) {
        ListView<User> available = availableList(robot);
        ListView<User> enrolled = enrolledList(robot);

        assertNotNull(available, "The available list should be on the scene");
        assertEquals(2, available.getItems().size(),
                "Both seeded students start available");
        assertEquals(0, enrolled.getItems().size(), "Nobody is enrolled yet");

        verifyThat("#courseLabel",
                hasText("Enrollment for CST338 - Software Design"));
    }

    @Test
    @DisplayName("UI: enrolling a student moves them from the left list to the right list")
    void clickingEnroll_movesStudentBetweenLists(FxRobot robot) {
        ListView<User> available = availableList(robot);
        ListView<User> enrolled = enrolledList(robot);

        // Select the first available student, then transfer them.
        robot.interact(() -> available.getSelectionModel().select(0));
        robot.clickOn("#enrollButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, available.getItems().size(),
                "One student should have moved out");
        assertEquals(1, enrolled.getItems().size(),
                "One student should have moved in");
        assertTrue(enrollmentDao.isEnrolled(course.getCourseId(),
                        enrolled.getItems().get(0).getId()),
                "The transfer should be persisted, not just visual");
    }

    @Test
    @DisplayName("UI: the Enroll button stays disabled until a student is selected")
    void enrollButton_isDisabledWithoutSelection(FxRobot robot) {
        assertTrue(robot.lookup("#enrollButton").queryButton().isDisabled(),
                "Enroll should be disabled with nothing selected");

        ListView<User> available = availableList(robot);
        robot.interact(() -> available.getSelectionModel().select(0));
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(!robot.lookup("#enrollButton").queryButton().isDisabled(),
                "Selecting a student should enable Enroll");
    }

    @Test
    @DisplayName("UI: unenrolling returns the student to the available list")
    void clickingUnenroll_returnsStudentToAvailable(FxRobot robot) {
        ListView<User> available = availableList(robot);
        ListView<User> enrolled = enrolledList(robot);

        robot.interact(() -> available.getSelectionModel().select(0));
        robot.clickOn("#enrollButton");
        WaitForAsyncUtils.waitForFxEvents();

        robot.interact(() -> enrolled.getSelectionModel().select(0));
        robot.clickOn("#unenrollButton");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(2, available.getItems().size(),
                "The student should be back on the left");
        assertEquals(0, enrolled.getItems().size(),
                "The enrolled list should be empty again");
    }

    @Test
    @DisplayName("UI: the scene transitions back to the Course List")
    void clickingBack_transitionsToCourseListScene(FxRobot robot) {
        Parent enrollmentRoot = stage.getScene().getRoot();

        robot.clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();

        Parent newRoot = stage.getScene().getRoot();
        assertTrue(newRoot != enrollmentRoot,
                "The stage should be showing a different scene");
        assertNotNull(robot.lookup("#courseTable").tryQuery().orElse(null),
                "The Course List scene should now be displayed");
    }

    @Test
    @DisplayName("UI: the message label reports the completed transfer")
    void enrollingAStudent_showsConfirmationMessage(FxRobot robot) {
        ListView<User> available = availableList(robot);

        robot.interact(() -> available.getSelectionModel().select(0));
        robot.clickOn("#enrollButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label message = robot.lookup("#enrollmentMessageLabel")
                .queryAs(Label.class);
        assertTrue(message.getText().startsWith("Enrolled "),
                "The user should get inline feedback, got: " + message.getText());
    }

    /**
     * Looks up the left-hand available-students list.
     *
     * @param robot the TestFX robot for the current test
     * @return the available students ListView
     */
    @SuppressWarnings("unchecked")
    private ListView<User> availableList(FxRobot robot) {
        return robot.lookup("#availableList").queryAs(ListView.class);
    }

    /**
     * Looks up the right-hand enrolled-students list.
     *
     * @param robot the TestFX robot for the current test
     * @return the enrolled students ListView
     */
    @SuppressWarnings("unchecked")
    private ListView<User> enrolledList(FxRobot robot) {
        return robot.lookup("#enrolledList").queryAs(ListView.class);
    }

    /**
     * Inserts a seed user.
     *
     * @param username  the username to store
     * @param firstName the first name to store
     * @param lastName  the last name to store
     * @param role      STUDENT or TEACHER
     * @return the generated users.id
     * @throws SQLException if the seed row cannot be inserted
     */
    private int insertUser(String username, String firstName, String lastName,
                           String role) throws SQLException {
        String sql = """
                INSERT INTO users
                    (username, first_name, last_name, email, password, role)
                VALUES (?, ?, ?, ?, 'pass123', ?)
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
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
