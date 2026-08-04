import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the insert, retrieval, query, and delete operations
 * performed by AssignmentDao.
 *
 * @author Jordan Browning
 * @since 7/31/2026
 */
class AssignmentDaoTest
{
    private AssignmentDao assignmentDao;
    private Assignment assignment;

    // Creates a new AssignmentDao and test Assignment before each test.
    @BeforeEach
    void setUp()
    {
        assignmentDao = new AssignmentDao(
                DatabaseManager.getInstance().getConnection()
        );

        assignment = new Assignment(
                2,
                "Unit Test Assignment",
                "Assignment created for testing",
                LocalDate.of(2026, 8, 15),
                100
        );
    }

    // Removes the test Assignment from the database after each test.
    @AfterEach
    void tearDown() throws SQLException
    {
        if (assignment.getAssignmentId() > 0)
        {
            assignmentDao.deleteById(
                    assignment.getAssignmentId()
            );
        }
    }

    // Closes the database connection after all tests finish.
    @AfterAll
    static void close()
    {
        DatabaseManager.getInstance().close();
    }

    // Verifies that insert successfully adds an Assignment
    // and returns a generated database ID.
    @Test
    void insertAssignment() throws SQLException
    {
        int generatedId = assignmentDao.insert(assignment);

        assertTrue(generatedId > 0);
        assertTrue(assignment.getAssignmentId() > 0);
    }

    // Verifies that findById returns the correct Assignment
    // after it has been inserted into the database.
    @Test
    void findById() throws SQLException
    {
        // Insert the test Assignment and receive its generated ID
        int generatedId = assignmentDao.insert(assignment);

        // Search the database using the generated assignment ID
        Optional<Assignment> foundAssignment =
                assignmentDao.findById(generatedId);

        // Verify that an Assignment was returned
        assertTrue(foundAssignment.isPresent());

        // Get the Assignment stored inside the Optional
        Assignment retrievedAssignment = foundAssignment.get();

        // Verify that the retrieved database values match the test Assignment
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
        // Insert the test Assignment
        assignmentDao.insert(assignment);

        // Retrieve every Assignment from the database
        List<Assignment> assignments = assignmentDao.findAll();

        // Verify that the returned list exists and contains information
        assertNotNull(assignments);
        assertFalse(assignments.isEmpty());

        // Tracks whether the test Assignment was found
        boolean wasAssignmentFound = false;

        // Search the returned list for the generated assignment ID
        for (Assignment currentAssignment : assignments)
        {
            if (currentAssignment.getAssignmentId()
                    == assignment.getAssignmentId())
            {
                wasAssignmentFound = true;
                break;
            }
        }

        // Verify that the inserted Assignment was found
        assertTrue(wasAssignmentFound);
    }

    // Verifies that findByCourseId returns an Assignment
    // belonging to the matching course.
    @Test
    void findByCourseId() throws SQLException
    {
        // Insert the test Assignment using its course ID
        assignmentDao.insert(assignment);

        // Retrieve all Assignments connected to the same course
        List<Assignment> courseAssignments =
                assignmentDao.findByCourseId(
                        assignment.getCourseId()
                );

        // Verify that the returned list exists and contains information
        assertNotNull(courseAssignments);
        assertFalse(courseAssignments.isEmpty());

        // Tracks whether the test Assignment was found
        boolean wasAssignmentFound = false;

        // Search the course list for the generated assignment ID
        for (Assignment currentAssignment : courseAssignments)
        {
            if (currentAssignment.getAssignmentId()
                    == assignment.getAssignmentId())
            {
                wasAssignmentFound = true;
                break;
            }
        }

        // Verify that the matching Assignment was returned
        assertTrue(wasAssignmentFound);
    }

    // Verifies that findByCourseId keeps Assignments separated
    // when more than one course ID is used.
    @Test
    void findByCourseIdWithDifferentCourseIds() throws SQLException
    {
        Assignment secondAssignment = new Assignment(
                3,
                "Second Course Assignment",
                "Assignment created for another course",
                LocalDate.of(2026, 8, 18),
                75
        );

        try
        {
            //Insert Assignments that belong to two different courses
            assignmentDao.insert(assignment);
            assignmentDao.insert(secondAssignment);

            //Retrieve the Assignments connected to course ID 2
            List<Assignment> firstCourseAssignments =
                    assignmentDao.findByCourseId(2);

            //Retrieve the Assignments connected to course ID 3
            List<Assignment> secondCourseAssignments =
                    assignmentDao.findByCourseId(3);

            //Track which Assignment IDs were returned for course ID 2
            boolean firstCourseHasFirstAssignment = false;
            boolean firstCourseHasSecondAssignment = false;

            for (Assignment currentAssignment : firstCourseAssignments)
            {
                if (currentAssignment.getAssignmentId()
                        == assignment.getAssignmentId())
                {
                    firstCourseHasFirstAssignment = true;
                }

                if (currentAssignment.getAssignmentId()
                        == secondAssignment.getAssignmentId())
                {
                    firstCourseHasSecondAssignment = true;
                }
            }

            //Track which Assignment IDs were returned for course ID 3
            boolean secondCourseHasFirstAssignment = false;
            boolean secondCourseHasSecondAssignment = false;

            for (Assignment currentAssignment : secondCourseAssignments)
            {
                if (currentAssignment.getAssignmentId()
                        == assignment.getAssignmentId())
                {
                    secondCourseHasFirstAssignment = true;
                }

                if (currentAssignment.getAssignmentId()
                        == secondAssignment.getAssignmentId())
                {
                    secondCourseHasSecondAssignment = true;
                }
            }

            //Verify that each course only returned its own Assignment
            assertTrue(firstCourseHasFirstAssignment);
            assertFalse(firstCourseHasSecondAssignment);
            assertFalse(secondCourseHasFirstAssignment);
            assertTrue(secondCourseHasSecondAssignment);
        }
        finally
        {
            //Remove the second Assignment created only for this test
            if (secondAssignment.getAssignmentId() > 0)
            {
                assignmentDao.deleteById(
                        secondAssignment.getAssignmentId()
                );
            }
        }
    }

    // Verifies that deleteById removes an Assignment
    // from the database.
    @Test
    void deleteById() throws SQLException
    {
        // Insert the test Assignment
        int generatedId = assignmentDao.insert(assignment);

        // Delete the Assignment using its generated ID
        boolean deleted =
                assignmentDao.deleteById(generatedId);

        // Verify that one Assignment was deleted
        assertTrue(deleted);

        // Search for the deleted Assignment
        Optional<Assignment> deletedAssignment =
                assignmentDao.findById(generatedId);

        // A deleted Assignment should no longer be returned
        assertTrue(deletedAssignment.isEmpty());

        // Prevent tearDown from attempting to delete it again
        assignment.setAssignmentId(0);
    }

    // Verifies that update saves the modified Assignment information
    // and that the updated values can be retrieved from the database.
    @Test
    void updateAssignment() throws SQLException
    {
        //Insert the test Assignment so it receives a database ID
        assignmentDao.insert(assignment);

        //Change the Assignment information
        assignment.setTitle("Updated Unit Test Assignment");
        assignment.setDescription("Updated description for testing");
        assignment.setDueDate(
                LocalDate.of(2026, 8, 20)
        );
        assignment.setPointsPossible(150);

        //Update the matching database row
        boolean updated = assignmentDao.update(assignment);

        //Verify that one Assignment was updated
        assertTrue(updated);

        //Retrieve the updated Assignment using its database ID
        Optional<Assignment> foundAssignment =
                assignmentDao.findById(
                        assignment.getAssignmentId()
                );

        //Verify that the Assignment still exists
        assertTrue(foundAssignment.isPresent());

        //Get the updated Assignment from the Optional
        Assignment retrievedAssignment =
                foundAssignment.get();

        //Verify that the updated values were saved
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