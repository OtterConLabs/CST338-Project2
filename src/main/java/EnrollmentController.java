import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Controls the Manage Enrollment screen. Two ListViews are backed by
 * ObservableLists, so enrolling or unenrolling a student updates both lists
 * immediately. This is the live-data enhancement applied to the slice.
 *
 * <p>The course being managed is supplied by SceneFactory, the same way the
 * logged-in user is shared across scenes.</p>
 *
 * @author Brent Brewington
 * @since 8/6/2026
 */
public class EnrollmentController {

    // Shows which course is being managed.
    @FXML
    private Label courseLabel;

    // Students who are not yet enrolled in this course.
    @FXML
    private ListView<User> availableList;

    // Students who are enrolled in this course.
    @FXML
    private ListView<User> enrolledList;

    @FXML
    private Button enrollButton;

    @FXML
    private Button unenrollButton;

    // Shows status and rule messages inline.
    @FXML
    private Label enrollmentMessageLabel;

    // Backing lists. Updating these updates the screen.
    private final ObservableList<User> available = FXCollections.observableArrayList();
    private final ObservableList<User> enrolled = FXCollections.observableArrayList();

    private EnrollmentDao enrollmentDao;

    // The course passed in from the Course List screen.
    private Course course;

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
     * Lets a test supply its own DAO and course instead of reading the
     * selection from SceneFactory.
     *
     * @param enrollmentDao the DAO to use
     * @param course        the course being managed
     */
    void setDependencies(EnrollmentDao enrollmentDao, Course course) {
        this.enrollmentDao = enrollmentDao;
        this.course = course;
        // A test may inject these before FXMLLoader injects the controls,
        // in which case initialize() does the first refresh instead.
        if (availableList != null) {
            refreshLists();
        }
    }

    /**
     * Called by FXMLLoader after the controls are injected. Binds both lists,
     * disables the transfer buttons until a student is selected, and loads the
     * roster for the selected course.
     */
    @FXML
    private void initialize() {
        availableList.setCellFactory(list -> new StudentCell());
        enrolledList.setCellFactory(list -> new StudentCell());

        availableList.setItems(available);
        enrolledList.setItems(enrolled);

        availableList.setPlaceholder(new Label("No students available."));
        enrolledList.setPlaceholder(new Label("No students enrolled yet."));

        // A transfer only makes sense when something is selected on that side.
        enrollButton.disableProperty()
                .bind(availableList.getSelectionModel().selectedItemProperty().isNull());
        unenrollButton.disableProperty()
                .bind(enrolledList.getSelectionModel().selectedItemProperty().isNull());

        if (enrollmentDao == null) {
            enrollmentDao = new EnrollmentDao();
        }
        if (course == null) {
            course = SceneFactory.getSelectedCourse();
        }

        if (course == null) {
            courseLabel.setText("No course selected.");
            enrollmentMessageLabel.setText("Return to the course list and select a course.");
            return;
        }

        courseLabel.setText("Enrollment for "
                + course.getCourseCode() + " - " + course.getCourseName());
        refreshLists();
    }

    /**
     * Reloads both rosters from the database so the screen always matches what
     * is actually stored.
     */
    private void refreshLists() {
        if (enrollmentDao == null || course == null) {
            return;
        }
        available.setAll(enrollmentDao.findAvailableStudents(course.getCourseId()));
        enrolled.setAll(enrollmentDao.findEnrolledStudents(course.getCourseId()));
    }

    /**
     * Handles Enroll. Guards are checked in order: a student must be selected,
     * the selected user must be a student, and they must not already be
     * enrolled. Only after those pass is the insert attempted, so a -1 returned
     * by enroll() now means a real failure rather than a duplicate, and the
     * message reflects that distinction (addresses PR feedback on overloaded -1).
     */
    @FXML
    private void handleEnroll() {
        User selected = availableList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            enrollmentMessageLabel.setText("Select a student to enroll.");
            return;
        }

        if (!CourseValidator.canEnroll(selected)) {
            enrollmentMessageLabel.setText("Only students can be enrolled in a course.");
            return;
        }

        // Distinguish the duplicate case up front so the -1 from enroll() below
        // can be treated purely as an insert failure.
        if (enrollmentDao.isEnrolled(course.getCourseId(), selected.getId())) {
            enrollmentMessageLabel.setText(selected.getFirstName()
                    + " is already enrolled in this course.");
            return;
        }

        int enrollmentId = enrollmentDao.enroll(
                new Enrollment(course.getCourseId(), selected.getId()));

        if (enrollmentId > 0) {
            refreshLists();
            enrollmentMessageLabel.setText("Enrolled "
                    + selected.getFirstName() + " " + selected.getLastName() + ".");
        } else {
            enrollmentMessageLabel.setText("Could not enroll "
                    + selected.getFirstName() + ". Please try again.");
        }
    }

    /** Handles Unenroll and removes the selected student from the course. */
    @FXML
    private void handleUnenroll() {
        User selected = enrolledList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            enrollmentMessageLabel.setText("Select a student to remove.");
            return;
        }

        if (enrollmentDao.unenroll(course.getCourseId(), selected.getId())) {
            refreshLists();
            enrollmentMessageLabel.setText("Removed "
                    + selected.getFirstName() + " " + selected.getLastName() + ".");
        } else {
            enrollmentMessageLabel.setText("Unable to remove that student.");
        }
    }

    /** Handles Back and returns to the Course List. */
    @FXML
    private void handleBack() {
        SceneFactory.setSelectedCourse(null);
        stage.setScene(SceneFactory.create(SceneType.COURSES, stage));
    }

    /**
     * Renders a User as "Last, First (username)" in both list views.
     */
    private static class StudentCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (empty || user == null) {
                setText(null);
            } else {
                setText(user.getLastName() + ", " + user.getFirstName()
                        + " (" + user.getUsername() + ")");
            }
        }
    }
}
