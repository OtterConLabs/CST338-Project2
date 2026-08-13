import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Optional;

/**
 * Test GradeDao with SQLite database
 *
 * @author Jit Tran
 * @since 08/04/2026
 */
class GradeDaoTest{
    private Connection connection;
    private GradeDao gradeDao;

    @BeforeEach
    void setUp() throws SQLException{
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try(Statement setup = connection.createStatement()){
            setup.execute("""
                          CREATE TABLE users (id INTEGER PRIMARY KEY)
                          """);

            setup.execute("""
                          CREATE TABLE assignments (assignment_id INTEGER PRIMARY KEY)
                          """);

            setup.execute("INSERT INTO users VALUES (14)");
            setup.execute("INSERT INTO users VALUES (28)");
            setup.execute("INSERT INTO assignments VALUES (7)");
            setup.execute("INSERT INTO assignments VALUES (19)");
        }

        GradeSchema.create(connection);
        gradeDao = new GradeDao(connection);
    }

    @AfterEach
    void tearDown() throws SQLException{
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }

    @Test
    void insertGrade() throws SQLException{
        Grade grade = new Grade(7, 14, 92.5, "Initial grade");

        int gradeID = gradeDao.insert(grade);

        assertTrue(gradeID > 0);
        assertEquals(gradeID, grade.getGradeID());
    }

    @Test
    void findGradeByID() throws SQLException{
        Grade grade = new Grade(19, 28, 88.5, "Initial grade");
        int gradeID = gradeDao.insert(grade);

        Optional<Grade> savedResult = gradeDao.findByID(gradeID);

        assertTrue(savedResult.isPresent());
        assertEquals(88.5, savedResult.get().getScore(), 0.001);
        assertEquals("Initial grade", savedResult.get().getFeedback());
    }

    @Test
    void findGradesByAssignmentID() throws SQLException{
        gradeDao.insert(new Grade(7, 28, 79.5, ""));
        gradeDao.insert(new Grade(7, 14, 94.0, ""));
        gradeDao.insert(new Grade(19, 14, 86.0, ""));

        assertEquals(2, gradeDao.findbyAssignmentID(7).size());
    }

    @Test
    void duplicateGrade() throws SQLException{
        gradeDao.insert(new Grade(19, 28, 82.0, ""));

        assertThrows(
                SQLException.class, () -> gradeDao.insert(new Grade(19, 28, 91.0, ""))
        );
    }

    @Test
    void updateGrade() throws SQLException{
        Grade grade = new Grade(7, 14, 73.5, "");
        int gradeID = gradeDao.insert(grade);

        grade.setScore(89.5);
        grade.setFeedback("Updated");

        assertTrue(gradeDao.update(grade));

        Grade updated = gradeDao.findByID(gradeID).orElseThrow();

        assertEquals(89.5, updated.getScore(), 0.001);
        assertEquals("Updated", updated.getFeedback());
    }

    @Test
    void deleteGrade() throws SQLException{
        Grade grade = new Grade(19, 28, 90.0, "");
        int gradeID = gradeDao.insert(grade);

        assertTrue(gradeDao.deleteByID(gradeID));
        assertTrue(gradeDao.findByID(gradeID).isEmpty());
    }

    @Test
    void findGradeByAssignmentAndStudent() throws SQLException{
        gradeDao.insert(new Grade(7, 14, 92.5, "Good"));
        gradeDao.insert(new Grade(7, 28, 81.0, ""));
    
        Optional<Grade> result =
                gradeDao.findByAssignmentAndStudent(7, 14);
    
        assertTrue(result.isPresent());
        
        assertEquals(7, result.get().getAssignmentID());
        assertEquals(14, result.get().getStudentID());
        assertEquals(92.5, result.get().getScore(), 0.001);
    }
    
    @Test
    void missingGradeByAssignmentAndStudent() throws SQLException{
        Optional<Grade> result =
                gradeDao.findByAssignmentAndStudent(19, 28);
    
        assertTrue(result.isEmpty());
    }
}