import java.sql.Connection;
import java.sql.ResultSet;
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
 * @since 8/7/2026
 */
public final class CourseSchema {

    /** Utility class: no instances. */
    private CourseSchema() {
    }

    /**
     * One row per course. teacher_id points at a user whose role is TEACHER.
     * The course code is unique and case-insensitive so CST338 cannot be
     * added twice. Deleting a teacher is blocked while they still own courses.
     * capacity is the extra-credit seat limit; 0 means unlimited.
     */
    public static final String CREATE_COURSES = """
            CREATE TABLE IF NOT EXISTS courses (
                course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_code TEXT NOT NULL UNIQUE COLLATE NOCASE,
                course_name TEXT NOT NULL,
                description TEXT DEFAULT '',
                teacher_id INTEGER NOT NULL,
                capacity INTEGER NOT NULL DEFAULT 0 CHECK (capacity >= 0),
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
     * Turns on SQLite foreign key enforcement for the given connection.
     *
     * <p>SQLite leaves foreign keys OFF by default, and the setting is
     * <em>per connection</em>, not per database. If the team ever opens a
     * second connection, or a test injects its own, the FOREIGN KEY clauses
     * above silently do nothing until this runs on that connection. Exposing
     * it as its own method means any code that gets a connection can make the
     * cascade and restrict rules real again with one call.</p>
     *
     * @param connection an open database connection
     * @throws SQLException if the PRAGMA cannot be run
     */
    public static void enableForeignKeys(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    /**
     * Creates both Slice 2 tables, enables foreign keys, and adds the capacity
     * column to any pre-existing courses table that was created before the
     * extra-credit seat limit landed.
     *
     * @param connection an open connection to the shared database
     * @throws SQLException if either table cannot be created
     */
    public static void create(Connection connection) throws SQLException {
        enableForeignKeys(connection);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(CREATE_COURSES);
            stmt.execute(CREATE_ENROLLMENT);
        }
        addCapacityColumnIfMissing(connection);
    }

    /**
     * Migrates an older courses table that does not yet have the capacity
     * column. CREATE TABLE IF NOT EXISTS never alters an existing table, so a
     * database file created before this feature would otherwise be missing the
     * column and every capacity query would fail with "no such column".
     *
     * @param connection an open database connection
     * @throws SQLException if the table cannot be inspected or altered
     */
    private static void addCapacityColumnIfMissing(Connection connection) throws SQLException {
        boolean hasCapacity = false;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(courses)")) {
            while (rs.next()) {
                if ("capacity".equalsIgnoreCase(rs.getString("name"))) {
                    hasCapacity = true;
                    break;
                }
            }
        }

        if (!hasCapacity) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(
                        "ALTER TABLE courses ADD COLUMN capacity INTEGER NOT NULL DEFAULT 0");
                System.out.println("Added capacity column to existing courses table.");
            }
        }
    }
}
