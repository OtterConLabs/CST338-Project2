import java.sql.SQLException;

import java.time.LocalDate;

import java.util.List;

/**
 * Handles validation and save function for Attendance Roster
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
public class AttendanceService{
    private final AttendanceDao attendanceDao;
    private final EnrollmentDao enrollmentDao;

    public AttendanceService(
            AttendanceDao attendanceDao,
            EnrollmentDao enrollmentDao
    ){
        if(attendanceDao == null){
            throw new IllegalArgumentException(
                    "AttendanceService requires an AttendanceDao"
            );
        }

        if(enrollmentDao == null){
            throw new IllegalArgumentException(
                    "AttendanceService requires an EnrollmentDao"
            );
        }

        this.attendanceDao = attendanceDao;
        this.enrollmentDao = enrollmentDao;
    }

    public AttendanceRecord saveAttendance(
            Course course,
            User student,
            User teacher,
            LocalDate attendanceDate,
            AttendanceStatus status,
            String notes
    ) throws SQLException{
        if(course == null){
            throw new IllegalArgumentException("Select a course");
        }

        if(student == null){
            throw new IllegalArgumentException( "Select a student");
        }

        if(teacher == null){
            throw new IllegalArgumentException(
                "Log in as a teacher to record attendance"
            );
        }

        if(course.getCourseId() <= 0){
            throw new IllegalArgumentException(
                 "Course must be saved before recording attendance"
            );
        }

        if(student.getId() <= 0){
            throw new IllegalArgumentException(
                 "Student must be saved before recording attendance"
            );
        }

        if(teacher.getId() <= 0){
            throw new IllegalArgumentException(
                "Teacher must be saved before recording attendance"
            );
        }

        if(teacher.getRole() != UserRole.TEACHER){
            throw new IllegalArgumentException(
                "Only teachers can record attendance"
            );
        }

        if(course.getTeacherId() != teacher.getId()){
            throw new IllegalArgumentException(
                "You can only record attendance for courses you teach"
            );
        }

        if(student.getRole() != UserRole.STUDENT){
            throw new IllegalArgumentException(
                "Only students can receive attendance records"
            );
        }

        if(attendanceDate == null){
            throw new IllegalArgumentException(
                "Requires attendance date"
            );
        }

        if(status == null){
            throw new IllegalArgumentException(
                "Requires attendance status"
            );
        }

        if(!enrollmentDao.isEnrolled(
                course.getCourseId(),
                student.getId()
        )){
            throw new IllegalArgumentException(
                "Student is not enrolled"
            );
        }

        List<AttendanceRecord> existingRecords =
                attendanceDao.findByCourseAndDate(
                    course.getCourseId(),
                    attendanceDate
                );

        for(AttendanceRecord existingAttendance : existingRecords){
            if(existingAttendance.getStudentID() == student.getId()){
                existingAttendance.setStatus(status);
                existingAttendance.setNotes(notes);
                existingAttendance.setRecordedBy(teacher.getId());

                if(!attendanceDao.update(existingAttendance)){
                    throw new SQLException(
                        "Attendance not updated"
                    );
                }

                return existingAttendance;
            }
        }

        AttendanceRecord attendance = new AttendanceRecord(
            course.getCourseId(),
            student.getId(),
            teacher.getId(),

            attendanceDate,
            status,
            notes
        );

        attendanceDao.insert(attendance);

        return attendance;
    }

    public List<AttendanceReportRow> getAttendanceReport(
        Course course,
        User teacher,
        Integer studentID,
        LocalDate startDate,
        LocalDate endDate,
        AttendanceStatus status
    ) throws SQLException{
        if(course == null){
            throw new IllegalArgumentException(
                "Select a course"
            );
        }

        if(teacher == null){
            throw new IllegalArgumentException(
                "Log in as a teacher to view attendance reports"
            );
        }

        if(course.getCourseId() <= 0){
            throw new IllegalArgumentException(
                "Course must be saved before viewing reports"
            );
        }

        if(teacher.getId() <= 0){
            throw new IllegalArgumentException(
                "Teacher must be saved before viewing reports"
            );
        }

        if(teacher.getRole() != UserRole.TEACHER){
            throw new IllegalArgumentException(
                "Only teachers can view attendance reports"
            );
        }

        if(course.getTeacherId() != teacher.getId()){
            throw new IllegalArgumentException(
                "You can only view reports for courses you teach"
            );
        }

        if(studentID != null && studentID <= 0){
            throw new IllegalArgumentException(
                "Student ID must be positive integer"
            );
        }

        validateDateRange(startDate, endDate);

        return attendanceDao.findReport(
            course.getCourseId(),
            studentID,
            startDate,
            endDate,
            status
        );
    }

    public void validateDateRange(
        LocalDate startDate,
        LocalDate endDate
    ){
        if(startDate != null
            && endDate != null
            && startDate.isAfter(endDate)){
        throw new IllegalArgumentException(
            "Start date cannot be after end date"
        );
        }
    }

    public AttendanceSummary calculateAttendanceSummary(
        List<AttendanceReportRow> reportRows
    ){
        if(reportRows == null){
            throw new IllegalArgumentException(
                "Attendance report rows cannot be null"
            );
        }

        int presentCount = 0;
        int absentCount = 0;
        int lateCount = 0;
        int excusedCount = 0;

        for(AttendanceReportRow row : reportRows){
            if(row == null){
                throw new IllegalArgumentException(
                    "Attendance report row cannot be null"
                );
            }

            switch(row.getStatus()){
                case PRESENT:
                    presentCount++;
                    break;

                case ABSENT:
                    absentCount++;
                    break;

                case LATE:
                    lateCount++;
                    break;

                case EXCUSED:
                    excusedCount++;
                    break;
            }
        }

        int denominator =
                presentCount
                + lateCount
                + absentCount;

        double attendanceRate = Double.NaN;

        if(denominator > 0){
            attendanceRate =
                (presentCount + lateCount)
                * 100.0
                / denominator;
        }

        return new AttendanceSummary(
            presentCount,
            absentCount,
            lateCount,
            excusedCount,
            attendanceRate
        );
    }

    public boolean deleteAttendance(
        AttendanceRecord attendance
    ) throws SQLException{
        if(attendance == null || attendance.getAttendanceID() <= 0){
            return false;
        }

        return attendanceDao.deleteByID(
            attendance.getAttendanceID()
        );
    }
}