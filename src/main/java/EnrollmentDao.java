import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Handles all database work for the enrollment junction table: enroll a
 * student, unenroll a student, and list the enrolled and available students
 * for a course.
 *
 * @author Brent Brewington
 * @since 7/30/2026
 */
public class EnrollmentDao {

    private final Connection connection;

    /** Uses the shared application connection from DatabaseManager. */
    public EnrollmentDao() {
        this(DatabaseManager.getInstance().getConnection());
    }

    /**
     * Uses a caller-supplied connection so the tests can inject an in-memory
     * database.
     *
     * @param connection an open database connection
     * @throws IllegalArgumentException if the connection is null
     */
    public EnrollmentDao(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("EnrollmentDao requires an open connection.");
        }
        this.connection = connection;
    }

    /**
     * Enrolls a student in a course. Duplicate enrollments are rejected before
     * the insert runs, and the UNIQUE (course_id, student_id) constraint backs
     * that rule up at the database level.
     *
     * @param enrollment the course/student pair to save
     * @return the generated enrollment_id, or -1 if the student was already
     *         enrolled or the insert failed
     */
    public int enroll(Enrollment enrollment) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null.");
        }

        if (isEnrolled(enrollment.getCourseId(), enrollment.getStudentId())) {
            System.out.println("Student is already enrolled in this course.");
            return -1;
        }

        String sql = """
                INSERT INTO enrollment (course_id, student_id)
                VALUES (?, ?)
                """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, enrollment.getCourseId());
            pstmt.setInt(2, enrollment.getStudentId());

            if (pstmt.executeUpdate() != 1) {
                return -1;
            }

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    enrollment.setEnrollmentId(generatedId);
                    return generatedId;
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("enroll failed: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Removes a student from a course.
     *
     * @param courseId  the course to remove the student from
     * @param studentId the student being removed
     * @return true if exactly one enrollment row was deleted
     */
    public boolean unenroll(int courseId, int studentId) {
        if (courseId <= 0 || studentId <= 0) {
            return false;
        }

        String sql = "DELETE FROM enrollment WHERE course_id = ? AND student_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, studentId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("unenroll failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reports whether a student is already enrolled in a course. This is the
     * rule that the duplicate-enrollment alternate flow depends on.
     *
     * @param courseId  the course to check
     * @param studentId the student to check
     * @return true if an enrollment row already exists
     */
    public boolean isEnrolled(int courseId, int studentId) {
        if (courseId <= 0 || studentId <= 0) {
            return false;
        }

        String sql = """
                SELECT 1
                FROM enrollment
                WHERE course_id = ? AND student_id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("isEnrolled failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns the raw enrollment rows for one course.
     *
     * @param courseId the course to look up
     * @return the enrollment records for that course
     */
    public List<Enrollment> findByCourseId(int courseId) {
        List<Enrollment> enrollments = new ArrayList<>();
        if (courseId <= 0) {
            return enrollments;
        }

        String sql = """
                SELECT enrollment_id, course_id, student_id, enrolled_date
                FROM enrollment
                WHERE course_id = ?
                ORDER BY enrollment_id
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("course_id"),
                            rs.getInt("student_id"),
                            rs.getString("enrolled_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("findByCourseId failed: " + e.getMessage());
        }
        return enrollments;
    }

    /**
     * Returns the students enrolled in a course, for the right-hand ListView
     * of the Enrollment scene.
     *
     * @param courseId the course to look up
     * @return the enrolled students
     */
    public List<User> findEnrolledStudents(int courseId) {
        String sql = """
                SELECT u.id, u.username, u.first_name, u.last_name,
                       u.email, u.password, u.role, u.created
                FROM users u
                JOIN enrollment e ON e.student_id = u.id
                WHERE e.course_id = ?
                ORDER BY u.last_name, u.first_name
                """;
        return queryStudents(sql, courseId);
    }

    /**
     * Returns the students who are not yet enrolled in a course, for the
     * left-hand ListView of the Enrollment scene.
     *
     * @param courseId the course to look up
     * @return the students still available to enroll
     */
    public List<User> findAvailableStudents(int courseId) {
        String sql = """
                SELECT u.id, u.username, u.first_name, u.last_name,
                       u.email, u.password, u.role, u.created
                FROM users u
                WHERE u.role = 'STUDENT'
                  AND u.id NOT IN (
                      SELECT e.student_id
                      FROM enrollment e
                      WHERE e.course_id = ?
                  )
                ORDER BY u.last_name, u.first_name
                """;
        return queryStudents(sql, courseId);
    }

    /**
     * Runs a student query that takes a single course_id parameter and maps
     * each row to a User.
     *
     * @param sql      the query to run
     * @param courseId the course_id placeholder value
     * @return the matching users
     */
    private List<User> queryStudents(String sql, int courseId) {
        List<User> students = new ArrayList<>();
        if (courseId <= 0) {
            return students;
        }

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            UserRole.valueOf(rs.getString("role")),
                            rs.getString("created")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("queryStudents failed: " + e.getMessage());
        }
        return students;
    }
}
