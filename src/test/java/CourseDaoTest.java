import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * CRUD tests for CourseDao.
 *
 * <p>These run against an in-memory SQLite database rather than H2 (the Part 01
 * plan said H2) because the shared schema uses SQLite-only syntax such as
 * AUTOINCREMENT and datetime('now'). Testing on SQLite means the tests exercise
 * the same DDL the application runs. Each test gets a brand new database, so the
 * tests can run in any order.</p>
 *
 * @author Brent Brewington
 * @since 7/30/2026
 */
class CourseDaoTest {

    /**
     * Copy of the users DDL from DatabaseManager. Slice 1 owns that table, so
     * this test only needs enough of it to satisfy the courses foreign key.
     */
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
    private CourseDao courseDao;

    private int larkinId;
    private int hallowayId;

    /**
     * Builds a fresh in-memory database with the users and Slice 2 tables and
     * seeds two teachers.
     *
     * @throws SQLException if the test database cannot be created
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_USERS);
        }
        CourseSchema.create(connection);

        larkinId = insertTeacher("mlarkin", "Morgan", "Larkin");
        hallowayId = insertTeacher("thalloway", "Tess", "Halloway");

        courseDao = new CourseDao(connection);
    }

    /**
     * Closes the test database so nothing leaks between tests.
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
    @DisplayName("Insert saves the course and returns the generated ID")
    void insert_savesCourseAndReturnsGeneratedId() {
        Course course = new Course("CST338", "Software Design", "Design patterns", larkinId);

        int generatedId = courseDao.insert(course);

        assertTrue(generatedId > 0, "Insert should return a real primary key");
        assertEquals(generatedId, course.getCourseId(),
                "The generated key should be copied back onto the object");
    }

    @Test
    @DisplayName("Read returns the values that were saved, including the teacher name")
    void findById_returnsSavedValues() {
        Course course = new Course("CST338", "Software Design", "Design patterns", larkinId);
        int id = courseDao.insert(course);

        Optional<Course> found = courseDao.findById(id);

        assertTrue(found.isPresent(), "The saved course should be found");
        assertEquals("CST338", found.get().getCourseCode());
        assertEquals("Software Design", found.get().getCourseName());
        assertEquals(larkinId, found.get().getTeacherId());
        assertEquals("Morgan Larkin", found.get().getTeacherName(),
                "findById joins the teacher name for the Teacher column");
    }

    @Test
    @DisplayName("Update changes the stored values")
    void update_changesStoredValues() {
        Course course = new Course("CST338", "Software Design", "", larkinId);
        int id = courseDao.insert(course);

        course.setCourseName("Software Design and Patterns");
        course.setTeacherId(hallowayId);
        boolean updated = courseDao.update(course);

        Course reloaded = courseDao.findById(id).orElseThrow();
        assertTrue(updated, "One row should be updated");
        assertEquals("Software Design and Patterns", reloaded.getCourseName());
        assertEquals(hallowayId, reloaded.getTeacherId());
    }

    @Test
    @DisplayName("Delete removes the course from the database")
    void delete_removesCourse() {
        int id = courseDao.insert(new Course("CST438", "Software Engineering", "", larkinId));

        boolean deleted = courseDao.delete(id);

        assertTrue(deleted, "One row should be deleted");
        assertTrue(courseDao.findById(id).isEmpty(), "The course should be gone");
    }

    @Test
    @DisplayName("Insert, read, update, and delete work end to end on one course")
    void crud_roundTripWorksEndToEnd() {
        Course course = new Course("CST334", "Operating Systems", "Processes", larkinId);

        int id = courseDao.insert(course);
        assertTrue(id > 0);

        assertTrue(courseDao.findById(id).isPresent());

        course.setCourseName("Operating Systems Internals");
        assertTrue(courseDao.update(course));
        assertEquals("Operating Systems Internals",
                courseDao.findById(id).orElseThrow().getCourseName());

        assertTrue(courseDao.delete(id));
        assertTrue(courseDao.findById(id).isEmpty());
        assertTrue(courseDao.findAll().isEmpty());
    }

    @Test
    @DisplayName("findByTeacherId returns only the courses owned by that teacher")
    void findByTeacherId_filtersByOwner() {
        courseDao.insert(new Course("CST338", "Software Design", "", larkinId));
        courseDao.insert(new Course("CST334", "Operating Systems", "", larkinId));
        courseDao.insert(new Course("CST438", "Software Engineering", "", hallowayId));

        List<Course> larkinCourses = courseDao.findByTeacherId(larkinId);

        assertEquals(2, larkinCourses.size());
        assertTrue(larkinCourses.stream().allMatch(c -> c.getTeacherId() == larkinId));
    }

    @Test
    @DisplayName("findAll returns courses ordered by course code")
    void findAll_returnsCoursesInCodeOrder() {
        courseDao.insert(new Course("CST438", "Software Engineering", "", hallowayId));
        courseDao.insert(new Course("CST334", "Operating Systems", "", larkinId));

        List<Course> all = courseDao.findAll();

        assertEquals(2, all.size());
        assertEquals("CST334", all.get(0).getCourseCode());
        assertEquals("CST438", all.get(1).getCourseCode());
    }

    @Test
    @DisplayName("Negative case: a duplicate course code is rejected")
    void insert_duplicateCourseCode_isRejected() {
        courseDao.insert(new Course("CST338", "Software Design", "", larkinId));

        int secondId = courseDao.insert(new Course("cst338", "Duplicate Code", "", hallowayId));

        assertEquals(-1, secondId, "The UNIQUE COLLATE NOCASE constraint should block this");
        assertEquals(1, courseDao.findAll().size());
    }

    @Test
    @DisplayName("Negative case: a course with no course code never reaches the database")
    void newCourse_blankCode_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Course("", "Software Design", "", larkinId));
    }

    @Test
    @DisplayName("Negative case: findByTeacherId rejects an invalid teacher ID")
    void findByTeacherId_invalidId_throws() {
        assertThrows(IllegalArgumentException.class, () -> courseDao.findByTeacherId(0));
    }

    @Test
    @DisplayName("Negative case: deleting a course that does not exist returns false")
    void delete_missingCourse_returnsFalse() {
        assertFalse(courseDao.delete(9999));
    }

    /**
     * Inserts a teacher so courses have a valid teacher_id to point at.
     *
     * @param username  the teacher's username
     * @param firstName the teacher's first name
     * @param lastName  the teacher's last name
     * @return the generated users.id
     * @throws SQLException if the seed row cannot be inserted
     */
    private int insertTeacher(String username, String firstName, String lastName)
            throws SQLException {
        String sql = """
                INSERT INTO users
                (username, first_name, last_name, email, password, role)
                VALUES (?, ?, ?, ?, 'pass123', 'TEACHER')
                """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, username + "@otterconlabs.edu");
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }
}
