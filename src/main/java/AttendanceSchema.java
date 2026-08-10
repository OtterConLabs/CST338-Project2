import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Attendance Schema
 *
 * @author Jit Tran
 * @since 08/10/2026
 */
public final class AttendanceSchema {
    private AttendanceSchema() {
    }

    public static final String CREATE_ATTENDANCE = 
            """
            CREATE TABLE IF NOT EXISTS attendance (
                attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                attendance_date TEXT NOT NULL,
                status TEXT NOT NULL
                    CHECK (
                        status IN (
                            'PRESENT',
                            'ABSENT',
                            'LATE',
                            'EXCUSED'
                        )
                    ),
                notes TEXT DEFAULT '',
                recorded_by INTEGER NOT NULL,
                updated_at TEXT NOT NULL
                    DEFAULT (datetime('now')),
                UNIQUE (
                    course_id,
                    student_id,
                    attendance_date
                ),
                FOREIGN KEY (course_id)
                    REFERENCES courses(course_id)
                    ON DELETE CASCADE,
                FOREIGN KEY (student_id)
                    REFERENCES users(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (recorded_by)
                    REFERENCES users(id)
                    ON DELETE RESTRICT
            )
            """;

    public static void create(Connection connection)
            throws SQLException{
                try (Statement statement = connection.createStatement()){
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute(CREATE_ATTENDANCE);
        }
    }
}