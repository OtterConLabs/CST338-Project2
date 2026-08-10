import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controls the Assignment form screen and handles creating
 * or editing an Assignment.
 *
 * @author Jordan Browning
 * @since 8/2/2026
 */
public class AssignmentFormController
{
    // Zero means the form was opened without a Course already selected.
    private static final int NO_COURSE_ID = 0;

    // Message displayed when the database does not contain any Courses.
    private static final String NO_COURSES_MESSAGE =
            "No courses are available. Add a course before creating an assignment.";

    private Stage stage;

    private Assignment assignment;

    // NO_COURSE_ID means the Assignment list was showing every Course.
    private int returnCourseId = NO_COURSE_ID;

    // Provides Course data used by the Assignment form.
    private CourseDao courseDao = new CourseDao();

    @FXML
    private Label formTitleLabel;

    @FXML
    private Label formMessageLabel;

    @FXML
    private ComboBox<Course> courseComboBox;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private TextField pointsField;

    @FXML
    private void initialize()
    {
        formMessageLabel.setText("");
        configureCourseComboBox();
        loadCourses();
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    /**
     * Replaces the CourseDao used by this controller.
     * This allows tests to provide a different CourseDao.
     *
     * @param courseDao The CourseDao used to retrieve Courses.
     * @throws IllegalArgumentException If the CourseDao is null.
     */
    public void setCourseDao(CourseDao courseDao)
    {
        if (courseDao == null)
        {
            throw new IllegalArgumentException("CourseDao cannot be null.");
        }

        this.courseDao = courseDao;

        // Reload the ComboBox when the controller has already been created from FXML.
        if (courseComboBox != null)
        {
            loadCourses();
        }
    }

    private void configureCourseComboBox()
    {
        courseComboBox.setCellFactory(
                listView -> createCourseCell()
        );

        courseComboBox.setButtonCell(
                createCourseCell()
        );
    }

    private ListCell<Course> createCourseCell()
    {
        return new ListCell<>()
        {
            @Override
            protected void updateItem(
                    Course course,
                    boolean empty
            )
            {
                super.updateItem(course, empty);

                if (empty || course == null)
                {
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

    private void loadCourses()
    {
        List<Course> courses = courseDao.findAll();

        courseComboBox.setItems(
                FXCollections.observableArrayList(courses)
        );

        if (courses.isEmpty())
        {
            formMessageLabel.setText(NO_COURSES_MESSAGE);
        }
        else
        {
            formMessageLabel.setText("");
        }
    }

    public void setCourseId(int courseId)
    {
        // This method selects an actual Course, so zero is not a valid Course ID.
        if (courseId <= NO_COURSE_ID)
        {
            throw new IllegalArgumentException(
                    "Course ID must be greater than zero."
            );
        }

        selectCourseById(courseId);
    }

    public void prepareForAdd()
    {
        prepareForAdd(NO_COURSE_ID);
    }

    public void prepareForAdd(int activeCourseId)
    {
        // NO_COURSE_ID is allowed here because it means no Course was selected
        // before opening the Assignment form.
        if (activeCourseId < NO_COURSE_ID)
        {
            throw new IllegalArgumentException(
                    "Course ID cannot be negative."
            );
        }

        assignment = null;
        returnCourseId = activeCourseId;
        formTitleLabel.setText("Add Assignment");
        clearFields();

        if (activeCourseId > NO_COURSE_ID)
        {
            setCourseId(activeCourseId);
        }
    }

    public void prepareForEdit(Assignment assignment)
    {
        prepareForEdit(assignment, NO_COURSE_ID);
    }

    public void prepareForEdit(
            Assignment assignment,
            int activeCourseId
    )
    {
        if (assignment == null)
        {
            throw new IllegalArgumentException(
                    "Assignment cannot be null."
            );
        }

        // NO_COURSE_ID is allowed because the previous list may have shown every Course.
        if (activeCourseId < NO_COURSE_ID)
        {
            throw new IllegalArgumentException(
                    "Course ID cannot be negative."
            );
        }

        this.assignment = assignment;
        returnCourseId = activeCourseId;
        formTitleLabel.setText("Edit Assignment");
        displayAssignment();
    }

    private void selectCourseById(int courseId)
    {
        for (Course course : courseComboBox.getItems())
        {
            if (course.getCourseId() == courseId)
            {
                courseComboBox.getSelectionModel().select(course);
                return;
            }
        }

        formMessageLabel.setText(
                "The Assignment's Course could not be found."
        );
    }

    private void displayAssignment()
    {
        if (assignment == null)
        {
            return;
        }

        selectCourseById(assignment.getCourseId());

        titleField.setText(assignment.getTitle());
        descriptionArea.setText(assignment.getDescription());
        dueDatePicker.setValue(assignment.getDueDate());
        pointsField.setText(
                String.valueOf(
                        assignment.getPointsPossible()
                )
        );
    }

    private void clearFields()
    {
        courseComboBox.getSelectionModel().clearSelection();
        titleField.clear();
        descriptionArea.clear();
        dueDatePicker.setValue(null);
        pointsField.clear();

        if (courseComboBox.getItems().isEmpty())
        {
            formMessageLabel.setText(NO_COURSES_MESSAGE);
        }
        else
        {
            formMessageLabel.setText("");
        }
    }

    @FXML
    private void handleSave()
    {
        Course selectedCourse = courseComboBox.getValue();

        if (selectedCourse == null)
        {
            formMessageLabel.setText(
                    "Select a course for the Assignment."
            );
            return;
        }

        int selectedCourseId = selectedCourse.getCourseId();

        if (selectedCourseId <= NO_COURSE_ID)
        {
            formMessageLabel.setText(
                    "Unable to save because the Course ID is invalid."
            );
            return;
        }

        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate dueDate = dueDatePicker.getValue();
        String pointsText = pointsField.getText().trim();

        if (title.isEmpty())
        {
            formMessageLabel.setText(
                    "Assignment title is required."
            );
            return;
        }

        if (dueDate == null)
        {
            formMessageLabel.setText(
                    "Assignment due date is required."
            );
            return;
        }

        if (pointsText.isEmpty())
        {
            formMessageLabel.setText(
                    "Points possible is required."
            );
            return;
        }

        int pointsPossible;

        try
        {
            pointsPossible = Integer.parseInt(pointsText);
        }
        catch (NumberFormatException e)
        {
            formMessageLabel.setText(
                    "Points possible must be a whole number."
            );
            return;
        }

        if (pointsPossible < 0)
        {
            formMessageLabel.setText(
                    "Points possible cannot be negative."
            );
            return;
        }

        try
        {
            AssignmentDao assignmentDao = new AssignmentDao(
                    DatabaseManager.getInstance().getConnection()
            );

            if (assignment == null)
            {
                Assignment newAssignment = new Assignment(
                        selectedCourseId,
                        title,
                        description,
                        dueDate,
                        pointsPossible
                );

                assignmentDao.insert(newAssignment);
            }
            else
            {
                Assignment updatedAssignment = new Assignment(
                        assignment.getAssignmentId(),
                        selectedCourseId,
                        title,
                        description,
                        dueDate,
                        pointsPossible
                );

                boolean updated = assignmentDao.update(updatedAssignment);

                if (!updated)
                {
                    formMessageLabel.setText(
                            "Unable to update the Assignment."
                    );
                    return;
                }

                assignment = updatedAssignment;
            }

            returnToAssignmentList();
        }
        catch (IllegalArgumentException e)
        {
            formMessageLabel.setText(e.getMessage());
        }
        catch (SQLException e)
        {
            formMessageLabel.setText(
                    "Unable to save the Assignment: "
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void handleCancel()
    {
        returnToAssignmentList();
    }

    private void returnToAssignmentList()
    {
        if (stage == null)
        {
            return;
        }

        if (returnCourseId > NO_COURSE_ID)
        {
            stage.setScene(
                    SceneFactory.createAssignmentsScene(
                            stage,
                            returnCourseId
                    )
            );
            return;
        }

        stage.setScene(
                SceneFactory.createAssignmentsScene(stage)
        );
    }
}
