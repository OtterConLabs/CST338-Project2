import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Tests for the enrollment junction table: enroll, unenroll, the available and
 * enrolled student lists, and the duplicate-enrollment rule from Use Case 2.
 *
 * @author Brent Brewington
 * @since 7/30/2026
 */
class EnrollmentDaoTest {

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
    private EnrollmentDao enrollmentDao;

    private int courseId;
    private int avaId;
    private int marcusId;

    /**
     * Builds a fresh in-memory database with one course and two students.
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

        courseDao = new CourseDao(connection);
        enrollmentDao = new EnrollmentDao(connection);

        int teacherId = insertUser("mlarkin", "Morgan", "Larkin", "TEACHER");
        avaId = insertUser("asinclair", "Ava", "Sinclair", "STUDENT");
        marcusId = insertUser("mbellamy", "Marcus", "Bellamy", "STUDENT");

        courseId = courseDao.insert(new Course("CST338", "Software Design", "", teacherId));
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
    @DisplayName("Enrolling a student moves them into the enrolled list")
    void enroll_addsStudentToCourse() {
        int enrollmentId = enrollmentDao.enroll(new Enrollment(courseId, avaId));

        List<User> enrolled = enrollmentDao.findEnrolledStudents(courseId);

        assertTrue(enrollmentId > 0, "Enroll should return a real primary key");
        assertEquals(1, enrolled.size());
        assertEquals("Ava", enrolled.get(0).getFirstName());
        assertTrue(enrollmentDao.isEnrolled(courseId, avaId));
    }

    @Test
    @DisplayName("Negative case: the same student cannot be enrolled twice")
    void enroll_duplicateStudent_isRejected() {
        enrollmentDao.enroll(new Enrollment(courseId, avaId));

        int secondAttempt = enrollmentDao.enroll(new Enrollment(courseId, avaId));

        assertEquals(-1, secondAttempt, "The duplicate should be refused");
        assertEquals(1, enrollmentDao.findByCourseId(courseId).size(),
                "Only one enrollment row should exist");
    }

    @Test
    @DisplayName("Unenrolling removes the student from the course")
    void unenroll_removesStudent() {
        enrollmentDao.enroll(new Enrollment(courseId, avaId));

        boolean removed = enrollmentDao.unenroll(courseId, avaId);

        assertTrue(removed);
        assertFalse(enrollmentDao.isEnrolled(courseId, avaId));
        assertTrue(enrollmentDao.findEnrolledStudents(courseId).isEmpty());
    }

    @Test
    @DisplayName("The available list excludes students who are already enrolled")
    void findAvailableStudents_excludesEnrolledStudents() {
        enrollmentDao.enroll(new Enrollment(courseId, avaId));

        List<User> available = enrollmentDao.findAvailableStudents(courseId);

        assertEquals(1, available.size(), "Only Marcus should still be available");
        assertEquals(marcusId, available.get(0).getId());
    }

    @Test
    @DisplayName("Deleting a course also removes its enrollment rows")
    void deleteCourse_cascadesToEnrollment() {
        enrollmentDao.enroll(new Enrollment(courseId, avaId));
        enrollmentDao.enroll(new Enrollment(courseId, marcusId));

        assertTrue(courseDao.delete(courseId));

        assertTrue(enrollmentDao.findByCourseId(courseId).isEmpty(),
                "ON DELETE CASCADE should clean up the junction table");
    }

    @Test
    @DisplayName("Negative case: unenrolling a student who was never enrolled returns false")
    void unenroll_studentNotEnrolled_returnsFalse() {
        assertFalse(enrollmentDao.unenroll(courseId, marcusId));
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
    private int insertUser(String username, String firstName, String lastName, String role)
            throws SQLException {
        String sql = """
                INSERT INTO users
                (username, first_name, last_name, email, password, role)
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
