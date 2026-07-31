import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Holds the DDL for the two tables owned by Slice 2 and creates them on a
 * supplied connection. DatabaseManager calls this at startup and the unit
 * tests call it against an in-memory database, so production and test code
 * run the exact same schema.
 *
 * @author Brent Brewington
 * @since 7/30/2026
 */
public final class CourseSchema {

    /** Utility class: no instances. */
    private CourseSchema() {
    }

    /**
     * One row per course. teacher_id points at a user whose role is TEACHER.
     * The course code is unique and case-insensitive so CST338 cannot be
     * added twice. Deleting a teacher is blocked while they still own courses.
     */
    public static final String CREATE_COURSES = """
            CREATE TABLE IF NOT EXISTS courses (
                course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_code TEXT NOT NULL UNIQUE COLLATE NOCASE,
                course_name TEXT NOT NULL,
                description TEXT DEFAULT '',
                teacher_id INTEGER NOT NULL,
                created TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (teacher_id) REFERENCES users(id)
                    ON DELETE RESTRICT
            )
            """;

    /**
     * Junction table resolving the many-to-many relationship between students
     * and courses. The UNIQUE constraint is what stops a student from being
     * enrolled in the same course twice. Deleting a course removes its
     * enrollment rows.
     */
    public static final String CREATE_ENROLLMENT = """
            CREATE TABLE IF NOT EXISTS enrollment (
                enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                enrolled_date TEXT DEFAULT (datetime('now')),
                UNIQUE (course_id, student_id),
                FOREIGN KEY (course_id) REFERENCES courses(course_id)
                    ON DELETE CASCADE,
                FOREIGN KEY (student_id) REFERENCES users(id)
                    ON DELETE CASCADE
            )
            """;

    /**
     * Creates both Slice 2 tables and turns on foreign key enforcement.
     * SQLite leaves foreign keys off by default, so the PRAGMA has to run
     * on every connection or the FOREIGN KEY clauses above do nothing.
     *
     * @param connection an open connection to the shared database
     * @throws SQLException if either table cannot be created
     */
    public static void create(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute(CREATE_COURSES);
            stmt.execute(CREATE_ENROLLMENT);
        }
    }
}
