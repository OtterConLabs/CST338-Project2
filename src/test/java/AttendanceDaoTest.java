import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

import java.util.Optional;

/**
 * Tests AttendanceDao with SQLite database
 *
 * @author Jit Tran
 * @since 08/13/2026
 */
class AttendanceDaoTest {
    @Test
    void attendanceDaoFullTest() throws SQLException{
        try(Connection connection = DriverManager.getConnection(
                "jdbc:sqlite::memory:")){
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

                setup.execute("INSERT INTO users VALUES (1)");
                setup.execute("INSERT INTO users VALUES (2)");
                setup.execute("INSERT INTO courses VALUES (1)");
            }

            AttendanceSchema.create(connection);
            AttendanceDao attendanceDao =
                    new AttendanceDao(connection);

            LocalDate attendanceDate =
                    LocalDate.of(2026, 8, 13);

            AttendanceRecord attendance =
                    new AttendanceRecord(
                            1,
                            1,
                            2,
                            attendanceDate,
                            AttendanceStatus.PRESENT,
                            "On time"
                    );

            // Insert and read
            int attendanceID =
                    attendanceDao.insert(attendance);

            assertTrue(attendanceID > 0);

            Optional<AttendanceRecord> savedResult =
                    attendanceDao.findByID(attendanceID);

            assertTrue(savedResult.isPresent());

            AttendanceRecord saved = savedResult.get();

            assertEquals(
                    AttendanceStatus.PRESENT,
                    saved.getStatus()
            );
            assertEquals("On time", saved.getNotes());

            // Find by course and date
            assertEquals(
                    1,
                    attendanceDao.findByCourseAndDate(
                            1,
                            attendanceDate
                    ).size()
            );

            // Reject duplicate attendance
            assertThrows(
                    SQLException.class,
                    () -> attendanceDao.insert(
                            new AttendanceRecord(
                                    1,
                                    1,
                                    2,
                                    attendanceDate,
                                    AttendanceStatus.ABSENT,
                                    "Duplicate"
                            )
                    )
            );

            // Reject invalid foreign key
            assertThrows(
                    SQLException.class,
                    () -> attendanceDao.insert(
                            new AttendanceRecord(
                                    99,
                                    1,
                                    2,
                                    attendanceDate.plusDays(1),
                                    AttendanceStatus.PRESENT,
                                    ""
                            )
                    )
            );

            // Reject invalid status
            String invalidStatusSQL = """
                                      INSERT INTO attendance (
                                        course_id,
                                        student_id,
                                        recorded_by,
                                        attendance_date,
                                        status,
                                        notes
                                      )
                                      VALUES (?, ?, ?, ?, ?, ?)
                                      """;

            assertThrows(SQLException.class, () -> {
                try(PreparedStatement statement =
                        connection.prepareStatement(
                                invalidStatusSQL
                        )){
                    statement.setInt(1, 1);
                    statement.setInt(2, 1);
                    statement.setInt(3, 2);
                    
                    statement.setString(4, attendanceDate.plusDays(1).toString()
                    );

                    statement.setString(5, "MISSING");
                    statement.setString(6, "");

                    statement.executeUpdate();
                }
            });

            // Update
            attendance.setStatus(AttendanceStatus.LATE);
            attendance.setNotes("Arrived late");

            assertTrue(attendanceDao.update(attendance));

            Optional<AttendanceRecord> updatedResult =
                    attendanceDao.findByID(attendanceID);

            assertTrue(updatedResult.isPresent());

            AttendanceRecord updated = updatedResult.get();

            assertEquals(
                    AttendanceStatus.LATE,
                    updated.getStatus()
            );
            assertEquals(
                    "Arrived late",
                    updated.getNotes()
            );

            // Delete
            assertTrue(
                    attendanceDao.deleteByID(attendanceID)
            );
            assertTrue(
                    attendanceDao.findByID(
                            attendanceID
                    ).isEmpty()
            );
        }
    }
}