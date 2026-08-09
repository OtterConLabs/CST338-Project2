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
    //Stores the primary Stage used for scene navigation
    private Stage stage;

    //Stores the Assignment being edited
    //The value remains null when creating a new Assignment
    private Assignment assignment;

    //Stores the course ID that owns the Assignment
    private int courseId;

    //Stores the course filter used before the form was opened
    //Zero means the Assignment list was showing every course
    private int returnCourseId;

    //Displays whether the form is adding or editing an Assignment
    @FXML
    private Label formTitleLabel;

    //Displays validation or database messages
    @FXML
    private Label formMessageLabel;

    //Allows the user to choose which Course owns the Assignment
    @FXML
    private ComboBox<Course> courseComboBox;

    //Accepts the Assignment title
    @FXML
    private TextField titleField;

    //Accepts the optional Assignment description
    @FXML
    private TextArea descriptionArea;

    //Accepts the Assignment due date
    @FXML
    private DatePicker dueDatePicker;

    //Accepts the maximum points possible
    @FXML
    private TextField pointsField;

    /**
     * Prepares the Assignment form after the FXML file is loaded.
     */
    @FXML
    private void initialize()
    {
        //The form starts without an error message
        formMessageLabel.setText("");

        //Display each Course using its code and name instead of Course.toString()
        configureCourseComboBox();

        //Retrieve the available Courses from the database
        loadCourses();
    }

    /**
     * Stores the primary application Stage used for scene navigation.
     *
     * @param stage The Stage used to display application scenes.
     */
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    /**
     * Sets how each Course appears inside the ComboBox.
     */
    private void configureCourseComboBox()
    {
        courseComboBox.setCellFactory(
                listView -> createCourseCell()
        );

        courseComboBox.setButtonCell(
                createCourseCell()
        );
    }

    /**
     * Creates a Course cell that displays the Course code and name.
     *
     * @return A ListCell used by the Course ComboBox.
     */
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

    /**
     * Retrieves every Course from the database and places the results
     * inside the Course ComboBox.
     */
    private void loadCourses()
    {
        //Use the Course DAO supplied by the Course feature
        CourseDao courseDao = new CourseDao();

        List<Course> courses = courseDao.findAll();

        courseComboBox.setItems(
                FXCollections.observableArrayList(courses)
        );

        //Tell the user when an Assignment cannot be connected to a Course yet
        if (courses.isEmpty())
        {
            formMessageLabel.setText(
                    "No courses are available. Add a course before creating an assignment."
            );
        }
    }

    /**
     * Stores and selects the Course used when creating an Assignment.
     *
     * @param courseId The ID of the Course that owns the Assignment.
     * @throws IllegalArgumentException If the Course ID is less than
     * or equal to zero.
     */
    public void setCourseId(int courseId)
    {
        //The Assignment must belong to a valid Course
        if (courseId <= 0)
        {
            throw new IllegalArgumentException(
                    "Course ID must be greater than zero."
            );
        }

        this.courseId = courseId;
        selectCourseById(courseId);
    }

    /**
     * Prepares the form to create a new Assignment without requiring
     * a Course to be selected before the form opens.
     */
    public void prepareForAdd()
    {
        prepareForAdd(0);
    }

    /**
     * Prepares the form to create a new Assignment.
     *
     * A value of zero means the user opened Assignments directly
     * from the Dashboard and will choose a Course in this form.
     *
     * @param activeCourseId The Course ID that should be selected,
     * or zero when no Course was selected before opening the form.
     * @throws IllegalArgumentException If the Course ID is negative.
     */
    public void prepareForAdd(int activeCourseId)
    {
        if (activeCourseId < 0)
        {
            throw new IllegalArgumentException(
                    "Course ID cannot be negative."
            );
        }

        //A null Assignment tells the form that this is an insert
        assignment = null;

        //Remember whether the previous Assignment list was filtered
        returnCourseId = activeCourseId;

        //Update the heading for Add mode
        formTitleLabel.setText("Add Assignment");

        //Remove values that may already be displayed
        clearFields();

        //Select the Course when the form was opened from a specific Course
        if (activeCourseId > 0)
        {
            setCourseId(activeCourseId);
        }
    }

    /**
     * Prepares the form to edit an existing Assignment.
     *
     * @param assignment The Assignment being edited.
     */
    public void prepareForEdit(Assignment assignment)
    {
        prepareForEdit(assignment, 0);
    }

    /**
     * Prepares the form to edit an existing Assignment and displays
     * its current values inside the form fields.
     *
     * @param assignment The Assignment being edited.
     * @param activeCourseId The Course filter used by the previous
     * Assignment list, or zero when every Assignment was displayed.
     * @throws IllegalArgumentException If the Assignment is null or
     * the active Course ID is negative.
     */
    public void prepareForEdit(
            Assignment assignment,
            int activeCourseId
    )
    {
        //Stop if no Assignment was supplied
        if (assignment == null)
        {
            throw new IllegalArgumentException(
                    "Assignment cannot be null."
            );
        }

        if (activeCourseId < 0)
        {
            throw new IllegalArgumentException(
                    "Course ID cannot be negative."
            );
        }

        //Store the selected Assignment
        this.assignment = assignment;

        //Keep the original Course ID
        this.courseId = assignment.getCourseId();

        //Remember whether the previous Assignment list was filtered
        returnCourseId = activeCourseId;

        //Update the heading for Edit mode
        formTitleLabel.setText("Edit Assignment");

        //Display the current Assignment values
        displayAssignment();
    }

    /**
     * Selects the Course that matches the provided Course ID.
     *
     * @param courseId The Course ID to locate.
     */
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

    /**
     * Displays the stored Assignment information inside the form fields.
     */
    private void displayAssignment()
    {
        //Do nothing if the form is not editing an Assignment
        if (assignment == null)
        {
            return;
        }

        //Select the Course that currently owns the Assignment
        selectCourseById(assignment.getCourseId());

        //Fill in each field using the current Assignment values
        titleField.setText(assignment.getTitle());
        descriptionArea.setText(assignment.getDescription());
        dueDatePicker.setValue(assignment.getDueDate());
        pointsField.setText(
                String.valueOf(
                        assignment.getPointsPossible()
                )
        );
    }

    /**
     * Removes all displayed values from the Assignment form.
     */
    private void clearFields()
    {
        //Remove the current form values
        courseComboBox.getSelectionModel().clearSelection();
        titleField.clear();
        descriptionArea.clear();
        dueDatePicker.setValue(null);
        pointsField.clear();
        courseId = 0;

        if (courseComboBox.getItems().isEmpty())
        {
            formMessageLabel.setText(
                    "No courses are available. Add a course before creating an assignment."
            );
        }
        else
        {
            formMessageLabel.setText("");
        }
    }

    /**
     * Handles the Save button action.
     * Validates the fields and either inserts a new Assignment
     * or updates the existing Assignment.
     */
    @FXML
    private void handleSave()
    {
        //Read the Course selected from the ComboBox
        Course selectedCourse = courseComboBox.getValue();

        //Stop if the user did not select a Course
        if (selectedCourse == null)
        {
            formMessageLabel.setText(
                    "Select a course for the Assignment."
            );
            return;
        }

        //Store the selected Course ID on the Assignment
        courseId = selectedCourse.getCourseId();

        //Stop if the selected Course does not have a database ID
        if (courseId <= 0)
        {
            formMessageLabel.setText(
                    "Unable to save because the Course ID is invalid."
            );
            return;
        }

        //Read the remaining values entered into the form
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        LocalDate dueDate = dueDatePicker.getValue();
        String pointsText = pointsField.getText().trim();

        //Stop if the Assignment title was not entered
        if (title.isEmpty())
        {
            formMessageLabel.setText(
                    "Assignment title is required."
            );
            return;
        }

        //Stop if the Assignment due date was not selected
        if (dueDate == null)
        {
            formMessageLabel.setText(
                    "Assignment due date is required."
            );
            return;
        }

        //Stop if the points possible value was not entered
        if (pointsText.isEmpty())
        {
            formMessageLabel.setText(
                    "Points possible is required."
            );
            return;
        }

        int pointsPossible;

        //Convert the points possible text into an integer
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

        //Stop if the points possible value is negative
        if (pointsPossible < 0)
        {
            formMessageLabel.setText(
                    "Points possible cannot be negative."
            );
            return;
        }

        try
        {
            //Create the DAO used to insert or update the Assignment
            AssignmentDao assignmentDao = new AssignmentDao(
                    DatabaseManager.getInstance().getConnection()
            );

            if (assignment == null)
            {
                //Create a new Assignment when the form is in Add mode
                Assignment newAssignment = new Assignment(
                        courseId,
                        title,
                        description,
                        dueDate,
                        pointsPossible
                );

                //Insert the new Assignment into the database
                assignmentDao.insert(newAssignment);
            }
            else
            {
                //Create the updated Assignment while keeping its original ID
                Assignment updatedAssignment = new Assignment(
                        assignment.getAssignmentId(),
                        courseId,
                        title,
                        description,
                        dueDate,
                        pointsPossible
                );

                //Update the matching database row
                boolean updated = assignmentDao.update(updatedAssignment);

                //Stop if the database did not update the Assignment
                if (!updated)
                {
                    formMessageLabel.setText(
                            "Unable to update the Assignment."
                    );
                    return;
                }

                //Store the updated values after the database operation succeeds
                assignment = updatedAssignment;
            }

            //Return to the Assignment list after the database operation succeeds
            returnToAssignmentList();
        }
        catch (IllegalArgumentException e)
        {
            //Display validation messages produced by the Assignment class
            formMessageLabel.setText(e.getMessage());
        }
        catch (SQLException e)
        {
            //Keep the form open and display the database error
            formMessageLabel.setText(
                    "Unable to save the Assignment: "
                            + e.getMessage()
            );
        }
    }

    /**
     * Handles the Cancel button action and returns to the
     * Assignment list without saving changes.
     */
    @FXML
    private void handleCancel()
    {
        //Return to the Assignment list without changing the database
        returnToAssignmentList();
    }

    /**
     * Returns to the Assignment list when the Stage is available.
     */
    private void returnToAssignmentList()
    {
        if (stage == null)
        {
            return;
        }

        //Return to a Course-filtered list when the form was opened that way
        if (returnCourseId > 0)
        {
            stage.setScene(
                    SceneFactory.createAssignmentsScene(
                            stage,
                            returnCourseId
                    )
            );
            return;
        }

        //Return to the standalone Assignment feature
        stage.setScene(
                SceneFactory.createAssignmentsScene(stage)
        );
    }
}
