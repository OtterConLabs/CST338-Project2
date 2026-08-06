import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Controls the Course List screen. It loads courses from CourseDao into an
 * ObservableList that backs the TableView, which is the live-data enhancement
 * chosen for this slice, and routes to the Add/Edit form and the Manage
 * Enrollment screen.
 *
 * @author Brent Brewington
 * @since 8/6/2026
 */
public class CourseListController {

    // Table that displays one row per course.
    @FXML
    private TableView<Course> courseTable;

    // Course code column, for example CST338.
    @FXML
    private TableColumn<Course, String> codeColumn;

    // Full course title column.
    @FXML
    private TableColumn<Course, String> nameColumn;

    // Teacher name column, joined in by CourseDao.
    @FXML
    private TableColumn<Course, String> teacherColumn;

    // Buttons that act on the selected row.
    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button manageEnrollmentButton;

    // Shows status and validation messages without a pop-up.
    @FXML
    private Label courseMessageLabel;

    // Backing list for the TableView. Changing this list updates the screen.
    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    // Data access for this slice.
    private CourseDao courseDao;

    // Primary stage, supplied by SceneFactory, used for scene navigation.
    private Stage stage;

    /**
     * Stores the primary application Stage for scene navigation.
     *
     * @param stage the stage created in Main
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Lets a test supply its own DAO. The app uses the default DAO created in
     * initialize().
     *
     * @param courseDao the DAO the controller should use
     */
    void setCourseDao(CourseDao courseDao) {
        this.courseDao = courseDao;
        // A test may inject the DAO before FXMLLoader injects the controls,
        // in which case initialize() does the first refresh instead.
        if (courseTable != null) {
            refreshTable();
        }
    }

    /**
     * Called by FXMLLoader after the controls are injected. Wires each column
     * to a Course getter, binds the ObservableList to the TableView, disables
     * the row actions until something is selected, and loads the data.
     */
    @FXML
    private void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        courseTable.setItems(courses);
        courseTable.setPlaceholder(new Label("No courses yet. Select Add to create one."));

        // Row actions only make sense when a row is selected.
        editButton.disableProperty()
                .bind(courseTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty()
                .bind(courseTable.getSelectionModel().selectedItemProperty().isNull());
        manageEnrollmentButton.disableProperty()
                .bind(courseTable.getSelectionModel().selectedItemProperty().isNull());

        if (courseDao == null) {
            courseDao = new CourseDao();
        }
        refreshTable();
    }

    /**
     * Reloads every course from the database into the ObservableList so the
     * TableView always reflects what is actually stored.
     */
    private void refreshTable() {
        courses.setAll(courseDao.findAll());
        courseMessageLabel.setText(courses.size() + " course(s) loaded.");
    }

    /** Handles Refresh and reloads the table from the database. */
    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    /**
     * Handles Add. Clearing the selection tells the form to insert a new
     * course rather than update an existing one.
     */
    @FXML
    private void handleAdd() {
        SceneFactory.setSelectedCourse(null);
        stage.setScene(SceneFactory.create(SceneType.COURSE_EDIT, stage));
    }

    /** Handles Edit and opens the form preloaded with the selected course. */
    @FXML
    private void handleEdit() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            courseMessageLabel.setText("Select a course to edit.");
            return;
        }
        SceneFactory.setSelectedCourse(selected);
        stage.setScene(SceneFactory.create(SceneType.COURSE_EDIT, stage));
    }

    /**
     * Handles Delete. Deleting a course also removes its enrollment rows
     * through ON DELETE CASCADE, so the user is asked to confirm first.
     */
    @FXML
    private void handleDelete() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            courseMessageLabel.setText("Select a course to delete.");
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete " + selected.getCourseCode()
                        + "? This also removes every enrollment for the course.",
                ButtonType.OK, ButtonType.CANCEL
        );
        confirm.setHeaderText("Confirm delete");

        ButtonType choice = confirm.showAndWait().orElse(ButtonType.CANCEL);
        if (choice != ButtonType.OK) {
            courseMessageLabel.setText("Delete canceled.");
            return;
        }

        if (courseDao.delete(selected.getCourseId())) {
            refreshTable();
            courseMessageLabel.setText("Deleted " + selected.getCourseCode() + ".");
        } else {
            courseMessageLabel.setText("Unable to delete " + selected.getCourseCode() + ".");
        }
    }

    /** Handles Manage Enrollment and opens the roster for the selected course. */
    @FXML
    private void handleManageEnrollment() {
        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            courseMessageLabel.setText("Select a course to manage enrollment.");
            return;
        }
        SceneFactory.setSelectedCourse(selected);
        stage.setScene(SceneFactory.create(SceneType.ENROLLMENT, stage));
    }

    /** Handles Back and returns to the Dashboard. */
    @FXML
    private void handleBack() {
        stage.setScene(SceneFactory.create(SceneType.DASHBOARD, stage));
    }
}
