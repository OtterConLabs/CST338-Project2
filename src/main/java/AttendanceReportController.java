import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Controls the Attendance Report scene.
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
public class AttendanceReportController{
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
        DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private Stage stage;

    private CourseDao courseDao;
    private EnrollmentDao enrollmentDao;
    private AttendanceDao attendanceDao;
    private AttendanceService attendanceService;

    private final ObservableList<Course> courses =
        FXCollections.observableArrayList();

    private final ObservableList<User> students =
        FXCollections.observableArrayList();

    private final ObservableList<AttendanceReportRow> reportRows =
        FXCollections.observableArrayList();

    @FXML
    private ComboBox<Course> courseComboBox;

    @FXML
    private ComboBox<User> studentComboBox;

    @FXML
    private ComboBox<AttendanceStatus> statusComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableView<AttendanceReportRow> reportTable;

    @FXML
    private TableColumn<AttendanceReportRow, String> dateColumn;

    @FXML
    private TableColumn<AttendanceReportRow, String> studentColumn;

    @FXML
    private TableColumn<AttendanceReportRow, String> statusColumn;

    @FXML
    private TableColumn<AttendanceReportRow, String> notesColumn;

    @FXML
    private TableColumn<AttendanceReportRow, String> recordedByColumn;

    @FXML
    private Label presentCountLabel;

    @FXML
    private Label absentCountLabel;

    @FXML
    private Label lateCountLabel;

    @FXML
    private Label excusedCountLabel;

    @FXML
    private Label attendanceRateLabel;

    @FXML
    private Label reportMessageLabel;

    @FXML
    private void initialize(){
        courseComboBox.setItems(courses);
        studentComboBox.setItems(students);

        statusComboBox.setItems(
            FXCollections.observableArrayList(
                AttendanceStatus.values()
            )
        );

        reportTable.setItems(reportRows);

        configureCourseComboBox();
        configureStudentComboBox();

        dateColumn.setCellValueFactory(
            cell -> new ReadOnlyStringWrapper(
                cell.getValue()
                .getAttendanceDate()
                .format(DISPLAY_DATE_FORMAT)
            )
        );

        studentColumn.setCellValueFactory(
            cell -> new ReadOnlyStringWrapper(
                cell.getValue().getStudentName()
            )
        );

        statusColumn.setCellValueFactory(
            cell -> new ReadOnlyStringWrapper(
                cell.getValue().getStatus().name()
            )
        );

        notesColumn.setCellValueFactory(
            cell -> new ReadOnlyStringWrapper(
                cell.getValue().getNotes()
            )
        );

        recordedByColumn.setCellValueFactory(
            cell -> new ReadOnlyStringWrapper(
                cell.getValue().getRecordedByName()
            )
        );

        courseComboBox.valueProperty().addListener(
            (observable, oldCourse, newCourse) ->
                handleCourseChanged(newCourse)
        );

        initializeDependencies();
        clearReport();
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
            "Attendance report dependencies cannot be null"
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

        courseComboBox.setButtonCell(createCourseCell());
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

    private void configureStudentComboBox(){
        studentComboBox.setCellFactory(
            listView -> createStudentCell()
        );

        studentComboBox.setButtonCell(createStudentCell());
    }

    private ListCell<User> createStudentCell(){
        return new ListCell<>(){
            @Override
            protected void updateItem(
                User student,
                boolean empty
            ){
                super.updateItem(student, empty);

                if(empty || student == null){
                    setText(null);
                    return;
                }

                setText(
                    student.getFirstName()
                    + " "
                    + student.getLastName()
                );
            }
        };
    }

    private void loadCourses(){
        courses.clear();
        students.clear();
        clearReport();

        User teacher = SceneFactory.getLoggedInUser();

        if(teacher == null
            || teacher.getRole() != UserRole.TEACHER
            || teacher.getId() <= 0){
            reportMessageLabel.setText(
                "Log in as a teacher to view attendance reports"
            );
            return;
        }

        courses.setAll(
            courseDao.findByTeacherId(teacher.getId())
        );

        if(courses.isEmpty()){
            reportMessageLabel.setText(
                "No courses are available"
            );
            
            return;
        }

        courseComboBox.getSelectionModel().selectFirst();

        reportMessageLabel.setText(
            "Set optional filters, then apply filters"
        );
    }

    private void handleCourseChanged(Course course){
        students.clear();
        studentComboBox.getSelectionModel().clearSelection();
        clearReport();

        if(course == null){
            reportMessageLabel.setText("Select a course");
            return;
        }

        students.setAll(
                enrollmentDao.findEnrolledStudents(
                    course.getCourseId()
                )
        );

        reportMessageLabel.setText(
            "Set optional filters, then apply filters"
        );
    }

    @FXML
    private void handleApplyFilters(){
        Course course = courseComboBox.getValue();
        User teacher = SceneFactory.getLoggedInUser();
        User selectedStudent = studentComboBox.getValue();

        Integer studentID = null;

        if(selectedStudent != null){
            studentID = selectedStudent.getId();
        }

        try{
            List<AttendanceReportRow> rows =
                    attendanceService.getAttendanceReport(
                        course,
                        teacher,
                        studentID,

                        startDatePicker.getValue(),
                        endDatePicker.getValue(),
                        statusComboBox.getValue()
                    );

            reportRows.setAll(rows);

            AttendanceSummary summary =
                attendanceService.calculateAttendanceSummary(rows);

            displaySummary(summary);

            if(reportRows.isEmpty()){
                reportMessageLabel.setText(
                    "No attendance records match these filters"
                );
            }else{
                reportMessageLabel.setText(
                    "Showing "
                    + reportRows.size()
                    + " attendance record(s)"
                );
            }
        }catch(IllegalArgumentException e){
            clearReport();
            reportMessageLabel.setText(e.getMessage());
        }catch(SQLException e){
            clearReport();
            reportMessageLabel.setText(
                "Could not load attendance report: "
                + e.getMessage()
            );
        }
    }

    private void displaySummary(AttendanceSummary summary){
        presentCountLabel.setText(
            Integer.toString(summary.getPresentCount())
        );

        absentCountLabel.setText(
            Integer.toString(summary.getAbsentCount())
        );

        lateCountLabel.setText(
            Integer.toString(summary.getLateCount())
        );

        excusedCountLabel.setText(
            Integer.toString(summary.getExcusedCount())
        );

        attendanceRateLabel.setText(
            summary.getAttendanceRateText()
        );
    }

    @FXML
    private void handleClear(){
        studentComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        clearReport();

        reportMessageLabel.setText("Filters cleared");
    }

    private void clearReport(){
        reportRows.clear();

        presentCountLabel.setText("0");
        absentCountLabel.setText("0");
        lateCountLabel.setText("0");
        excusedCountLabel.setText("0");
        attendanceRateLabel.setText("N/A");
    }

    @FXML
    private void handleBack(){
        if(stage == null){
            throw new IllegalStateException("Stage not set.");
        }

        stage.setScene(
            SceneFactory.create(SceneType.ATTENDANCE, stage)
        );
    }
}