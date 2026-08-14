import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Display one enrolled student in the Attendance Roster table
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
public class AttendanceRosterRow {
    private final User student;

    private AttendanceRecord attendance;
    private AttendanceStatus status;

    private final IntegerProperty studentID;

    private final StringProperty studentName;
    private final StringProperty statusText;
    private final StringProperty notes;

    public AttendanceRosterRow( User student, AttendanceRecord attendance){
        if(student == null){
            throw new IllegalArgumentException(
                "Attendance roster row requires a student"
            );
        }

        this.student = student;

        studentID = new SimpleIntegerProperty(
            this, "studentID", student.getId()
        );

        studentName = new SimpleStringProperty(
            this,
            "studentName"
            , student.getFirstName()
            + " "
            + student.getLastName()
        );

        statusText = new SimpleStringProperty(
            this, "statusText", ""
        );

        notes = new SimpleStringProperty(
            this, "notes", ""
        );

        setAttendance(attendance);
    }

    public void setAttendance(AttendanceRecord attendance){
        this.attendance = attendance;

        if(attendance == null){
            setStatus(null);
            setNotes("");
            
            return;
        }

        setStatus(attendance.getStatus());
        setNotes(attendance.getNotes());
    }

    public void setStatus(AttendanceStatus status){
        this.status = status;

        if(status == null){
            statusText.set("");

            return;
        }

        statusText.set(status.name());
    }

    public void setNotes(String notes){
        if(notes == null){
            this.notes.set("");

            return;
        }

        this.notes.set(notes.trim());
    }

    public User getStudent(){
        return student;
    }

    public AttendanceRecord getAttendance(){
        return attendance;
    }

    public AttendanceStatus getStatus(){
        return status;
    }

    public String getNotes(){
        return notes.get();
    }

    public boolean hasAttendance(){
        return attendance != null;
    }

    public boolean isChanged(){
        if(attendance == null){
            return status != null || !getNotes().isBlank();
        }

        return status != attendance.getStatus() || !getNotes().equals(attendance.getNotes());
    }

    public int getStudentID(){
        return studentID.get();
    }

    public String getStudentName(){
        return studentName.get();
    }

    public String getStatusText(){
        return statusText.get();
    }

    public IntegerProperty studentIDProperty(){
        return studentID;
    }

    public StringProperty studentNameProperty(){
        return studentName;
    }

    public StringProperty statusTextProperty(){
        return statusText;
    }

    public StringProperty notesProperty(){
        return notes;
    }
}