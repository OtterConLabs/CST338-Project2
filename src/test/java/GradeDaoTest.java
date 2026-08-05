import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Optional;

/**
 * Tests GradeDao using an in-memory SQLite database.
 *
 * @author Jit Tran
 * @since 08/04/2026
 */
class GradeDaoTest {
    @Test
    void gradeDaoFullTest() throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:sqlite::memory:")) {
            try (Statement setup = connection.createStatement()) {
                setup.execute("""
                        CREATE TABLE users (id INTEGER PRIMARY KEY)
                        """);

                setup.execute("""
                        CREATE TABLE assignments (assignment_id INTEGER PRIMARY KEY)
                        """);

                setup.execute("INSERT INTO users VALUES (1)");

                setup.execute("INSERT INTO assignments VALUES (1)");
            }

            GradeSchema.create(connection);
            GradeDao gradeDao = new GradeDao(connection);

            Grade grade = new Grade(
                    1,
                    1,
                    85.0,
                    "Initial grade");

            // Insert & read
            int gradeID = gradeDao.insert(grade);

            assertTrue(gradeID > 0);

            Optional<Grade> savedResult = gradeDao.findByID(gradeID);

            assertTrue(savedResult.isPresent());

            Grade saved = savedResult.get();

            assertEquals(85.0, saved.getScore(), 0.001);

            // Query by assignment
            assertEquals(
                    1,
                    gradeDao.findbyAssignmentID(1).size());

            // Reject duplicate
            assertThrows(
                    SQLException.class,
                    () -> gradeDao.insert(
                            new Grade(1, 1, 90.0, "Duplicate")));

            // Update
            grade.setScore(95.0);
            assertTrue(gradeDao.update(grade));

            Optional<Grade> updatedResult = gradeDao.findByID(gradeID);

            assertTrue(updatedResult.isPresent());

            Grade updated = updatedResult.get();

            assertEquals(95.0, updated.getScore(), 0.001);

            // Delete
            assertTrue(gradeDao.deleteByID(gradeID));
            assertTrue(gradeDao.findByID(gradeID).isEmpty());
        }
    }
}