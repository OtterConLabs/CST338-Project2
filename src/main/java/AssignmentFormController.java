import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;

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

    //Displays whether the form is adding or editing an Assignment
    @FXML
    private Label formTitleLabel;

    //Displays validation or database messages
    @FXML
    private Label formMessageLabel;

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
     * Stores the course ID used when creating a new Assignment.
     *
     * @param courseId The ID of the course that owns the Assignment.
     * @throws IllegalArgumentException If the course ID is less than
     * or equal to zero.
     */
    public void setCourseId(int courseId)
    {
        //The Assignment must belong to a valid course
        if (courseId <= 0)
        {
            throw new IllegalArgumentException(
                    "Course ID must be greater than zero."
            );
        }

        this.courseId = courseId;
    }

    /**
     * Prepares the form to create a new Assignment.
     *
     * @param courseId The ID of the course that will own the Assignment.
     */
    public void prepareForAdd(int courseId)
    {
        //Store the course used for the new Assignment
        setCourseId(courseId);

        //A null Assignment tells the form that this is an insert
        assignment = null;

        //Update the heading for Add mode
        formTitleLabel.setText("Add Assignment");

        //Remove values that may already be displayed
        clearFields();
    }

    /**
     * Prepares the form to edit an existing Assignment and displays
     * its current values inside the form fields.
     *
     * @param assignment The Assignment being edited.
     * @throws IllegalArgumentException If the Assignment is null.
     */
    public void prepareForEdit(Assignment assignment)
    {
        //Stop if no Assignment was supplied
        if (assignment == null)
        {
            throw new IllegalArgumentException(
                    "Assignment cannot be null."
            );
        }

        //Store the selected Assignment
        this.assignment = assignment;

        //Keep the original course ID
        this.courseId = assignment.getCourseId();

        //Update the heading for Edit mode
        formTitleLabel.setText("Edit Assignment");

        //Display the current Assignment values
        displayAssignment();
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
        titleField.clear();
        descriptionArea.clear();
        dueDatePicker.setValue(null);
        pointsField.clear();
        formMessageLabel.setText("");
    }

    /**
     * Handles the Save button action.
     * Validates the fields and either inserts a new Assignment
     * or updates the existing Assignment.
     */
    @FXML
    private void handleSave()
    {
        //Read the values entered into the form
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

        //Stop if the form does not have a valid course ID
        if (courseId <= 0)
        {
            formMessageLabel.setText(
                    "Unable to save because the course ID is invalid."
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
        if (stage != null)
        {
            //Return to the Assignment list using the same course ID
            stage.setScene(
                    SceneFactory.createAssignmentsScene(
                            stage,
                            courseId
                    )
            );
        }
    }
}
