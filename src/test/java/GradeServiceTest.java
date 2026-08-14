import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

import java.util.List;

/**
 * Tests GradeService with SQLite database.
 *
 * @author Jit Tran
 * @since 08/13/2026
 */
class GradeServiceTest {
    private Connection connection;
    private GradeDao gradeDao;
    private GradeService gradeService;

    private Assignment assignment;
    private User enrolledStudent;
    private User unenrolledStudent;

    @BeforeEach
    void setUp() throws SQLException{
        connection = DriverManager.getConnection(
                "jdbc:sqlite::memory:"
        );

        try(Statement setup = connection.createStatement()){
            setup.execute("""
                          CREATE TABLE users (
                              id INTEGER PRIMARY KEY
                          )
                          """);

            setup.execute("""
                          CREATE TABLE courses (
                              course_id INTEGER PRIMARY KEY
                          )
                          """);

            setup.execute("""
                          CREATE TABLE enrollment (
                              enrollment_id INTEGER
                                  PRIMARY KEY AUTOINCREMENT,
                              course_id INTEGER NOT NULL,
                              student_id INTEGER NOT NULL,
                              UNIQUE (course_id, student_id),
                              FOREIGN KEY (course_id)
                                  REFERENCES courses(course_id),
                              FOREIGN KEY (student_id)
                                  REFERENCES users(id)
                          )
                          """);

            setup.execute("""
                          CREATE TABLE assignments (
                              assignment_id INTEGER PRIMARY KEY,
                              course_id INTEGER NOT NULL,
                              FOREIGN KEY (course_id)
                                  REFERENCES courses(course_id)
                          )
                          """);

            setup.execute(
                    "INSERT INTO users VALUES (47)"
            );
            setup.execute(
                    "INSERT INTO users VALUES (83)"
            );
            setup.execute(
                    "INSERT INTO courses VALUES (12)"
            );
            setup.execute(
                    "INSERT INTO assignments VALUES (36, 12)"
            );
            setup.execute("""
                          INSERT INTO enrollment (
                              course_id,
                              student_id
                          )
                          VALUES (12, 47)
                          """);
        }

        GradeSchema.create(connection);

        gradeDao = new GradeDao(connection);

        EnrollmentDao enrollmentDao =
                new EnrollmentDao(connection);

        gradeService = new GradeService(gradeDao, enrollmentDao);

        assignment = new Assignment(
                36,
                12,
                "Unit Test Assignment",
                "",
                LocalDate.of(2026, 8, 14),
                120
        );

        enrolledStudent = new User(
                47,
                "jordan",
                "Jordan",
                "Student",
                "j@csumb.edu",
                "password",
                UserRole.STUDENT,
                null
        );

        unenrolledStudent = new User(
                83,
                "yoko",
                "Yoko",
                "Student",
                "y@csumb.edu",
                "password",
                UserRole.STUDENT,
                null
        );
    }

    @AfterEach
    void tearDown() throws SQLException{
        if(connection != null
                && !connection.isClosed()){
            connection.close();
        }
    }

    @Test
    void validGradeIsSaved() throws SQLException{
        Grade grade = gradeService.saveGrade(
                assignment,
                enrolledStudent,
                "107.5",
                "Good work"
        );

        assertTrue(grade.getGradeID() > 0);
        assertEquals(
                107.5,
                grade.getScore(),
                0.001
        );
        assertEquals(
                "Good work",
                grade.getFeedback()
        );
        assertTrue(
                gradeDao.findByAssignmentAndStudent(
                        36,
                        47
                ).isPresent()
        );
    }

    @Test
    void existingGradeIsUpdated() throws SQLException{
        Grade firstGrade = gradeService.saveGrade(
                assignment,
                enrolledStudent,
                "73",
                "First score"
        );

        Grade updatedGrade = gradeService.saveGrade(
                assignment,
                enrolledStudent,
                "98",
                "Updated score"
        );

        assertEquals(
                firstGrade.getGradeID(),
                updatedGrade.getGradeID()
        );
        assertEquals(
                1,
                gradeDao.findbyAssignmentID(36).size()
        );
        assertEquals(
                98.0,
                updatedGrade.getScore(),
                0.001
        );
        assertEquals(
                "Updated score",
                updatedGrade.getFeedback()
        );
    }

    @Test
    void blankScoreIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> gradeService.saveGrade(
                        assignment,
                        enrolledStudent,
                        "",
                        ""
                )
        );
    }

    @Test
    void nonnumericScoreIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> gradeService.saveGrade(
                        assignment,
                        enrolledStudent,
                        "abc",
                        ""
                )
        );
    }

    @Test
    void negativeScoreIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> gradeService.saveGrade(
                        assignment,
                        enrolledStudent,
                        "-6",
                        ""
                )
        );
    }

    @Test
    void scoreAboveMaximumIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> gradeService.saveGrade(
                        assignment,
                        enrolledStudent,
                        "121",
                        ""
                )
        );
    }

    @Test
    void zeroPointsPossibleIsAllowed(){
        assertEquals(
                0.0,
                gradeService.validateScore(
                        "0",
                        0
                ),
                0.001
        );
    }

    @Test
    void negativePointsPossibleIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> gradeService.validateScore(
                        "0",
                        -1
                )
        );
    }

    @Test
    void unenrolledStudentIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> gradeService.saveGrade(
                        assignment,
                        unenrolledStudent,
                        "89",
                        ""
                )
        );
    }

    @Test
    void savedGradeIsDeleted() throws SQLException{
        Grade grade = gradeService.saveGrade(
                assignment,
                enrolledStudent,
                "64",
                ""
        );

        assertTrue(
                gradeService.deleteGrade(grade)
        );
        assertTrue(
                gradeDao.findByID(
                        grade.getGradeID()
                ).isEmpty()
        );
    }

    @Test
    void emptyGradesReturnEmptyStatistics(){
        GradeStatistics statistics =
                gradeService.calculateStatistics(List.of(), 100, 4);

        assertFalse(statistics.hasGrades());
        assertEquals(0, statistics.getGradedCount());
        assertEquals(4, statistics.getUngradedCount());
    }

    @Test
    void statisticsUsePercentagesAndCountUngradedStudents(){
        List<Grade> grades = List.of(
                new Grade(36, 47, 55, ""),
                new Grade(36, 83, 65, ""),
                new Grade(36, 19, 75, ""),
                new Grade(36, 64, 85, ""),
                new Grade(36, 28, 95, "")
        );

        GradeStatistics statistics =
                gradeService.calculateStatistics(
                        grades,
                        100,
                        7
                );

        assertTrue(statistics.hasGrades());

        assertEquals(5, statistics.getGradedCount());
        assertEquals(2, statistics.getUngradedCount());
        assertEquals(75.0, statistics.getMean(), 0.001);
        assertEquals(75.0, statistics.getMedian(), 0.001);
        assertEquals(55.0, statistics.getMinimum(), 0.001);
        assertEquals(95.0, statistics.getMaximum(), 0.001);

        assertEquals(1, statistics.getBelow60Count());
        assertEquals(1, statistics.getSixtyToSixtyNineCount());
        assertEquals(1, statistics.getSeventyToSeventyNineCount());
        assertEquals(1, statistics.getEightyToEightyNineCount());
        assertEquals(1, statistics.getNinetyToOneHundredCount());
    }

    @Test
    void evenNumberOfGradesUsesMiddleTwoForMedian(){
        List<Grade> grades = List.of(
                new Grade(36, 47, 40, ""),
                new Grade(36, 83, 60, ""),
                new Grade(36, 19, 80, ""),
                new Grade(36, 64, 100, "")
        );

        GradeStatistics statistics =
                gradeService.calculateStatistics(
                        grades,
                        100,
                        4
                );

        assertEquals(
                70.0,
                statistics.getMedian(),
                0.001
        );
    }

    @Test
    void distributionUsesCorrectBoundaries(){
        List<Grade> grades = List.of(
                new Grade(36, 11, 59.9, ""),
                new Grade(36, 12, 60, ""),
                new Grade(36, 13, 69.9, ""),
                new Grade(36, 14, 70, ""),
                new Grade(36, 15, 79.9, ""),
                new Grade(36, 16, 80, ""),
                new Grade(36, 17, 89.9, ""),
                new Grade(36, 18, 90, ""),
                new Grade(36, 19, 100, "")
        );

        GradeStatistics statistics =
                gradeService.calculateStatistics(
                        grades,
                        100,
                        9
                );

        assertEquals(1, statistics.getBelow60Count());
        assertEquals(2, statistics.getSixtyToSixtyNineCount());
        assertEquals(2, statistics.getSeventyToSeventyNineCount());
        assertEquals(2, statistics.getEightyToEightyNineCount());
        assertEquals(2, statistics.getNinetyToOneHundredCount());
    }

    @Test
    void zeroPointAssignmentCannotCalculateStatistics(){
        List<Grade> grades = List.of(
                new Grade(36, 47, 0, "")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> gradeService.calculateStatistics(
                        grades,
                        0,
                        1
                )
        );
    }
}