import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Predicate;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Controls the Course List screen. Courses load from CourseDao into an
 * ObservableList that backs the TableView. The columns bind to the Course
 * JavaFX properties, so a cell repaints the moment its value changes, which is
 * the live-data enhancement chosen for this slice.
 *
 * <p>A search box filters the table live through a FilteredList, and a
 * SortedList keeps column sorting working on top of the filter. This is the
 * second, extra-credit enhancement layered on the ObservableList.</p>
 *
 * @author Brent Brewington
 * @since 8/7/2026
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

    // Seat-limit column: shows the number, or "Unlimited" when capacity is 0.
    @FXML
    private TableColumn<Course, String> capacityColumn;

    // Live filter box for the table.
    @FXML
    private TextField searchField;

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

    // Master list loaded from the database. Changing this list updates the screen.
    private final ObservableList<Course> courses = FXCollections.observableArrayList();

    // View of the master list that hides rows not matching the search box.
    private FilteredList<Course> filteredCourses;

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
     * Called by FXMLLoader after the controls are injected. Binds each column
     * to a Course property, wraps the list in a FilteredList and SortedList so
     * search and sorting work together, disables the row actions until
     * something is selected, and loads the data.
     */
    @FXML
    private void initialize() {
        // Bind to the JavaFX properties directly so edits show live.
        codeColumn.setCellValueFactory(cell -> cell.getValue().courseCodeProperty());
        nameColumn.setCellValueFactory(cell -> cell.getValue().courseNameProperty());
        teacherColumn.setCellValueFactory(cell -> cell.getValue().teacherNameProperty());

        // Capacity is display text: "Unlimited" reads better than 0.
        if (capacityColumn != null) {
            capacityColumn.setCellValueFactory(cell -> {
                Course course = cell.getValue();
                String text = course.hasCapacityLimit()
                        ? String.valueOf(course.getCapacity())
                        : "Unlimited";
                return new ReadOnlyStringWrapper(text);
            });
        }

        // FilteredList feeds a SortedList, which is what the TableView shows.
        filteredCourses = new FilteredList<>(courses, course -> true);
        SortedList<Course> sortedCourses = new SortedList<>(filteredCourses);
        sortedCourses.comparatorProperty().bind(courseTable.comparatorProperty());
        courseTable.setItems(sortedCourses);
        courseTable.setPlaceholder(new Label("No courses yet. Select Add to create one."));

        // Update the filter whenever the search text changes.
        if (searchField != null) {
            searchField.textProperty().addListener(
                    (observable, oldText, newText) -> applyFilter(newText));
        }

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
     * Narrows the table to rows whose code, name, or teacher contains the
     * search text, ignoring case. A blank search shows every course.
     *
     * @param search the current text in the search box
     */
    private void applyFilter(String search) {
        if (filteredCourses == null) {
            return;
        }
        String needle = (search == null) ? "" : search.trim().toLowerCase();

        Predicate<Course> matches = course -> {
            if (needle.isEmpty()) {
                return true;
            }
            return contains(course.getCourseCode(), needle)
                    || contains(course.getCourseName(), needle)
                    || contains(course.getTeacherName(), needle);
        };
        filteredCourses.setPredicate(matches);

        int shown = filteredCourses.size();
        courseMessageLabel.setText(needle.isEmpty()
                ? shown + " course(s)."
                : shown + " course(s) match \"" + search.trim() + "\".");
    }

    /**
     * Null-safe case-insensitive contains check.
     *
     * @param value  the text to search in, may be null
     * @param needle the already lower-cased search text
     * @return true if value contains needle
     */
    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase().contains(needle);
    }

    /**
     * Reloads every course for the logged-in teacher into the master list so
     * the TableView always reflects what is actually stored, then reapplies
     * the current search filter.
     */
    private void refreshTable() {
        User teacher = SceneFactory.getLoggedInUser();
        courses.setAll(courseDao.findByTeacherId(teacher.getId()));
        applyFilter(searchField == null ? "" : searchField.getText());
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
