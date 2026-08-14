import java.sql.SQLException;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles validation and saving for Grade Entry.
 *
 * @author Jit Tran
 * @since 08/13/2026
 */
public class GradeService{
    private final GradeDao gradeDao;
    private final EnrollmentDao enrollmentDao;

    public GradeService(
            GradeDao gradeDao,
            EnrollmentDao enrollmentDao
    ){
        if(gradeDao == null){
            throw new IllegalArgumentException(
                    "GradeService requires a GradeDao"
            );
        }

        if(enrollmentDao == null){
            throw new IllegalArgumentException(
                    "GradeService requires an EnrollmentDao"
            );
        }

        this.gradeDao = gradeDao;
        this.enrollmentDao = enrollmentDao;
    }

    public double validateScore(
            String scoreText,
            int pointsPossible
    ){
        if(scoreText == null || scoreText.isBlank()){
            throw new IllegalArgumentException(
                    "Score is required"
            );
        }

        if(pointsPossible < 0){
            throw new IllegalArgumentException(
                "Points possible must not be negative"
            );
        }

        double score;

        try{
            score = Double.parseDouble(scoreText.trim());
        }catch(NumberFormatException e){
            throw new IllegalArgumentException(
                    "Score must be a number"
            );
        }

        if(!Double.isFinite(score)){
            throw new IllegalArgumentException(
                    "Score must be a finite number"
            );
        }

        if(score < 0){
            throw new IllegalArgumentException(
                    "Score cannot be negative"
            );
        }

        if(score > pointsPossible){
            throw new IllegalArgumentException(
                    "Score cannot exceed points possible"
            );
        }

        return score;
    }

    public Grade saveGrade(
            Assignment assignment,
            User student,
            String scoreText,
            String feedback
    ) throws SQLException{
        if(assignment == null){
            throw new IllegalArgumentException(
                    "Select an assignment"
            );
        }

        if(student == null){
            throw new IllegalArgumentException(
                    "Select a student"
            );
        }

        if(assignment.getAssignmentId() <= 0){
            throw new IllegalArgumentException(
                    "Assignment must be saved before grading"
            );
        }

        if(student.getId() <= 0){
            throw new IllegalArgumentException(
                    "Student must be saved before grading"
            );
        }

        if(student.getRole() != UserRole.STUDENT){
            throw new IllegalArgumentException(
                    "Only students can receive grades"
            );
        }

        if(!enrollmentDao.isEnrolled(
                assignment.getCourseId(),
                student.getId()
        )){
            throw new IllegalArgumentException(
                    "Student is not enrolled"
            );
        }

        double score = validateScore(
                scoreText,
                assignment.getPointsPossible()
        );

        Optional<Grade> existingGrade =
                gradeDao.findByAssignmentAndStudent(
                        assignment.getAssignmentId(),
                        student.getId()
                );

        if(existingGrade.isPresent()){
            Grade grade = existingGrade.get();

            grade.setScore(score);
            grade.setFeedback(feedback);

            if(!gradeDao.update(grade)){
                throw new SQLException(
                        "Grade not updated"
                );
            }

            return grade;
        }

        Grade grade = new Grade(
                assignment.getAssignmentId(),
                student.getId(),
                score,
                feedback
        );

        gradeDao.insert(grade);

        return grade;
    }

    public GradeStatistics calculateStatistics(
        List<Grade> grades,

        int pointsPossible,
        int enrolledCount
){
    if(grades == null){
        throw new IllegalArgumentException(
            "Grades are required"
        );
    }

    if(pointsPossible <= 0){
        throw new IllegalArgumentException(
            "Points possible must greater than zero"
        );
    }

    if(enrolledCount < 0){
        throw new IllegalArgumentException(
                "Enrolled count must be greater than zero"
        );
    }

    int gradedCount = grades.size();

    int ungradedCount = Math.max(0, enrolledCount - gradedCount);

    if(grades.isEmpty()){
        return new GradeStatistics(
            0,
            ungradedCount,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        );
    }

    List<Double> percentages = new ArrayList<>();

    double total = 0;

    int below60Count = 0;
    int sixtyToSixtyNineCount = 0;
    int seventyToSeventyNineCount = 0;
    int eightyToEightyNineCount = 0;
    int ninetyToOneHundredCount = 0;

    for(Grade grade : grades){
        if(grade == null){
            throw new IllegalArgumentException(
                "Grade list cannot contain null"
            );
        }

        double percentage = grade.getScore() / pointsPossible * 100.0;

        percentages.add(percentage);
        total += percentage;

        if(percentage < 60){
            below60Count++;
        }else if(percentage < 70){
            sixtyToSixtyNineCount++;
        }else if(percentage < 80){
            seventyToSeventyNineCount++;
        }else if(percentage < 90){
            eightyToEightyNineCount++;
        }else{
            ninetyToOneHundredCount++;
        }
    }

    Collections.sort(percentages);

    double mean = total / gradedCount;

    int middle = gradedCount / 2;
    double median;

    if(gradedCount % 2 == 0){
        median = (
            percentages.get(middle - 1) + percentages.get(middle)
        ) 
        
        / 2.0;
    }else{
        median = percentages.get(middle);
    }

    double minimum = percentages.get(0);

    double maximum = percentages.get(percentages.size() - 1);

    return new GradeStatistics(
        gradedCount,
        ungradedCount,
        mean,
        median,
        minimum,
        maximum,
        below60Count,
        sixtyToSixtyNineCount,
        seventyToSeventyNineCount,
        eightyToEightyNineCount,
        ninetyToOneHundredCount
    );
}

    public boolean deleteGrade(Grade grade) throws SQLException{
        if(grade == null || grade.getGradeID() <= 0){
            return false;
        }

        return gradeDao.deleteByID(grade.getGradeID());
    }
}