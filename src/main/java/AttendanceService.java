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