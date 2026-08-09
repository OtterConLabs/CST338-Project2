import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Handles all database work for Course objects: insert, read, update, delete,
 * plus a query that filters courses by teacher.
 *
 * <p>The no-argument constructor uses the shared singleton connection so the
 * app behaves like the rest of the team's DAOs. The Connection constructor
 * lets the unit tests point the same code at an in-memory database.</p>
 *
 * @author Brent Brewington
 * @since 8/7/2026
 */
public class CourseDao {

    private final Connection connection;

    /** Uses the shared application connection from DatabaseManager. */
    public CourseDao() {
        this(DatabaseManager.getInstance().getConnection());
    }

    /**
     * Uses a caller-supplied connection, which is how the tests inject an
     * in-memory database.
     *
     * <p>Foreign keys are re-enabled here because the setting is per
     * connection in SQLite. A connection handed to this DAO by a test, or a
     * second connection opened elsewhere, would otherwise ignore the
     * ON DELETE rules that the enrollment cascade depends on.</p>
     *
     * @param connection an open database connection
     * @throws IllegalArgumentException if the connection is null
     */
    public CourseDao(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("CourseDao requires an open connection.");
        }
        this.connection = connection;
        try {
            CourseSchema.enableForeignKeys(connection);
        } catch (SQLException e) {
            System.out.println("Could not enable foreign keys: " + e.getMessage());
        }
    }

    /**
     * Inserts a new course and copies the generated key back onto the object.
     *
     * @param course the course to save
     * @return the generated course_id, or -1 if the insert failed
     */
    public int insert(Course course) {
        requireCourse(course);

        String sql = """
                INSERT INTO courses (
                    course_code,
                    course_name,
                    description,
                    teacher_id,
                    capacity
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getTeacherId());
            pstmt.setInt(5, course.getCapacity());

            if (pstmt.executeUpdate() != 1) {
                return -1;
            }

            // Ask the database for the primary key it just generated.
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    course.setCourseId(generatedId);
                    return generatedId;
                }
            }
            return -1;
        } catch (SQLException e) {
            // A duplicate course code lands here because of the UNIQUE constraint.
            System.out.println("insert failed: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Finds one course by its primary key.
     *
     * @param courseId the course_id to look up
     * @return the matching course, or an empty Optional if it does not exist
     */
    public Optional<Course> findById(int courseId) {
        if (courseId <= 0) {
            return Optional.empty();
        }

        String sql = SELECT_WITH_TEACHER + " WHERE c.course_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("findById failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Returns every course, ordered by course code, with the teacher's name
     * already joined in for the Teacher column of the TableView.
     *
     * @return all courses, or an empty list if the table is empty
     */
    public List<Course> findAll() {
        String sql = SELECT_WITH_TEACHER + " ORDER BY c.course_code";
        List<Course> courses = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapCourse(rs));
            }
        } catch (SQLException e) {
            System.out.println("findAll failed: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Query/filter method required by the slice: returns only the courses
     * owned by one teacher.
     *
     * @param teacherId users.id of the teacher
     * @return that teacher's courses, or an empty list
     * @throws IllegalArgumentException if the teacher ID is zero or negative
     */
    public List<Course> findByTeacherId(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException("Teacher ID must be greater than zero.");
        }

        String sql = SELECT_WITH_TEACHER + " WHERE c.teacher_id = ? ORDER BY c.course_code";
        List<Course> courses = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapCourse(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("findByTeacherId failed: " + e.getMessage());
        }
        return courses;
    }

    /**
     * Saves edits to an existing course.
     *
     * @param course the course carrying the updated values and a real course_id
     * @return true if exactly one row was updated
     */
    public boolean update(Course course) {
        requireCourse(course);

        if (course.getCourseId() <= 0) {
            throw new IllegalArgumentException("Cannot update a course that has no ID.");
        }

        String sql = """
                UPDATE courses
                SET course_code = ?,
                    course_name = ?,
                    description = ?,
                    teacher_id = ?,
                    capacity = ?
                WHERE course_id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getDescription());
            pstmt.setInt(4, course.getTeacherId());
            pstmt.setInt(5, course.getCapacity());
            pstmt.setInt(6, course.getCourseId());

            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("update failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a course. The enrollment rows for that course are removed by the
     * ON DELETE CASCADE rule in the schema.
     *
     * @param courseId the course_id to delete
     * @return true if exactly one row was deleted
     */
    public boolean delete(int courseId) {
        if (courseId <= 0) {
            return false;
        }

        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("delete failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Shared SELECT that joins the teacher's name onto every course row.
     * A LEFT JOIN is used so a course still shows up if its teacher row is
     * missing for any reason.
     */
    private static final String SELECT_WITH_TEACHER = """
            SELECT c.course_id,
                   c.course_code,
                   c.course_name,
                   c.description,
                   c.teacher_id,
                   c.capacity,
                   c.created,
                   u.first_name,
                   u.last_name
            FROM courses c
            LEFT JOIN users u ON u.id = c.teacher_id""";

    /**
     * Converts the current result set row into a Course object.
     *
     * @param rs a result set positioned on a valid row
     * @return the mapped course
     * @throws SQLException if a column cannot be read
     */
    private Course mapCourse(ResultSet rs) throws SQLException {
        Course course = new Course(
                rs.getInt("course_id"),
                rs.getString("course_code"),
                rs.getString("course_name"),
                rs.getString("description"),
                rs.getInt("teacher_id"),
                rs.getInt("capacity"),
                rs.getString("created")
        );

        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        if (firstName != null && lastName != null) {
            course.setTeacherName(firstName + " " + lastName);
        }
        return course;
    }

    /**
     * Guards the write methods against a null argument.
     *
     * @param course the course being saved
     * @throws IllegalArgumentException if the course is null
     */
    private void requireCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null.");
        }
    }
}
