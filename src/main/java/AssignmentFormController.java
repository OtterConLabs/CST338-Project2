import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
     * The completed logic will validate the fields and either insert
     * a new Assignment or update the existing Assignment.
     */
    @FXML
    private void handleSave()
    {
        //Read and validate the form values

        //Create a new Assignment when assignment is null

        //Update the existing Assignment when assignment is not null

        //Return to the Assignment list after the database operation succeeds
        formMessageLabel.setText(
                "Save logic will be added next."
        );
    }

    /**
     * Handles the Cancel button action and returns to the
     * Assignment list without saving changes.
     */
    @FXML
    private void handleCancel()
    {
        //Return to the Assignment list without changing the database
        if (stage != null)
        {
            stage.setScene(
                    SceneFactory.create(
                            SceneType.ASSIGNMENTS,
                            stage
                    )
            );
        }
    }
}