import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Database operations for AttendanceRecord.java
 *
 * @author Jit Tran
 * @since 08/11/2026
 */
public class AttendanceDao {
    private final Connection connection;

    public AttendanceDao(Connection connection){
        if(connection == null){
            throw new IllegalArgumentException(
                    "AttendanceDao requires an open connection"
            );
        }

        this.connection = connection;
    }

    public int insert(AttendanceRecord record) throws SQLException{
        requireAttendance(record);

        String sql = """
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

        try(PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            statement.setInt(1, record.getCourseID());
            statement.setInt(2, record.getStudentID());
            statement.setInt(3, record.getRecordedBy());

            statement.setString(4, record.getAttendanceDate().toString());
            statement.setString(5, record.getStatus().name());
            statement.setString(6, record.getNotes());

            int affectedRows = statement.executeUpdate();

            if(affectedRows != 1){
                throw new SQLException(
                        "Attendance insertion affected "
                        + affectedRows
                        + " rows"
                );
            }

            try(ResultSet generatedKeys = statement.getGeneratedKeys()){
                if(!generatedKeys.next()){
                    throw new SQLException(
                            "Failed attendance insertion: no ID"
                    );
                }

                int generatedID = generatedKeys.getInt(1);
                record.setAttendanceID(generatedID);

                return generatedID;
            }
        }
    }

    public Optional<AttendanceRecord> findByID(
            int attendanceID
    ) throws SQLException{
        if(attendanceID <= 0){
            return Optional.empty();
        }

        String sql = """
                SELECT
                    attendance_id,
                    course_id,
                    student_id,
                    recorded_by,
                    attendance_date,
                    status,
                    notes,
                    updated_at
                FROM attendance
                WHERE attendance_id = ?
                """;

        try(PreparedStatement statement =
                connection.prepareStatement(sql)){
            statement.setInt(1, attendanceID);

            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(
                            mapAttendance(resultSet)
                    );
                }

                return Optional.empty();
            }
        }
    }

    public List<AttendanceRecord> findByCourseAndDate(
            int courseID,
            LocalDate attendanceDate
    ) throws SQLException{
        if(courseID <= 0){
            throw new IllegalArgumentException(
                    "Course ID must be positive integer"
            );
        }

        if(attendanceDate == null){
            throw new IllegalArgumentException(
                    "Attendance date must be valid"
            );
        }

        String sql = """
                SELECT
                    attendance_id,
                    course_id,
                    student_id,
                    recorded_by,
                    attendance_date,
                    status,
                    notes,
                    updated_at
                FROM attendance
                WHERE course_id = ?
                    AND attendance_date = ?
                ORDER BY student_id
                """;

        List<AttendanceRecord> records = new ArrayList<>();

        try(PreparedStatement statement =
                connection.prepareStatement(sql)){
            statement.setInt(1, courseID);
            statement.setString(
                    2,
                    attendanceDate.toString()
            );

            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    records.add(
                            mapAttendance(resultSet)
                    );
                }
            }
        }

        return records;
    }

    public List<AttendanceReportRow> findReport(
        int courseID,

        Integer studentID,

        LocalDate startDate,
        LocalDate endDate,

        AttendanceStatus status
    ) throws SQLException{
        if(courseID <= 0){
            throw new IllegalArgumentException(
                "Course ID must be a positive integer"
            );
        }

        if(studentID != null && studentID <= 0){
            throw new IllegalArgumentException(
                "Student ID must be a positive integer"
            );
        }

        StringBuilder sql = new StringBuilder(
            """
            SELECT
                a.attendance_id,
                a.course_id,
                a.student_id,
                a.recorded_by,
                a.attendance_date,
                a.status,
                a.notes,
                a.updated_at,
                student_user.first_name || ' '
                    || student_user.last_name AS student_name,
                recorder_user.first_name || ' '
                    || recorder_user.last_name AS recorded_by_name
            FROM attendance a
            JOIN users student_user
                ON student_user.id = a.student_id
            JOIN users recorder_user
                ON recorder_user.id = a.recorded_by
            WHERE a.course_id = ?
            """);

        if(studentID != null){
            sql.append(" AND a.student_id = ?");
        }

        if(startDate != null){
            sql.append(" AND a.attendance_date >= ?");
        }

        if(endDate != null){
            sql.append(" AND a.attendance_date <= ?");
        }

        if(status != null){
            sql.append(" AND a.status = ?");
        }

        sql.append(
            """
            ORDER BY
                a.attendance_date,
                student_user.last_name,
                student_user.first_name
            """);

        List<AttendanceReportRow> reportRows = new ArrayList<>();

        try(PreparedStatement statement =
            connection.prepareStatement(sql.toString())){
                int parameterIndex = 1;
                statement.setInt(parameterIndex++, courseID);

                if(studentID != null){
                    statement.setInt(
                    parameterIndex++,
                    studentID
                );
                }

                if(startDate != null){
                    statement.setString(
                        parameterIndex++,
                        startDate.toString()
                    );
                }

                if(endDate != null){
                    statement.setString(
                        parameterIndex++,
                        endDate.toString()
                );
                }

                if(status != null){
                    statement.setString(
                        parameterIndex,
                        status.name()
                    );
                }

                try(ResultSet resultSet = statement.executeQuery()){
                    while(resultSet.next()){
                        reportRows.add(
                            mapReportRow(resultSet)
                    );
                }
            }
        }

        return reportRows;
    }

    public boolean update(
            AttendanceRecord record
    ) throws SQLException{
        requireAttendance(record);

        if(record.getAttendanceID() <= 0){
            throw new IllegalArgumentException(
                    "Attendance record must have an ID before updating"
            );
        }

        String sql = """
                UPDATE attendance
                SET
                    status = ?,
                    notes = ?,
                    recorded_by = ?,
                    updated_at = datetime('now')
                WHERE attendance_id = ?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, record.getStatus().name());
            statement.setString(2, record.getNotes());

            statement.setInt(3, record.getRecordedBy());
            statement.setInt(4, record.getAttendanceID());

            return statement.executeUpdate() == 1;
        }
    }

    public boolean deleteByID(
            int attendanceID
    ) throws SQLException{
        if(attendanceID <= 0){
            return false;
        }

        String sql = """
                DELETE FROM attendance
                WHERE attendance_id = ?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, attendanceID);

            return statement.executeUpdate() == 1;
        }
    }

    private AttendanceRecord mapAttendance(
            ResultSet resultSet
    ) throws SQLException{
        return new AttendanceRecord(
                resultSet.getInt("attendance_id"),
                resultSet.getInt("course_id"),
                resultSet.getInt("student_id"),
                resultSet.getInt("recorded_by"),
                LocalDate.parse(
                        resultSet.getString("attendance_date")
                ),
                AttendanceStatus.valueOf(
                        resultSet.getString("status")
                ),
                resultSet.getString("notes"),
                resultSet.getString("updated_at")
        );
    }

    private AttendanceReportRow mapReportRow(
        ResultSet resultSet
    ) throws SQLException{
        return new AttendanceReportRow(
            mapAttendance(resultSet),
            resultSet.getString("student_name"),
            resultSet.getString("recorded_by_name")
        );
    }
    
    private void requireAttendance(
            AttendanceRecord record
    ){
        if(record == null){
            throw new IllegalArgumentException(
                    "Attendance record cannot be null"
            );
        }
    }
}