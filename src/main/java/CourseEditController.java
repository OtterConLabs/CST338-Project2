import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Controls the Add/Edit Course form. The same scene handles both cases: when
 * SceneFactory has a selected course the form loads it and saves an update,
 * and when it does not the form starts empty and saves an insert.
 *
 * <p>All form rules live in CourseValidator, so this controller only moves
 * values between the screen and the DAO.</p>
 *
 * @author Brent Brewington
 * @since 8/6/2026
 */
public class CourseEditController {

    // Title that switches between Add Course and Edit Course.
    @FXML
    private Label formTitleLabel;

    // Course code, for example CST338.
    @FXML
    private TextField codeField;

    // Full course title.
    @FXML
    private TextField nameField;

    // Optional description.
    @FXML
    private TextArea descriptionArea;

    // Teacher owning the course. Only users with the TEACHER role appear here.
    @FXML
    private ComboBox<User> teacherCombo;

    // Shows validation errors inline instead of in a pop-up.
    @FXML
    private Label errorLabel;

    // Teachers shown in the dropdown, loaded live from the users table.
    private final ObservableList<User> teachers = FXCollections.observableArrayList();

    private CourseDao courseDao;
    private UserDao userDao;

    // The course being edited, or null when adding a new one.
    private Course editingCourse;

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
     * Lets a test supply its own data access objects.
     *
     * @param courseDao the course DAO to use
     * @param userDao   the user DAO to use
     */
    void setDaos(CourseDao courseDao, UserDao userDao) {
        this.courseDao = courseDao;
        this.userDao = userDao;
        // A test may inject the DAOs before FXMLLoader injects the controls,
        // in which case initialize() loads the dropdown instead.
        if (teacherCombo != null) {
            loadTeachers();
        }
    }

    /**
     * Called by FXMLLoader after the controls are injected. Loads the teacher
     * dropdown, then fills the form if a course was selected for editing.
     */
    @FXML
    private void initialize() {
        // Show a readable name in the dropdown instead of User.toString().
        teacherCombo.setCellFactory(list -> new TeacherCell());
        teacherCombo.setButtonCell(new TeacherCell());
        teacherCombo.setItems(teachers);

        if (courseDao == null) {
            courseDao = new CourseDao();
        }
        if (userDao == null) {
            userDao = new UserDao();
        }
        loadTeachers();

        editingCourse = SceneFactory.getSelectedCourse();
        if (editingCourse == null) {
            formTitleLabel.setText("Add Course");
        } else {
            formTitleLabel.setText("Edit Course");
            populateForm(editingCourse);
        }
    }

    /**
     * Loads every user whose role is TEACHER into the dropdown. The role check
     * lives in CourseValidator so the same rule is used by the tests.
     */
    private void loadTeachers() {
        if (userDao == null) {
            return;
        }
        List<User> allUsers = userDao.getAllUsers();
        teachers.clear();
        for (User user : allUsers) {
            if (CourseValidator.canOwnCourse(user)) {
                teachers.add(user);
            }
        }
    }

    /**
     * Copies an existing course into the form fields.
     *
     * @param course the course being edited
     */
    private void populateForm(Course course) {
        codeField.setText(course.getCourseCode());
        nameField.setText(course.getCourseName());
        descriptionArea.setText(course.getDescription());

        for (User teacher : teachers) {
            if (teacher.getId() == course.getTeacherId()) {
                teacherCombo.getSelectionModel().select(teacher);
                break;
            }
        }
    }

    /**
     * Handles Save. Validates first, then inserts or updates depending on
     * whether the form was opened with a selected course. On success it
     * returns to the Course List, which reloads from the database.
     */
    @FXML
    private void handleSave() {
        String code = codeField.getText();
        String name = nameField.getText();
        User selectedTeacher = teacherCombo.getSelectionModel().getSelectedItem();
        int teacherId = (selectedTeacher == null) ? 0 : selectedTeacher.getId();

        List<String> errors = CourseValidator.validate(code, name, teacherId);
        if (!errors.isEmpty()) {
            // Keep the user on the form with their typed values intact.
            errorLabel.setText(CourseValidator.toMessage(errors));
            return;
        }

        boolean saved;
        if (editingCourse == null) {
            Course newCourse = new Course(code, name, descriptionArea.getText(), teacherId);
            saved = courseDao.insert(newCourse) > 0;
        } else {
            editingCourse.setCourseCode(code);
            editingCourse.setCourseName(name);
            editingCourse.setDescription(descriptionArea.getText());
            editingCourse.setTeacherId(teacherId);
            saved = courseDao.update(editingCourse);
        }

        if (!saved) {
            // The most common cause is the UNIQUE course code constraint.
            errorLabel.setText("Save failed. That course code may already exist.");
            return;
        }

        returnToCourseList();
    }

    /** Handles Cancel and discards any edits. */
    @FXML
    private void handleCancel() {
        returnToCourseList();
    }

    /** Clears the editing selection and navigates back to the Course List. */
    private void returnToCourseList() {
        SceneFactory.setSelectedCourse(null);
        stage.setScene(SceneFactory.create(SceneType.COURSES, stage));
    }

    /**
     * Renders a User as "First Last" inside the teacher dropdown.
     */
    private static class TeacherCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (empty || user == null) {
                setText(null);
            } else {
                setText(user.getFirstName() + " " + user.getLastName());
            }
        }
    }
}
