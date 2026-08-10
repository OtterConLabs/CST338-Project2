import java.time.LocalDate;
import java.util.Objects;

/**
 * Record of one student's attendance for one course for one date
 *
 * @author Jit Tran
 * @since 08/10/2026
 */
public class AttendanceRecord {
    private int attendanceId;
    private int courseId;
    private int studentId;
    private int recordedBy;

    private LocalDate attendanceDate;
    private AttendanceStatus status;
   
    private String notes;
    private String updatedAt;

    /**
     * Creates a new attendance record
     */
    public AttendanceRecord(
            int courseId,
            int studentId,
            int recordedBy,

            LocalDate attendanceDate,
            AttendanceStatus status,
            
            String notes
    ) {
        this.attendanceId = 0;
        setCourseId(courseId);
        setStudentId(studentId);
        setRecordedBy(recordedBy);

        setAttendanceDate(attendanceDate);
        setStatus(status);

        setNotes(notes);
        this.updatedAt = null;
    }

    /**
     * Creates an attendance record read from the database.
     */
    public AttendanceRecord(
            int attendanceId,
            int courseId,
            int studentId,
            int recordedBy,

            LocalDate attendanceDate,
            AttendanceStatus status,

            String notes,
            String updatedAt
    ){
        setAttendanceId(attendanceId);
        setCourseId(courseId);
        setStudentId(studentId);
        setRecordedBy(recordedBy);

        setAttendanceDate(attendanceDate);
        setStatus(status);

        setNotes(notes);
        this.updatedAt = updatedAt;
    }

    public void setAttendanceId(int attendanceId){
        if (attendanceId < 0){
            throw new IllegalArgumentException(
                    "Attendance ID must be positive integer"
            );
        }

        this.attendanceId = attendanceId;
    }

    public void setCourseId(int courseId){
        if (courseId <= 0){
            throw new IllegalArgumentException(
                    "Course ID must be positive integer"
            );
        }

        this.courseId = courseId;
    }

    public void setStudentId(int studentId){
        if (studentId <= 0){
            throw new IllegalArgumentException(
                    "Student ID must be positive integer"
            );
        }

        this.studentId = studentId;
    }

    public void setRecordedBy(int recordedBy) {
        if (recordedBy <= 0){
            throw new IllegalArgumentException(
                    "Recorded by ID must be positive integer"
            );
        }

        this.recordedBy = recordedBy;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        if (attendanceDate == null){
            throw new IllegalArgumentException(
                    "Failed: Null attendance date"
            );
        }

        this.attendanceDate = attendanceDate;
    }

    public void setStatus(AttendanceStatus status) {
        if (status == null){
            throw new IllegalArgumentException(
                    "Failed: Null attendance status"
            );
        }

        this.status = status;
    }

    public void setNotes(String notes) {
        if (notes == null){
            this.notes = "";
        }else{
            this.notes = notes.trim();
        }
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getRecordedBy() {
        return recordedBy;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AttendanceRecord record)) {
            return false;
        }

        return getAttendanceId() == record.getAttendanceId()
                && getCourseId() == record.getCourseId()
                && getStudentId() == record.getStudentId()
                && getRecordedBy() == record.getRecordedBy()
                && Objects.equals(
                        getAttendanceDate(),
                        record.getAttendanceDate()
                )
                && getStatus() == record.getStatus()
                && Objects.equals(getNotes(), record.getNotes())
                && Objects.equals(
                        getUpdatedAt(),
                        record.getUpdatedAt()
                );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getAttendanceId(),
                getCourseId(),
                getStudentId(),
                getRecordedBy(),
                getAttendanceDate(),
                getStatus(),
                getNotes(),
                getUpdatedAt()
        );
    }

    @Override
    public String toString() {
        return "AttendanceRecord{"
                + "attendanceId=" + attendanceId
                + ", courseId=" + courseId
                + ", studentId=" + studentId
                + ", recordedBy=" + recordedBy
                + ", attendanceDate=" + attendanceDate
                + ", status=" + status
                + ", notes='" + notes + '\''
                + ", updatedAt='" + updatedAt + '\''
                + '}';
    }
}