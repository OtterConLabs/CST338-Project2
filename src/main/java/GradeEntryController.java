import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls the Grade Entry scene.
 *
 * @author Jit Tran
 * @since 08/04/2026
 */
public class GradeEntryController{
    private Stage stage;

    private CourseDao courseDao;
    private AssignmentDao assignmentDao;
    private EnrollmentDao enrollmentDao;
    private GradeDao gradeDao;
    private GradeService gradeService;

    private final ObservableList<Course> courses =
        FXCollections.observableArrayList();

    private final ObservableList<Assignment> assignments =
        FXCollections.observableArrayList();

    private final ObservableList<GradeEntryRow> gradeRows =
        FXCollections.observableArrayList();

    @FXML
    private ComboBox<Course> courseComboBox;

    @FXML
    private ComboBox<Assignment> assignmentComboBox;

    @FXML
    private TableView<GradeEntryRow> gradeTable;

    @FXML
    private TableColumn<GradeEntryRow, Number> studentIDColumn;

    @FXML
    private TableColumn<GradeEntryRow, String> studentNameColumn;

    @FXML
    private TableColumn<GradeEntryRow, String> scoreColumn;

    @FXML
    private TableColumn<GradeEntryRow, String> feedbackColumn;

    @FXML
    private TextField scoreField;

    @FXML
    private TextField feedbackField;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label gradeMessageLabel;

    @FXML
    private void initialize(){
        courseComboBox.setItems(courses);
        assignmentComboBox.setItems(assignments);
        gradeTable.setItems(gradeRows);

        configureCourseComboBox();
        configureAssignmentComboBox();

        studentIDColumn.setCellValueFactory(
            cell -> cell.getValue().studentIDProperty()
        );

        studentNameColumn.setCellValueFactory(
            cell -> cell.getValue().studentNameProperty()
        );

        scoreColumn.setCellValueFactory(
            cell -> cell.getValue().scoreProperty()
        );

        feedbackColumn.setCellValueFactory(
            cell -> cell.getValue().feedbackProperty()
        );

        courseComboBox.valueProperty().addListener(
            (observable, oldCourse, newCourse) 
            -> loadAssignments(newCourse)
        );

        assignmentComboBox.valueProperty().addListener(
            (observable, oldAssignment, newAssignment) 
            -> loadRoster(newAssignment)
        );

        gradeTable.getSelectionModel()
                  .selectedItemProperty()
                  .addListener(
                    (observable, oldRow, newRow) 
                    -> displaySelectedRow(newRow)
                );

        saveButton.disableProperty().bind(
                gradeTable.getSelectionModel()
                          .selectedItemProperty()
                          .isNull()
        );

        deleteButton.setDisable(true);

        initializeDependencies();
        loadCourses();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    void setDependencies(
        CourseDao courseDao,
        AssignmentDao assignmentDao,
        EnrollmentDao enrollmentDao,
        GradeDao gradeDao
    ){
        if(courseDao == null
            || assignmentDao == null
            || enrollmentDao == null
            || gradeDao == null){
            throw new IllegalArgumentException(
                "Grade Entry dependencies cannot be null"
            );
        }

        this.courseDao = courseDao;
        this.assignmentDao = assignmentDao;
        this.enrollmentDao = enrollmentDao;
        this.gradeDao = gradeDao;

        gradeService = new GradeService(
            gradeDao,
            enrollmentDao
        );

        if(courseComboBox != null){
            loadCourses();
        }
    }

    private void initializeDependencies(){
        if(courseDao != null
            && assignmentDao != null
               && enrollmentDao != null
             && gradeDao != null){
                if(gradeService == null){
                    gradeService = new GradeService(
                    gradeDao,
                    enrollmentDao
                );
            }

            return;
        }

        Connection connection = DatabaseManager
            .getInstance()
            .getConnection();

        courseDao = new CourseDao(connection);
        assignmentDao = new AssignmentDao(connection);
        enrollmentDao = new EnrollmentDao(connection);
        gradeDao = new GradeDao(connection);

        gradeService = new GradeService(
            gradeDao,
            enrollmentDao
        );
    }

    private void configureCourseComboBox(){
        courseComboBox.setCellFactory(
            listView -> createCourseCell()
        );

        courseComboBox.setButtonCell(
            createCourseCell()
        );
    }

    private ListCell<Course> createCourseCell(){
        return new ListCell<>(){
            @Override
            protected void updateItem(
                Course course,
                boolean empty
            ){
                super.updateItem(course, empty);

                if(empty || course == null){
                    setText(null);
                    return;
                }

                setText(
                    course.getCourseCode()
                    + " - "
                    + course.getCourseName()
                );
            }
        };
    }

    private void configureAssignmentComboBox(){
        assignmentComboBox.setCellFactory(
            listView -> createAssignmentCell()
        );

        assignmentComboBox.setButtonCell(
            createAssignmentCell()
        );
    }

    private ListCell<Assignment> createAssignmentCell(){
        return new ListCell<>(){
            @Override
            protected void updateItem(
                Assignment assignment,
                boolean empty
            ){
                super.updateItem(assignment, empty);

                if(empty || assignment == null){
                    setText(null);
                    return;
                }

                setText(
                    assignment.getTitle()
                    + " ("
                    + assignment.getPointsPossible()
                    + " points)"
                );
            }
        };
    }

    private void loadCourses(){
        courses.clear();
        assignments.clear();
        gradeRows.clear();
        clearEditor();

        User teacher = SceneFactory.getLoggedInUser();

        if(teacher == null
            || teacher.getRole() != UserRole.TEACHER
            || teacher.getId() <= 0){
            gradeMessageLabel.setText(
                "Log in as a teacher to enter grades"
            );

            return;
        }

        courses.setAll(
            courseDao.findByTeacherId(
                teacher.getId()
            )
        );

        if(courses.isEmpty()){
            gradeMessageLabel.setText(
                "No courses are available"
            );

            return;
        }

        courseComboBox.getSelectionModel().selectFirst();
    }

    private void loadAssignments(Course course){
        assignments.clear();
        gradeRows.clear();
        clearEditor();

        if(course == null){
            gradeMessageLabel.setText(
                "Select a course"
            );

            return;
        }

        try{
            assignments.setAll(
                assignmentDao.findByCourseId(
                    course.getCourseId()
                )
            );

            if(assignments.isEmpty()){
                gradeMessageLabel.setText(
                    "No assignments are available for this course"
                );

                return;
            }

            assignmentComboBox
                .getSelectionModel()
                .selectFirst();

        }catch(SQLException e){
            gradeMessageLabel.setText(
                "Could not load assignments: "
                + e.getMessage()
            );
        }
    }

    private void loadRoster(Assignment assignment){
        gradeRows.clear();
        clearEditor();

        if(assignment == null){
            gradeMessageLabel.setText(
                "Select an assignment"
            );

            return;
        }

        try{
            List<User> students =
                enrollmentDao.findEnrolledStudents(
                assignment.getCourseId()
            );

            List<Grade> grades =
                gradeDao.findbyAssignmentID(
                assignment.getAssignmentId()
            );

            Map<Integer, Grade> gradesByStudent = new HashMap<>();

            for(Grade grade : grades){
                gradesByStudent.put(
                    grade.getStudentID(),
                    grade
                );
            }

            for(User student : students){
                gradeRows.add(
                    new GradeEntryRow(
                        student,
                        gradesByStudent.get(
                            student.getId()
                        )
                    )
                );
            }

            if(gradeRows.isEmpty()){
                gradeMessageLabel.setText(
                    "No students are enrolled in this course"
                );
            }else{
                gradeMessageLabel.setText(
                    "Select a student to enter grade"
                );
            }

        }catch(SQLException e){
            gradeMessageLabel.setText(
                "Could not load grades: "
                + e.getMessage()
            );
        }
    }

    private void displaySelectedRow(GradeEntryRow row){
        if(row == null){
            scoreField.clear();
            feedbackField.clear();
            deleteButton.setDisable(true);

            return;
        }

        scoreField.setText(
            row.getScore()
        );

        feedbackField.setText(
            row.getFeedback()
        );

        deleteButton.setDisable(
            !row.hasGrade()
        );
    }

    private void clearEditor(){
        gradeTable.getSelectionModel()
                  .clearSelection();

        scoreField.clear();
        feedbackField.clear();
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleSave(){
        GradeEntryRow row = gradeTable
            .getSelectionModel()
            .getSelectedItem();

        Assignment assignment =
            assignmentComboBox.getValue();

        if(row == null){
            gradeMessageLabel.setText(
                "Select a student"
            );

            return;
        }

        try{
            Grade savedGrade =
                gradeService.saveGrade(
                    assignment,
                    row.getStudent(),
                    scoreField.getText(),
                    feedbackField.getText()
                );

            row.setGrade(savedGrade);
            deleteButton.setDisable(false);

            gradeMessageLabel.setText(
                 "Saved grade for "
                    + row.getStudentName()
            );

        }catch(IllegalArgumentException e){
            gradeMessageLabel.setText(
                e.getMessage()
            );

        }catch(SQLException e){
            gradeMessageLabel.setText(
                "Could not save grade: "
                + e.getMessage()
            );
        }
    }

    @FXML
    private void handleDelete(){
        GradeEntryRow row = gradeTable
            .getSelectionModel()
            .getSelectedItem();

        if(row == null || !row.hasGrade()){
            gradeMessageLabel.setText(
                "Select a saved grade to delete"
            );

            return;
        }

        try{
            if(!gradeService.deleteGrade(
                row.getGrade()
            )){
                gradeMessageLabel.setText(
                    "Grade was not deleted"
                );

                return;
            }

            String studentName = row.getStudentName();

            row.setGrade(null);

            gradeTable.refresh();
            gradeTable.getSelectionModel().clearSelection();
            
            scoreField.clear();
            feedbackField.clear();
            
            deleteButton.setDisable(true);
            
            gradeMessageLabel.setText(
                "Deleted grade for "
                + studentName
                + ". Student remains in roster."
            );

        }catch(SQLException e){
            gradeMessageLabel.setText(
                "Could not delete grade: " + e.getMessage()
            );
        }
    }

    @FXML
    private void handleStatistics(){
        Assignment assignment = assignmentComboBox.getValue();
    
        if(assignment == null){
            gradeMessageLabel.setText("Select an assignment");
    
            return;
        }
    
        if(stage == null){
            throw new IllegalStateException("Stage not set");
        }
    
        stage.setScene(
            SceneFactory.createGradeStatisticsScene(stage, assignment)

        );
    }

    @FXML
    private void handleBack(){
        if(stage == null){
            throw new IllegalStateException(
                "Stage not set."
            );
        }

        stage.setScene(
                SceneFactory.create(
                    SceneType.DASHBOARD,
                    stage
                )
        );
    }
}