import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the insert, retrieval, query, update, and delete operations
 * performed by AssignmentDao.
 *
 * @author Jordan Browning
 * @since 7/31/2026
 */
class AssignmentDaoTest
{
    private Connection connection;
    private AssignmentDao assignmentDao;
    private Assignment assignment;

    // Stores the database IDs created for each test so they can be removed afterward.
    private int testTeacherId;
    private int testCourseId;

    /**
     * Creates a real Teacher and Course before each test.
     * Assignments now use a foreign key to courses, so the Course must
     * exist before the Assignment can be inserted.
     *
     * @throws SQLException If the test database cannot be prepared.
     */
    @BeforeEach
    void setUp() throws SQLException
    {
        connection =
                DatabaseManager.getInstance().getConnection();

        assignmentDao =
                new AssignmentDao(connection);

        testTeacherId = createTestTeacher();
        testCourseId = createTestCourse(testTeacherId);

        assignment = new Assignment(
                testCourseId,
                "Unit Test Assignment",
                "Assignment created for testing",
                LocalDate.of(2026, 8, 15),
                100
        );
    }

    /**
     * Removes the records created by each test.
     * The Assignment is removed first because it belongs to the Course,
     * and the Course is removed before its Teacher.
     *
     * @throws SQLException If the test records cannot be removed.
     */
    @AfterEach
    void tearDown() throws SQLException
    {
        if (assignment != null
                && assignment.getAssignmentId() > 0)
        {
            assignmentDao.deleteById(
                    assignment.getAssignmentId()
            );
        }

        if (testCourseId > 0)
        {
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 "DELETE FROM courses WHERE course_id = ?"
                         ))
            {
                statement.setInt(1, testCourseId);
                statement.executeUpdate();
            }
        }

        if (testTeacherId > 0)
        {
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 "DELETE FROM users WHERE id = ?"
                         ))
            {
                statement.setInt(1, testTeacherId);
                statement.executeUpdate();
            }
        }
    }

    // Closes the database connection after all tests finish.
    @AfterAll
    static void close()
    {
        DatabaseManager.getInstance().close();
    }

    /**
     * Creates a Teacher row used by the Course created for the current test.
     *
     * @return The generated Teacher user ID.
     * @throws SQLException If the Teacher cannot be inserted.
     */
    private int createTestTeacher() throws SQLException
    {
        String uniqueValue =
                Long.toString(System.nanoTime());

        String sql = """
                INSERT INTO users (
                    username,
                    first_name,
                    last_name,
                    email,
                    password,
                    role
                )
                VALUES (?, ?, ?, ?, ?, 'TEACHER')
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     ))
        {
            statement.setString(
                    1,
                    "assignment_test_teacher_" + uniqueValue
            );
            statement.setString(
                    2,
                    "Assignment"
            );
            statement.setString(
                    3,
                    "Tester"
            );
            statement.setString(
                    4,
                    "assignment_test_" + uniqueValue + "@example.com"
            );
            statement.setString(
                    5,
                    "TestPassword1!"
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys())
            {
                if (keys.next())
                {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Unable to create the Teacher used by AssignmentDaoTest."
        );
    }

    /**
     * Creates the Course that the current test Assignment belongs to.
     *
     * @param teacherId The Teacher who owns the test Course.
     * @return The generated Course ID.
     * @throws SQLException If the Course cannot be inserted.
     */
    private int createTestCourse(int teacherId)
            throws SQLException
    {
        String uniqueValue =
                Long.toString(System.nanoTime());

        String sql = """
                INSERT INTO courses (
                    course_code,
                    course_name,
                    description,
                    teacher_id
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     ))
        {
            statement.setString(
                    1,
                    "TEST" + uniqueValue
            );
            statement.setString(
                    2,
                    "Assignment DAO Test Course"
            );
            statement.setString(
                    3,
                    "Course created for AssignmentDaoTest"
            );
            statement.setInt(
                    4,
                    teacherId
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys())
            {
                if (keys.next())
                {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Unable to create the Course used by AssignmentDaoTest."
        );
    }

    // Verifies that insert successfully adds an Assignment
    // and returns a generated database ID.
    @Test
    void insertAssignment() throws SQLException
    {
        int generatedId =
                assignmentDao.insert(assignment);

        assertTrue(generatedId > 0);
        assertTrue(
                assignment.getAssignmentId() > 0
        );
    }

    // Verifies that findById returns the correct Assignment
    // after it has been inserted into the database.
    @Test
    void findById() throws SQLException
    {
        int generatedId =
                assignmentDao.insert(assignment);

        Optional<Assignment> foundAssignment =
                assignmentDao.findById(
                        generatedId
                );

        assertTrue(
                foundAssignment.isPresent()
        );

        Assignment retrievedAssignment =
                foundAssignment.get();

        assertEquals(
                assignment.getAssignmentId(),
                retrievedAssignment.getAssignmentId()
        );

        assertEquals(
                assignment.getCourseId(),
                retrievedAssignment.getCourseId()
        );

        assertEquals(
                assignment.getTitle(),
                retrievedAssignment.getTitle()
        );

        assertEquals(
                assignment.getDescription(),
                retrievedAssignment.getDescription()
        );

        assertEquals(
                assignment.getDueDate(),
                retrievedAssignment.getDueDate()
        );

        assertEquals(
                assignment.getPointsPossible(),
                retrievedAssignment.getPointsPossible()
        );
    }

    // Verifies that findAll includes an Assignment
    // that was previously inserted into the database.
    @Test
    void findAll() throws SQLException
    {
        assignmentDao.insert(assignment);

        List<Assignment> assignments =
                assignmentDao.findAll();

        assertNotNull(assignments);
        assertFalse(assignments.isEmpty());

        boolean wasAssignmentFound = false;

        for (Assignment currentAssignment : assignments)
        {
            if (currentAssignment.getAssignmentId()
                    == assignment.getAssignmentId())
            {
                wasAssignmentFound = true;
                break;
            }
        }

        assertTrue(wasAssignmentFound);
    }

    // Verifies that findByCourseId returns an Assignment
    // belonging to the matching Course.
    @Test
    void findByCourseId() throws SQLException
    {
        assignmentDao.insert(assignment);

        List<Assignment> courseAssignments =
                assignmentDao.findByCourseId(
                        assignment.getCourseId()
                );

        assertNotNull(courseAssignments);
        assertFalse(courseAssignments.isEmpty());

        boolean wasAssignmentFound = false;

        for (Assignment currentAssignment : courseAssignments)
        {
            if (currentAssignment.getAssignmentId()
                    == assignment.getAssignmentId())
            {
                wasAssignmentFound = true;
                break;
            }
        }

        assertTrue(wasAssignmentFound);
    }

    // Verifies that deleteById removes an Assignment
    // from the database.
    @Test
    void deleteById() throws SQLException
    {
        int generatedId =
                assignmentDao.insert(assignment);

        boolean deleted =
                assignmentDao.deleteById(
                        generatedId
                );

        assertTrue(deleted);

        Optional<Assignment> deletedAssignment =
                assignmentDao.findById(
                        generatedId
                );

        assertTrue(
                deletedAssignment.isEmpty()
        );

        assignment.setAssignmentId(0);
    }

    // Verifies that deleteById returns false for an invalid ID.
    @Test
    void deleteByIdInvalidId() throws SQLException
    {
        boolean deleted =
                assignmentDao.deleteById(0);

        assertFalse(deleted);
    }

    // Verifies that update saves the modified Assignment information
    // and that the updated values can be retrieved from the database.
    @Test
    void updateAssignment() throws SQLException
    {
        assignmentDao.insert(assignment);

        assignment.setTitle(
                "Updated Unit Test Assignment"
        );
        assignment.setDescription(
                "Updated description for testing"
        );
        assignment.setDueDate(
                LocalDate.of(2026, 8, 20)
        );
        assignment.setPointsPossible(150);

        boolean updated =
                assignmentDao.update(assignment);

        assertTrue(updated);

        Optional<Assignment> foundAssignment =
                assignmentDao.findById(
                        assignment.getAssignmentId()
                );

        assertTrue(
                foundAssignment.isPresent()
        );

        Assignment retrievedAssignment =
                foundAssignment.get();

        assertEquals(
                assignment.getTitle(),
                retrievedAssignment.getTitle()
        );

        assertEquals(
                assignment.getDescription(),
                retrievedAssignment.getDescription()
        );

        assertEquals(
                assignment.getDueDate(),
                retrievedAssignment.getDueDate()
        );

        assertEquals(
                assignment.getPointsPossible(),
                retrievedAssignment.getPointsPossible()
        );
    }
}
