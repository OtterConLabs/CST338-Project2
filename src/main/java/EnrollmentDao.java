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
 * student, unenroll a student, count and list the enrolled and available
 * students for a course, and enforce the extra-credit seat limit.
 *
 * @author Brent Brewington
 * @since 8/7/2026
 */
public class EnrollmentDao {

    /** Returned by enroll() when the course is already at its seat limit. */
    public static final int RESULT_FULL = -2;

    /** Returned by enroll() when the student is already enrolled, or on failure. */
    public static final int RESULT_FAILED = -1;

    private final Connection connection;

    /** Uses the shared application connection from DatabaseManager. */
    public EnrollmentDao() {
        this(DatabaseManager.getInstance().getConnection());
    }

    /**
     * Uses a caller-supplied connection so the tests can inject an in-memory
     * database. Foreign keys are re-enabled here because the setting is per
     * connection in SQLite, and the delete cascade relies on it.
     *
     * @param connection an open database connection
     * @throws IllegalArgumentException if the connection is null
     */
    public EnrollmentDao(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("EnrollmentDao requires an open connection.");
        }
        this.connection = connection;
        try {
            CourseSchema.enableForeignKeys(connection);
        } catch (SQLException e) {
            System.out.println("Could not enable foreign keys: " + e.getMessage());
        }
    }

    /**
     * Enrolls a student in a course with no seat limit. Duplicate enrollments
     * are rejected before the insert runs, and the UNIQUE (course_id,
     * student_id) constraint backs that rule up at the database level.
     *
     * @param enrollment the course/student pair to save
     * @return the generated enrollment_id, or {@link #RESULT_FAILED} if the
     *         student was already enrolled or the insert failed
     */
    public int enroll(Enrollment enrollment) {
        return enroll(enrollment, Course.UNLIMITED);
    }

    /**
     * Enrolls a student in a course, honoring the course seat limit. The order
     * of the guards matters: duplicates are rejected first so a repeat click
     * never counts against the limit, then the seat limit is checked, and only
     * then is the insert attempted.
     *
     * @param enrollment the course/student pair to save
     * @param capacity   the course seat limit, or {@link Course#UNLIMITED} (0)
     *                   for no limit
     * @return the generated enrollment_id on success,
     *         {@link #RESULT_FULL} if the course is at capacity,
     *         or {@link #RESULT_FAILED} if the student was already enrolled or
     *         the insert failed
     */
    public int enroll(Enrollment enrollment, int capacity) {
        if (enrollment == null) {
            throw new IllegalArgumentException("Enrollment cannot be null.");
        }

        if (isEnrolled(enrollment.getCourseId(), enrollment.getStudentId())) {
            System.out.println("Student is already enrolled in this course.");
            return RESULT_FAILED;
        }

        if (isFull(enrollment.getCourseId(), capacity)) {
            System.out.println("Course is at its seat limit.");
            return RESULT_FULL;
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
                return RESULT_FAILED;
            }

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    enrollment.setEnrollmentId(generatedId);
                    return generatedId;
                }
            }
            return RESULT_FAILED;
        } catch (SQLException e) {
            System.out.println("enroll failed: " + e.getMessage());
            return RESULT_FAILED;
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
     * Counts how many students are enrolled in a course. Used to enforce the
     * extra-credit seat limit and to show seat usage in the UI.
     *
     * @param courseId the course to count
     * @return the number of enrolled students, or 0 for an invalid course id
     */
    public int countEnrolled(int courseId) {
        if (courseId <= 0) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM enrollment WHERE course_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("countEnrolled failed: " + e.getMessage());
        }
        return 0;
    }

    /**
     * True when a course has reached its seat limit. A capacity of zero means
     * the course is unlimited and is therefore never full.
     *
     * @param courseId the course to check
     * @param capacity the course seat limit, or {@link Course#UNLIMITED} (0)
     * @return true if no more students can be enrolled
     */
    public boolean isFull(int courseId, int capacity) {
        return CourseValidator.isFull(countEnrolled(courseId), capacity);
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
