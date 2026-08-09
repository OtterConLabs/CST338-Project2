import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Database tests for the extra-credit seat limit and the enrollment cascade,
 * run against an in-memory SQLite database (jdbc:sqlite::memory:) so they use
 * the exact same DDL the app runs. Each test gets a fresh throwaway database.
 *
 * @author Brent Brewington
 * @since 8/7/2026
 */
class EnrollmentCapacityTest {

    private Connection connection;
    private CourseDao courseDao;
    private EnrollmentDao enrollmentDao;

    private int teacherId;
    private int studentOneId;
    private int studentTwoId;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        createUsersTable();
        CourseSchema.create(connection);

        teacherId = insertUser("tsmith", "Terry", "Smith", "TEACHER");
        studentOneId = insertUser("jdoe", "Jamie", "Doe", "STUDENT");
        studentTwoId = insertUser("aray", "Alex", "Ray", "STUDENT");

        courseDao = new CourseDao(connection);
        enrollmentDao = new EnrollmentDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    @DisplayName("An empty course reports zero enrolled and is not full")
    void emptyCourseIsNotFull() {
        int courseId = courseDao.insert(
                new Course("CST338", "Software Design", "", teacherId, 1));

        assertEquals(0, enrollmentDao.countEnrolled(courseId));
        assertFalse(enrollmentDao.isFull(courseId, 1));
    }

    @Test
    @DisplayName("A course with capacity 1 becomes full after one enrollment")
    void courseBecomesFullAtCapacity() {
        int courseId = courseDao.insert(
                new Course("CST338", "Software Design", "", teacherId, 1));

        int firstResult =
                enrollmentDao.enroll(new Enrollment(courseId, studentOneId), 1);
        assertTrue(firstResult > 0, "first enrollment should succeed");
        assertEquals(1, enrollmentDao.countEnrolled(courseId));
        assertTrue(enrollmentDao.isFull(courseId, 1));
    }

    @Test
    @DisplayName("Enrolling past the seat limit returns RESULT_FULL and adds no row")
    void enrollingPastCapacityIsBlocked() {
        int courseId = courseDao.insert(
                new Course("CST338", "Software Design", "", teacherId, 1));

        enrollmentDao.enroll(new Enrollment(courseId, studentOneId), 1);
        int secondResult =
                enrollmentDao.enroll(new Enrollment(courseId, studentTwoId), 1);

        assertEquals(EnrollmentDao.RESULT_FULL, secondResult);
        assertEquals(1, enrollmentDao.countEnrolled(courseId));
    }

    @Test
    @DisplayName("A capacity of zero means unlimited, so the course never fills")
    void unlimitedCourseNeverFills() {
        int courseId = courseDao.insert(
                new Course("CST338", "Software Design", "", teacherId, Course.UNLIMITED));

        assertTrue(enrollmentDao.enroll(new Enrollment(courseId, studentOneId), Course.UNLIMITED) > 0);
        assertTrue(enrollmentDao.enroll(new Enrollment(courseId, studentTwoId), Course.UNLIMITED) > 0);
        assertFalse(enrollmentDao.isFull(courseId, Course.UNLIMITED));
        assertEquals(2, enrollmentDao.countEnrolled(courseId));
    }

    @Test
    @DisplayName("Re-enrolling the same student is rejected and does not use a seat")
    void duplicateEnrollmentIsRejected() {
        int courseId = courseDao.insert(
                new Course("CST338", "Software Design", "", teacherId, 5));

        enrollmentDao.enroll(new Enrollment(courseId, studentOneId), 5);
        int repeat = enrollmentDao.enroll(new Enrollment(courseId, studentOneId), 5);

        assertEquals(EnrollmentDao.RESULT_FAILED, repeat);
        assertEquals(1, enrollmentDao.countEnrolled(courseId));
    }

    @Test
    @DisplayName("Unenrolling frees a seat so the course is no longer full")
    void unenrollFreesASeat() {
        int courseId = courseDao.insert(
                new Course("CST338", "Software Design", "", teacherId, 1));

        enrollmentDao.enroll(new Enrollment(courseId, studentOneId), 1);
        assertTrue(enrollmentDao.isFull(courseId, 1));

        assertTrue(enrollmentDao.unenroll(courseId, studentOneId));
        assertFalse(enrollmentDao.isFull(courseId, 1));
        assertEquals(0, enrollmentDao.countEnrolled(courseId));
    }

    @Test
    @DisplayName("Deleting a course cascades and removes its enrollment rows")
    void deletingCourseCascadesEnrollment() {
        int courseId = courseDao.insert(
                new Course("CST338", "Software Design", "", teacherId, 5));
        enrollmentDao.enroll(new Enrollment(courseId, studentOneId), 5);

        assertTrue(courseDao.delete(courseId));
        assertEquals(0, enrollmentDao.countEnrolled(courseId));
    }

    // ---- helpers --------------------------------------------------------

    /** Minimal users table so the foreign keys in CourseSchema resolve. */
    private void createUsersTable() throws SQLException {
        String sql = """
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
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private int insertUser(String username, String first, String last, String role)
            throws SQLException {
        String sql = "INSERT INTO users (username, first_name, last_name, email, password, role) "
                + "VALUES ('" + username + "', '" + first + "', '" + last + "', '"
                + username + "@otter.edu', 'password1', '" + role + "')";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            try (var keys = stmt.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }
}
