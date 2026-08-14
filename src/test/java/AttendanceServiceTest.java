import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

/**
 * Tests AttendanceService with SQLite database.
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
class AttendanceServiceTest {
    private Connection connection;
    private AttendanceDao attendanceDao;
    private AttendanceService attendanceService;

    private Course course;
    private User teacher;
    private User otherTeacher;
    private User enrolledStudent;
    private User unenrolledStudent;

    private LocalDate attendanceDate;

    @BeforeEach
    void setUp() throws SQLException{
        connection = DriverManager.getConnection(
            "jdbc:sqlite::memory:"
        );

        try(Statement setup = connection.createStatement()){
            setup.execute("PRAGMA foreign_keys = ON");

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

            setup.execute("INSERT INTO users VALUES (1)");
            setup.execute("INSERT INTO users VALUES (47)");
            setup.execute("INSERT INTO users VALUES (83)");
            setup.execute("INSERT INTO users VALUES (91)");

            setup.execute("INSERT INTO courses VALUES (12)");

            setup.execute("""
                          INSERT INTO enrollment (
                              course_id,
                              student_id
                          )
                          VALUES (12, 47)
                          """);
        }

        AttendanceSchema.create(connection);

        attendanceDao = new AttendanceDao(connection);

        EnrollmentDao enrollmentDao =
                new EnrollmentDao(connection);

        attendanceService = new AttendanceService(
                attendanceDao,
                enrollmentDao
        );

        course = new Course(
                12,
                "CST338",
                "Software Design",
                "",
                1,
                Course.UNLIMITED,
                null
        );

        teacher = new User(
                1,
                "teacher",
                "Taylor",
                "Teacher",
                "teacher@csumb.edu",
                "password",
                UserRole.TEACHER,
                null
        );

        otherTeacher = new User(
                91,
                "otherteacher",
                "Morgan",
                "Teacher",
                "otherteacher@csumb.edu",
                "password",
                UserRole.TEACHER,
                null
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

        attendanceDate = LocalDate.of(2026, 8, 14);
    }

    @AfterEach
    void tearDown() throws SQLException{
        if(connection != null
                && !connection.isClosed()){
            connection.close();
        }
    }

    @Test
    void validAttendanceIsSaved() throws SQLException{
        AttendanceRecord attendance =
                attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        teacher,
                        attendanceDate,
                        AttendanceStatus.PRESENT,
                        "On time"
                );

        assertTrue(attendance.getAttendanceID() > 0);
        assertEquals(
                AttendanceStatus.PRESENT,
                attendance.getStatus()
        );
        assertEquals("On time", attendance.getNotes());
        assertTrue(
                attendanceDao.findByID(
                        attendance.getAttendanceID()
                ).isPresent()
        );
    }

    @Test
    void existingAttendanceIsUpdated() throws SQLException{
        AttendanceRecord firstAttendance =
                attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        teacher,
                        attendanceDate,
                        AttendanceStatus.PRESENT,
                        "On time"
                );

        AttendanceRecord updatedAttendance =
                attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        teacher,
                        attendanceDate,
                        AttendanceStatus.LATE,
                        "Arrived late"
                );

        assertEquals(
                firstAttendance.getAttendanceID(),
                updatedAttendance.getAttendanceID()
        );
        assertEquals(
                1,
                attendanceDao.findByCourseAndDate(
                        course.getCourseId(),
                        attendanceDate
                ).size()
        );
        assertEquals(
                AttendanceStatus.LATE,
                updatedAttendance.getStatus()
        );
        assertEquals(
                "Arrived late",
                updatedAttendance.getNotes()
        );
    }

    @Test
    void unenrolledStudentIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> attendanceService.saveAttendance(
                        course,
                        unenrolledStudent,
                        teacher,
                        attendanceDate,
                        AttendanceStatus.PRESENT,
                        ""
                )
        );
    }

    @Test
    void nonTeacherCannotRecordAttendance(){
        assertThrows(
                IllegalArgumentException.class,
                () -> attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        enrolledStudent,
                        attendanceDate,
                        AttendanceStatus.PRESENT,
                        ""
                )
        );
    }

    @Test
    void teacherWhoDoesNotOwnCourseIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        otherTeacher,
                        attendanceDate,
                        AttendanceStatus.PRESENT,
                        ""
                )
        );
    }

    @Test
    void missingAttendanceDateIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        teacher,
                        null,
                        AttendanceStatus.PRESENT,
                        ""
                )
        );
    }

    @Test
    void missingStatusIsRejected(){
        assertThrows(
                IllegalArgumentException.class,
                () -> attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        teacher,
                        attendanceDate,
                        null,
                        ""
                )
        );
    }

    @Test
    void savedAttendanceIsDeleted() throws SQLException{
        AttendanceRecord attendance =
                attendanceService.saveAttendance(
                        course,
                        enrolledStudent,
                        teacher,
                        attendanceDate,
                        AttendanceStatus.ABSENT,
                        "Sick"
                );

        assertTrue(
                attendanceService.deleteAttendance(attendance)
        );
        assertTrue(
                attendanceDao.findByID(
                        attendance.getAttendanceID()
                ).isEmpty()
        );
    }
}