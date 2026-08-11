import java.time.LocalDate;
import java.util.Objects;

/**
 * Record of one student's attendance for one course for one date
 *
 * @author Jit Tran
 * @since 08/10/2026
 */
public class AttendanceRecord {
    private int attendanceID;
    private int courseID;
    private int studentID;
    private int recordedBy;

    private LocalDate attendanceDate;
    private AttendanceStatus status;
   
    private String notes;
    private String updatedAt;

    /**
     * Creates a new attendance record
     */
    public AttendanceRecord(
            int courseID,
            int studentID,
            int recordedBy,

            LocalDate attendanceDate,
            AttendanceStatus status,
            
            String notes
    ) {
        this.attendanceID = 0;
        setCourseID(courseID);
        setStudentID(studentID);
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
            int attendanceID,
            int courseID,
            int studentID,
            int recordedBy,

            LocalDate attendanceDate,
            AttendanceStatus status,

            String notes,
            String updatedAt
    ){
        setAttendanceID(attendanceID);
        setCourseID(courseID);
        setStudentID(studentID);
        setRecordedBy(recordedBy);

        setAttendanceDate(attendanceDate);
        setStatus(status);

        setNotes(notes);
        this.updatedAt = updatedAt;
    }

    public void setAttendanceID(int attendanceID){
        if (attendanceID < 0){
            throw new IllegalArgumentException(
                    "Attendance ID must be positive integer"
            );
        }

        this.attendanceID = attendanceID;
    }

    public void setCourseID(int courseID){
        if (courseID <= 0){
            throw new IllegalArgumentException(
                    "Course ID must be positive integer"
            );
        }

        this.courseID = courseID;
    }

    public void setStudentID(int studentID){
        if (studentID <= 0){
            throw new IllegalArgumentException(
                    "Student ID must be positive integer"
            );
        }

        this.studentID = studentID;
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

    public int getAttendanceID() {
        return attendanceID;
    }

    public int getCourseID() {
        return courseID;
    }

    public int getStudentID() {
        return studentID;
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

        return getAttendanceID() == record.getAttendanceID()
                && getCourseID() == record.getCourseID()
                && getStudentID() == record.getStudentID()
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
                getAttendanceID(),
                getCourseID(),
                getStudentID(),
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
                + "attendanceID=" + attendanceID
                + ", courseID=" + courseID
                + ", studentID=" + studentID
                + ", recordedBy=" + recordedBy
                + ", attendanceDate=" + attendanceDate
                + ", status=" + status
                + ", notes='" + notes + '\''
                + ", updatedAt='" + updatedAt + '\''
                + '}';
    }
}