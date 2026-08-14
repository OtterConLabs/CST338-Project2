import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controls the Attendance Roster scene.
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
public class AttendanceRosterController{
    private Stage stage;

    private CourseDao courseDao;
    private EnrollmentDao enrollmentDao;
    private AttendanceDao attendanceDao;
    private AttendanceService attendanceService;

    private final ObservableList<Course> courses =
            FXCollections.observableArrayList();

    private final ObservableList<AttendanceRosterRow> attendanceRows =
            FXCollections.observableArrayList();

    @FXML
    private ComboBox<Course> courseComboBox;

    @FXML
    private DatePicker attendanceDatePicker;

    @FXML
    private TableView<AttendanceRosterRow> attendanceTable;

    @FXML
    private TableColumn<AttendanceRosterRow, Number> studentIDColumn;

    @FXML
    private TableColumn<AttendanceRosterRow, String> studentNameColumn;

    @FXML
    private TableColumn<AttendanceRosterRow, String> statusColumn;

    @FXML
    private TableColumn<AttendanceRosterRow, String> notesColumn;

    @FXML
    private ComboBox<AttendanceStatus> statusComboBox;

    @FXML
    private TextField notesField;

    @FXML
    private Button applyButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Label attendanceMessageLabel;

    @FXML
    private void initialize(){
        courseComboBox.setItems(courses);

        statusComboBox.setItems(
            FXCollections.observableArrayList(
                AttendanceStatus.values()
            )
        );

        attendanceTable.setItems(attendanceRows);

        configureCourseComboBox();

        studentIDColumn.setCellValueFactory(
            cell -> cell.getValue().studentIDProperty()
        );

        studentNameColumn.setCellValueFactory(
            cell -> cell.getValue().studentNameProperty()
        );

        statusColumn.setCellValueFactory(
            cell -> cell.getValue().statusTextProperty()
        );

        notesColumn.setCellValueFactory(
            cell -> cell.getValue().notesProperty()
        );

        courseComboBox.valueProperty().addListener(
            (observable, oldCourse, newCourse)
                -> handleRosterContextChanged()
        );

        attendanceDatePicker.valueProperty().addListener(
            (observable, oldDate, newDate)
                 -> handleRosterContextChanged()
        );

        attendanceTable.getSelectionModel()
            .selectedItemProperty()
            .addListener(
                (observable, oldRow, newRow)
                    -> displaySelectedRow(newRow)
            );

        clearEditor();
        initializeDependencies();
        loadCourses();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    void setDependencies(
        CourseDao courseDao,
        EnrollmentDao enrollmentDao,
        AttendanceDao attendanceDao
    ){
        if(courseDao == null
            || enrollmentDao == null
            || attendanceDao == null){
            throw new IllegalArgumentException(
                 "Attendance roster dependencies can not be null"
            );
        }

        this.courseDao = courseDao;
        this.enrollmentDao = enrollmentDao;
        this.attendanceDao = attendanceDao;

        attendanceService = new AttendanceService(
            attendanceDao,
            enrollmentDao
        );

        if(courseComboBox != null){
            loadCourses();
        }
    }

    private void initializeDependencies(){
        if(courseDao != null
            && enrollmentDao != null
            && attendanceDao != null){
                if(attendanceService == null){
                    attendanceService = new AttendanceService(
                    attendanceDao,
                    enrollmentDao
                );
            }

            return;
        }

        Connection connection = DatabaseManager
            .getInstance()
            .getConnection();

        courseDao = new CourseDao(connection);
        enrollmentDao = new EnrollmentDao(connection);
        attendanceDao = new AttendanceDao(connection);

        attendanceService = new AttendanceService(
            attendanceDao,
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

    private void loadCourses(){
        courses.clear();
        clearRoster();

        User teacher = SceneFactory.getLoggedInUser();

        if(teacher == null
            || teacher.getRole() != UserRole.TEACHER
            || teacher.getId() <= 0){
                attendanceMessageLabel.setText(
                    "Log in as a teacher to record attendance"
                );
            
            return;
        }

        courses.setAll(
            courseDao.findByTeacherId(
                teacher.getId()
            )
        );

        if(courses.isEmpty()){
            attendanceMessageLabel.setText(
                "No courses are available"
            );
            
            return;
        }

        courseComboBox.getSelectionModel().selectFirst();

        attendanceMessageLabel.setText(
            "Select a date, then load a roster"
        );
    }

    private void handleRosterContextChanged(){
        clearRoster();

        attendanceMessageLabel.setText(
            "Select a course and date, then load a roster"
        );
    }

    @FXML
    private void handleLoadRoster(){
        loadRoster(
            courseComboBox.getValue(),
            attendanceDatePicker.getValue()
        );
    }

    private void loadRoster(
        Course course,
        LocalDate attendanceDate
    ){
        clearRoster();

        if(course == null){
            attendanceMessageLabel.setText("Select a course");
            return;
        }

        if(attendanceDate == null){
            attendanceMessageLabel.setText(
                "Select an attendance date"
            );
            return;
        }

        try{
            List<User> students = enrollmentDao.findEnrolledStudents(course.getCourseId());

            List<AttendanceRecord> records =
                    attendanceDao.findByCourseAndDate(
                        course.getCourseId(),
                        attendanceDate
                    );

            Map<Integer, AttendanceRecord> recordsByStudent =
                    new HashMap<>();

            for(AttendanceRecord record : records){
                recordsByStudent.put(
                    record.getStudentID(),
                    record
                );
            }

            for(User student : students){
                attendanceRows.add(
                    new AttendanceRosterRow(
                        student,
                        recordsByStudent.get(student.getId())
                    )
                );
            }

            saveButton.setDisable(attendanceRows.isEmpty());

            if(attendanceRows.isEmpty()){
                attendanceMessageLabel.setText(
                     "No students are enrolled in this course"
                );
            }else{
                attendanceMessageLabel.setText(
                    "Select a student to record attendance"
                );
            }
        }catch(SQLException e){
            attendanceMessageLabel.setText(
                "Could not load attendance: "
                + e.getMessage()
            );
        }
    }

    private void clearRoster(){
        attendanceRows.clear();
        clearEditor();
        saveButton.setDisable(true);
    }

    private void displaySelectedRow(AttendanceRosterRow row){
        if(row == null){
            statusComboBox.setValue(null);
            notesField.clear();

            statusComboBox.setDisable(true);
            notesField.setDisable(true);
            applyButton.setDisable(true);
            deleteButton.setDisable(true);

            return;
        }

        statusComboBox.setDisable(false);
        notesField.setDisable(false);
        applyButton.setDisable(false);

        statusComboBox.setValue(row.getStatus());
        notesField.setText(row.getNotes());

        deleteButton.setDisable(!row.hasAttendance());
    }

    private void clearEditor(){
        attendanceTable.getSelectionModel().clearSelection();

        statusComboBox.setValue(null);
        notesField.clear();

        statusComboBox.setDisable(true);
        notesField.setDisable(true);
        applyButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @FXML
    private void handleApply(){
        if(!applySelectedRow()){
            return;
        }

        AttendanceRosterRow row = attendanceTable
             .getSelectionModel()
            .getSelectedItem();

        attendanceMessageLabel.setText(
            "Updated attendance for " + row.getStudentName()
        );
    }

    private boolean applySelectedRow(){
        AttendanceRosterRow row = attendanceTable
            .getSelectionModel()
            .getSelectedItem();
        if(row == null){
            attendanceMessageLabel.setText("Select a student");
            
            return false;
        }

        AttendanceStatus status = statusComboBox.getValue();

        if(status == null){
            attendanceMessageLabel.setText(
                "Select an attendance status"
            );

            return false;
        }

        row.setStatus(status);
        row.setNotes(notesField.getText());

        attendanceTable.refresh();

        return true;
    }

    @FXML
    private void handleMarkAllPresent(){
        if(attendanceRows.isEmpty()){
            attendanceMessageLabel.setText(
                "Load a roster before marking attendance"
            );
            return;
        }

        for(AttendanceRosterRow row : attendanceRows){
            row.setStatus(AttendanceStatus.PRESENT);
        }

        AttendanceRosterRow selectedRow = attendanceTable
            .getSelectionModel()
            .getSelectedItem();

        if(selectedRow != null){
            statusComboBox.setValue(AttendanceStatus.PRESENT);
        }

        attendanceTable.refresh();

        attendanceMessageLabel.setText(
            "Marked all students present"
        );
    }

    @FXML
    private void handleSave(){
        if(attendanceRows.isEmpty()){
            attendanceMessageLabel.setText(
                "Load a roster before saving attendance"
            );
            return;
        }

        AttendanceRosterRow selectedRow = attendanceTable
            .getSelectionModel()
            .getSelectedItem();

        if(selectedRow != null && !applySelectedRow()){
            return;
        }

        if(!allRowsHaveStatus()){
            return;
        }

        List<AttendanceRosterRow> changedRows =
            new ArrayList<>();

        for(AttendanceRosterRow row : attendanceRows){
            if(row.isChanged()){
                changedRows.add(row);
            }
        }

        if(changedRows.isEmpty()){
            attendanceMessageLabel.setText(
                "No attendance changes to save"
            );
            return;
        }

        if(hasExistingChanges(changedRows) && !confirmOverwrite()){
            attendanceMessageLabel.setText("Save canceled");
            
            return;
        }

        Course course = courseComboBox.getValue();

        LocalDate attendanceDate = attendanceDatePicker.getValue();

        User teacher = SceneFactory.getLoggedInUser();

        try{
            for(AttendanceRosterRow row : changedRows){
                AttendanceRecord savedAttendance =
                    attendanceService.saveAttendance(
                        course,
                        row.getStudent(),
                        teacher,
                        attendanceDate,
                        row.getStatus(),
                        row.getNotes()
                        );

                row.setAttendance(savedAttendance);
            }

            attendanceTable.refresh();

            attendanceMessageLabel.setText(
                "Saved attendance for "
                + changedRows.size()
                + " student(s)"
            );

            showSaveAlert(changedRows.size());

        }catch(IllegalArgumentException e){
            attendanceMessageLabel.setText(e.getMessage());

        }catch(SQLException e){
            attendanceMessageLabel.setText(
                "Could not save attendance: "
                + e.getMessage()
            );
        }
    }

    private boolean allRowsHaveStatus(){
        for(AttendanceRosterRow row : attendanceRows){
            if(row.getStatus() == null){
                attendanceMessageLabel.setText(
                    "Select a status for "
                    + row.getStudentName()
                );
                return false;
            }
        }

        return true;
    }

    private boolean hasExistingChanges(
            List<AttendanceRosterRow> changedRows
    ){
        for(AttendanceRosterRow row : changedRows){
            if(row.hasAttendance() && row.isChanged()){
                return true;
            }
        }

        return false;
    }

    private boolean confirmOverwrite(){
        Alert confirmation = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Attendance records already exist. "
            + "Overwrite the changed records?",
            ButtonType.YES,
            ButtonType.CANCEL
        );

        confirmation.setTitle("Attendance");
        confirmation.setHeaderText("Confirm overwrite");

        ButtonType choice = confirmation.showAndWait()
            .orElse(ButtonType.CANCEL);

        return choice == ButtonType.YES;
    }

    private void showSaveAlert(int savedCount){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Attendance");
        alert.setHeaderText(null);
        alert.setContentText(
            "Saved attendance for "
            + savedCount
            + " student(s)."
        );

        alert.showAndWait();
    }

    @FXML
    private void handleReset(){
        if(attendanceRows.isEmpty()){
            attendanceMessageLabel.setText(
               "Load a roster before resetting changes"
            );
            return;
        }

        for(AttendanceRosterRow row : attendanceRows){
            row.setAttendance(row.getAttendance());
        }

        attendanceTable.refresh();
        clearEditor();

        attendanceMessageLabel.setText("Changes reset");
    }

    @FXML
    private void handleDelete(){
        AttendanceRosterRow row = attendanceTable
            .getSelectionModel()
            .getSelectedItem();

        if(row == null || !row.hasAttendance()){
            attendanceMessageLabel.setText(
                "Select a saved attendance record to delete"
            );
            return;
        }

        Alert confirmation = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Delete attendance for "
            + row.getStudentName()
            + "?",
            ButtonType.OK,
            ButtonType.CANCEL
        );

        confirmation.setTitle("Attendance");
        confirmation.setHeaderText("Confirm delete");

        ButtonType choice = confirmation.showAndWait()
            .orElse(ButtonType.CANCEL);

        if(choice != ButtonType.OK){
            attendanceMessageLabel.setText("Delete canceled");
            return;
        }

        try{
            if(!attendanceService.deleteAttendance(
                row.getAttendance()
            )){
                attendanceMessageLabel.setText(
                    "Attendance was not deleted"
                );
                return;
            }

            row.setAttendance(null);
            attendanceTable.refresh();
            displaySelectedRow(row);

            attendanceMessageLabel.setText(
                "Deleted attendance for "
                + row.getStudentName()
            );

        }catch(SQLException e){
            attendanceMessageLabel.setText(
                "Could not delete attendance: "
                + e.getMessage()
            );
        }
    }

    @FXML
    private void handleViewReport(){
        if(stage == null){
            throw new IllegalStateException("Stage not set.");
        }

        stage.setScene(
            SceneFactory.createAttendanceReportScene(stage)
        );
    }

    @FXML
    private void handleBack(){
        if(stage == null){
            throw new IllegalStateException("Stage not set.");
        }

        stage.setScene(
            SceneFactory.create(
                SceneType.DASHBOARD,
                stage
            )
        );
    }
}