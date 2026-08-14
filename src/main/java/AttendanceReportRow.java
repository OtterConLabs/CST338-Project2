import java.time.LocalDate;

/**
 * One attendance record holding names for Attendance Report
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
public class AttendanceReportRow{
    private final AttendanceRecord attendance;

    private final String studentName;
    private final String recordedByName;

    public AttendanceReportRow(
        AttendanceRecord attendance,
            
        String studentName,
        String recordedByName
    ){
        if(attendance == null){
            throw new IllegalArgumentException(
                "Attendance report row requires attendance"
            );
        }

        this.attendance = attendance;
        this.studentName = studentName == null ? "" : studentName;
        this.recordedByName = recordedByName == null ? "" : recordedByName;
    }

    public AttendanceRecord getAttendance(){
        return attendance;
    }

    public int getAttendanceID(){
        return attendance.getAttendanceID();
    }

    public int getCourseID(){
        return attendance.getCourseID();
    }

    public int getStudentID(){
        return attendance.getStudentID();
    }

    public LocalDate getAttendanceDate(){
        return attendance.getAttendanceDate();
    }

    public AttendanceStatus getStatus(){
        return attendance.getStatus();
    }

    public String getNotes(){
        return attendance.getNotes();
    }

    public String getStudentName(){
        return studentName;
    }

    public String getRecordedByName(){
        return recordedByName;
    }
}