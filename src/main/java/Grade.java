import java.util.Objects;

/**
 * OtterconLabs Project 2 - Slice 4: Grades & Statistics
 * Grade is a student's score for 1 assignment
 * 
 * @author Jit Tran
 * @since 08/04/2026
 */
public class Grade {
    private int gradeID;
    private int assignmentID;
    private int studentID;

    private double score;

    private String feedback;
    private String gradedAt;
    private String updatedAt;

    public Grade(int assignmentID,
                 int studentID,
                 double score,
                 String feedback
    ){
        this.gradeID = 0;
        setAssignmentID(assignmentID);
        setStudentID(studentID);
        setScore(score);
        setFeedback(feedback);
        this.gradedAt = null;
        this.updatedAt = null;
    }

    public Grade(
        int gradeID,
        int assignmentID,
        int studentID,
        
        double score,

        String feedback,
        String gradedAt,
        String updatedAt
    ){
        setGradeID(gradeID);
        setAssignmentID(assignmentID);
        setStudentID(studentID);
        setScore(score);
        setFeedback(feedback);

        this.gradedAt = gradedAt;
        this.updatedAt = updatedAt;
    }

    public void setGradeID(int gradeID){
        if(gradeID < 0){
            throw new IllegalArgumentException("Grade ID cannot be negative");
        }

        this.gradeID = gradeID;
    }

    public void setAssignmentID(int assignmentID){
        if(assignmentID <= 0){
            throw new IllegalArgumentException("Assignment ID must be greater than zero");
        }

        this.assignmentID = assignmentID;
    }

    public void setStudentID(int studentID){
        if(studentID <= 0){
            throw new IllegalArgumentException("Student ID must be greater than zero");
        }

        this.studentID = studentID;
    }

    public void setScore(double score){
        if(score < 0 || Double.isNaN(score) || Double.isInfinite(score)){
            throw new IllegalArgumentException(
                    "Score must be a valid number zero or greater"
            );
        }
    
        this.score = score;
    }

    public void setFeedback(String feedback){
        if(feedback == null){
            this.feedback = "";
        } else {
            this.feedback = feedback.trim();
        }
    }

    public int getGradeID(){
        return gradeID;
    }

    public int getAssignmentID(){
        return assignmentID;
    }

    public int getStudentID(){
        return studentID;
    }

    public double getScore(){
        return score;
    }

    public String getFeedback(){
        return feedback;
    }

    public String getGradedAt(){
        return gradedAt;
    }

    public String getUpdatedAt(){
        return updatedAt;
    }

@Override
public boolean equals(Object object){
    if(!(object instanceof Grade grade)){
        return false;
    }

    return getGradeID() == grade.getGradeID() &&
      getAssignmentID() == grade.getAssignmentID() &&
         getStudentID() == grade.getStudentID() &&
        Double.compare(getScore(), grade.getScore()) == 0
        && Objects.equals(
            getFeedback(),
            grade.getFeedback()
        )
        && Objects.equals(
            getGradedAt(),
            grade.getGradedAt()
        )
        && Objects.equals(
            getUpdatedAt(),
            grade.getUpdatedAt()
        );
}

@Override
public int hashCode(){
    return Objects.hash(
        getGradeID(),
        getAssignmentID(),
        getStudentID(),
        getScore(),
        getFeedback(),
        getGradedAt(),
        getUpdatedAt()
    );
}

@Override
public String toString(){
    return "Grade{"
         + "gradeID=" + gradeID
         + ", assignmentID=" + assignmentID
         + ", studentID=" + studentID
         + ", score=" + score
         + ", feedback=" + feedback + '\''
         + ", gradedAt=" + gradedAt + '\''
         + ", updatedAt=" + updatedAt +'\''
         + '}';
}

}
