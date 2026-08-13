import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * Database Operations for Grade.java
 * 
 * @author Jit Tran
 * @since 08/05/2026
 */
public class GradeDao {
    private final Connection connection;

    public GradeDao(Connection connection){
        if(connection == null){
            throw new IllegalArgumentException("GradeDao requires an open connection");
        }

        this.connection = connection;
    }

    public int insert(Grade grade) throws SQLException{
        requireGrade(grade);

        String sql = """
                     INSERT INTO grades (
                        assignment_id,
                        student_id,
                        score,
                        feedback
                     )
                
                     VALUES (?, ?, ?, ?)
                     """;
        
        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setInt(1, grade.getAssignmentID());
            statement.setInt(2, grade.getStudentID());
            statement.setDouble(3, grade.getScore());
            statement.setString(4, grade.getFeedback());

            int affectedRows = statement.executeUpdate();

            if(affectedRows != 1){
                throw new SQLException(
                    "Grade insertion affected "
                    + affectedRows
                    + " rows"
                );
            }

        try(ResultSet generatedKeys = statement.getGeneratedKeys()){
            if(!generatedKeys.next()){
                throw new SQLException(
                    "Grade insertion error: no ID returned"
                );
            }

            int generatedID = generatedKeys.getInt(1);
            grade.setGradeID(generatedID);
            return generatedID;
        }

    }
}   
    public Optional<Grade> findByID(int gradeID) throws SQLException{
            if(gradeID <= 0){
                return Optional.empty();
            }

            String sql = """
                    SELECT
                        grade_id,
                        assignment_id,
                        student_id,
                        score,
                        feedback,
                        graded_at,
                        updated_at
                    FROM grades
                    WHERE grade_id = ?
                    """;
            
            try(PreparedStatement statement =
                connection.prepareStatement(sql)){
                    statement.setInt(1, gradeID);
                

                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        return Optional.of(mapGrade(resultSet));
                    }

                    return Optional.empty();
                }
            }
        }

        public List<Grade> findbyAssignmentID(int assignmentID) throws SQLException{
                if(assignmentID <= 0){
                    throw new IllegalArgumentException("Assignment ID must be greater than zero");
                }
            
            String sql = """
                    SELECT
                        grade_id,
                        assignment_id,
                        student_id,
                        score,
                        feedback,
                        graded_at,
                        updated_at
                    FROM grades
                    WHERE assignment_id = ?
                    ORDER BY student_id
                    """;
            
            List<Grade> grades = new ArrayList<>();

            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, assignmentID);

                try(ResultSet resultSet = statement.executeQuery()){
                    while(resultSet.next()){
                        grades.add(mapGrade(resultSet));
                    }
                }

                return grades;
            }
        }

        public Optional<Grade> findByAssignmentAndStudent(
                int assignmentID,
                int studentID
        ) throws SQLException{
            if(assignmentID <= 0 || studentID <= 0){
                throw new IllegalArgumentException(
                        "Assignment and Student IDs must be positive integers"
                );
            }

            String sql = """
                         SELECT
                            grade_id,
                            assignment_id,
                            student_id,
                            score,
                            feedback,
                            graded_at,
                            updated_at
                         FROM grades
                         WHERE assignment_id = ? AND student_id = ?
                         """;

            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.setInt(1, assignmentID);
                statement.setInt(2, studentID);

                try(ResultSet resultSet = statement.executeQuery()){
                    if(resultSet.next()){
                        return Optional.of(mapGrade(resultSet));
                    }

                    return Optional.empty();
                }
            }
        }

            public boolean update(Grade grade) throws SQLException{
                requireGrade(grade);

                if(grade.getGradeID() <= 0){
                    throw new IllegalArgumentException("Grade must have an ID before it can be updated");
                }

                String sql = """
                             UPDATE grades
                             SET
                                score = ?,
                                feedback = ?,
                                updated_at = datetime('now')
                             WHERE grade_id = ?
                             """;
                
                try(PreparedStatement statement = connection.prepareStatement(sql)){
                    statement.setDouble(1, grade.getScore());
                    
                    statement.setString(2, grade.getFeedback());
                   
                    statement.setInt(3, grade.getGradeID());

                    return statement.executeUpdate() == 1;
                }
            }

            public boolean deleteByID(int gradeID) throws SQLException{
                if(gradeID <= 0){
                    return false;
                }

                String sql = """
                             DELETE FROM grades
                             WHERE grade_id = ?
                             """;
                
                try(PreparedStatement statement = connection.prepareStatement(sql)){
                    statement.setInt(1, gradeID);
                return statement.executeUpdate() == 1;
                }
        }
    
    private Grade mapGrade(ResultSet resultSet) throws SQLException{
        return new Grade(
                resultSet.getInt("grade_id"),
                resultSet.getInt("assignment_id"),
                resultSet.getInt("student_id"),
                resultSet.getDouble("score"),
                resultSet.getString("feedback"),
                resultSet.getString("graded_at"),
                resultSet.getString("updated_at"));
    }

    private void requireGrade(Grade grade){
        if(grade == null){
            throw new IllegalArgumentException("Grade cannot be null");
        }
    }
}