import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Displays one enrolled student and their grades in the Grade Entry table
 *
 * @author Jit Tran
 * @since 08/13/2026
 */
public class GradeEntryRow {
    private final User student;
    private Grade grade;

    private final IntegerProperty studentID;

    private final StringProperty studentName;
    private final StringProperty score;
    private final StringProperty feedback;

    public GradeEntryRow(User student, Grade grade){
        if(student == null){
            throw new IllegalArgumentException(
                    "Grade entry row requires a student"
            );
        }

        this.student = student;

        studentID = new SimpleIntegerProperty(
                this,
                "studentID",
                student.getId()
        );

        studentName = new SimpleStringProperty(
                this,
                "studentName",
                student.getFirstName()
                + " "
                + student.getLastName()
        );

        score = new SimpleStringProperty(
                this,
                "score",
                ""
        );

        feedback = new SimpleStringProperty(
                this,
                "feedback",
                ""
        );

        setGrade(grade);
    }

    public void setGrade(Grade grade){
        this.grade = grade;

        if(grade == null){
            score.set("");
            feedback.set("");
        }else{
            score.set(
                    Double.toString(grade.getScore())
            );
            feedback.set(grade.getFeedback());
        }
    }

    public User getStudent(){
        return student;
    }

    public Grade getGrade(){
        return grade;
    }

    public boolean hasGrade(){
        return grade != null;
    }

    public int getStudentID(){
        return studentID.get();
    }

    public String getStudentName(){
        return studentName.get();
    }

    public String getScore(){
        return score.get();
    }

    public String getFeedback(){
        return feedback.get();
    }

    public IntegerProperty studentIDProperty(){
        return studentID;
    }

    public StringProperty studentNameProperty(){
        return studentName;
    }

    public StringProperty scoreProperty(){
        return score;
    }

    public StringProperty feedbackProperty(){
        return feedback;
    }
}