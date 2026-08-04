import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * OtterconLabs Project 2 - Slice 4: Grades & Statistics
 * 
 * @author Jit Tran
 * @since 08/04/2026
 */
public final class GradeSchema {
    private GradeSchema(){
        //no instances
    }

    public static final String CREATE_GRADES = """
        CREATE TABLE IF NOT EXISTS grades (
            grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
            assignment_id INTEGER NOT NULL,
            student_id INTEGER NOT NULL,
            score REAL NOT NULL
                CHECK (score >= 0),
            feedback TEXT DEFAULT '',
            graded_at TEXT NOT NULL
                DEFAULT (datetime('now')),
            updated_at TEXT,
            UNIQUE (assignment_id, student_id),
            FOREIGN KEY (assignment_id)
                REFERENCES assignments(assignment_id)
                ON DELETE CASCADE,
            FOREIGN KEY (student_id)
                REFERENCES users(id)
                ON DELETE CASCADE
        )
        """;

    /**
     * Creates grade table. Implement SQLite's foreign key verification
     * 
     * @param connection is the open database connection
     * @throws SQLException if table not created
     */
    public static void create(Connection connection) throws SQLException{
        try(Statement statement = connection.createStatement()){
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute(CREATE_GRADES);
        }
    }
}