import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controls the Assignment list screen and displays the assignments
 * retrieved from the database.
 *
 * @author Jordan Browning
 * @since 7/30/2026
 */
public class AssignmentListController
{
    // Stores the primary Stage used for scene navigation.
    private Stage stage;

    // Stores the ID of the course currently connected to this Assignment list.
    private int activeCourseId;

    // Displays the assignments retrieved from the database.
    @FXML
    private TableView<Assignment> assignmentTable;

    // Displays the title of each Assignment.
    @FXML
    private TableColumn<Assignment, String> titleColumn;

    // Displays the due date of each Assignment.
    @FXML
    private TableColumn<Assignment, LocalDate> dueDateColumn;

    // Displays the possible points for each Assignment.
    @FXML
    private TableColumn<Assignment, Integer> pointsColumn;


    /**
     * Prepares the Assignment table after the FXML file is loaded.
     */
    @FXML
    private void initialize()
    {
        // Tells the Title column to display the Assignment title.
        titleColumn.setCellValueFactory(
                assignment ->
                        new ReadOnlyStringWrapper(
                                assignment.getValue().getTitle()
                        )
        );

        // Tells the Due Date column to display the Assignment due date.
        dueDateColumn.setCellValueFactory(
                assignment ->
                        new ReadOnlyObjectWrapper<>(
                                assignment.getValue().getDueDate()
                        )
        );

        // Tells the Points column to display the Assignment points possible.
        pointsColumn.setCellValueFactory(
                assignment ->
                        new ReadOnlyObjectWrapper<>(
                                assignment.getValue().getPointsPossible()
                        )
        );

        // Displays a message when there are no assignments in the table.
        assignmentTable.setPlaceholder(
                new Label(
                        "No assignments yet — click Add to create one"
                )
        );
    }

    /**
     * Stores the primary application Stage and loads the assignments
     * from the database.
     *
     * @param stage The Stage used for scene navigation.
     */
    public void setStage(Stage stage)
    {
        this.stage = stage;
        loadAssignments();
    }

    /**
     * Stores the active course ID so new assignments can be connected
     * to the course that the user selected.
     *
     * @param activeCourseId The ID of the course currently being viewed.
     * @throws IllegalArgumentException If the course ID is not valid.
     */
    public void setActiveCourseId(int activeCourseId)
    {
        //course ID must be greater than zero before it can be stored
        if (activeCourseId <= 0)
        {
            throw new IllegalArgumentException(
                    "course ID must be greater than 0."
            );
        }

        //store the course ID that was passed into the Assignment scene
        this.activeCourseId = activeCourseId;
    }

    /**
     * Retrieves the Assignments from the database and displays
     * them inside the TableView.
     *
     * If an active course was passed into the Assignment scene,
     * only the Assignments connected to that course will be displayed.
     */
    private void loadAssignments()
    {
        try
        {
            //create the AssignmentDao using the database connection
            AssignmentDao assignmentDao = new AssignmentDao(
                    DatabaseManager.getInstance().getConnection()
            );

            List<Assignment> assignments;

            //check if the Assignment scene received an active course ID
            if (activeCourseId > 0)
            {
                //only retrieve Assignments that belong to the active course
                assignments =
                        assignmentDao.findByCourseId(activeCourseId);
            }
            else
            {
                //retrieve every Assignment when no active course was passed in
                assignments =
                        assignmentDao.findAll();
            }

            //place the Assignments retrieved from the database into the table
            assignmentTable.setItems(
                    FXCollections.observableArrayList(assignments)
            );
        }
        catch (SQLException e)
        {
            //display the database error inside the Assignment table
            assignmentTable.setPlaceholder(
                    new Label(
                            "Unable to load assignments: "
                                    + e.getMessage()
                    )
            );
        }
    }

    /**
     * Handles the Add button action and opens the Assignment form.
     */
    @FXML
    private void handleAdd()
    {
        //Stop if the Assignment list was opened without a selected course
        if (activeCourseId <= 0)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Assignments");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Select a course before adding an assignment."
            );
            alert.showAndWait();
            return;
        }

        //Open the Assignment form using the active course ID
        stage.setScene(
                SceneFactory.createAssignmentFormForAdd(
                        stage,
                        activeCourseId
                )
        );
    }

    /**
     * Handles the Edit button action using the Assignment selected
     * from the table.
     */
    @FXML
    private void handleEdit()
    {
        Assignment selectedAssignment =
                assignmentTable.getSelectionModel().getSelectedItem();

        if (selectedAssignment == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Assignments");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Select an assignment before clicking Edit."
            );
            alert.showAndWait();
            return;
        }

        //Open the Assignment form using the selected Assignment
        stage.setScene(
                SceneFactory.createAssignmentFormForEdit(
                        stage,
                        selectedAssignment
                )
        );
    }
}