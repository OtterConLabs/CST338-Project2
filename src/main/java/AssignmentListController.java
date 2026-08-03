import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    // Displays the description of the Assignment selected from the table.
    @FXML
    private TextArea descriptionArea;

    /**
     * Prepares the Assignment table and description area after
     * the FXML file is loaded.
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

        // Watches the Assignment selected from the table and displays
        // its description underneath the table.
        assignmentTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldAssignment, newAssignment) ->
                        {
                            // Remove the description when no Assignment is selected.
                            if (newAssignment == null)
                            {
                                descriptionArea.clear();
                                return;
                            }

                            // Display the selected Assignment description.
                            descriptionArea.setText(
                                    newAssignment.getDescription()
                            );
                        }
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
     * Retrieves every Assignment from the database and displays
     * them inside the TableView.
     */
    private void loadAssignments()
    {
        try
        {
            AssignmentDao assignmentDao = new AssignmentDao(
                    DatabaseManager.getInstance().getConnection()
            );

            List<Assignment> assignments =
                    assignmentDao.findAll();

            assignmentTable.setItems(
                    FXCollections.observableArrayList(assignments)
            );

            // Remove the previous selection and description after reloading.
            assignmentTable.getSelectionModel().clearSelection();
            descriptionArea.clear();
        }
        catch (SQLException e)
        {
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
        stage.setScene(
                SceneFactory.createAssignmentFormForAdd(
                        stage,
                        1
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
            assignmentTable.setPlaceholder(
                    new Label(
                            "Select an assignment before clicking Edit."
                    )
            );
            return;
        }

        stage.setScene(
                SceneFactory.createAssignmentFormForEdit(
                        stage,
                        selectedAssignment
                )
        );
    }

    /**
     * Handles the Delete button action using the Assignment selected
     * from the table.
     */
    @FXML
    private void handleDelete()
    {
        //Get the Assignment selected from the table
        Assignment selectedAssignment =
                assignmentTable.getSelectionModel().getSelectedItem();

        //Stop if no Assignment was selected
        if (selectedAssignment == null)
        {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING
            );

            alert.setTitle("No Assignment Selected");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Select an assignment before clicking Delete."
            );

            alert.showAndWait();
            return;
        }

        //Ask the user to confirm before deleting the Assignment
        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Assignment");
        confirmation.setHeaderText(
                "Delete " + selectedAssignment.getTitle() + "?"
        );
        confirmation.setContentText(
                "This assignment will be permanently deleted."
        );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        //Stop if the user closes the alert or selects Cancel
        if (result.isEmpty()
                || result.get() != ButtonType.OK)
        {
            return;
        }

        try
        {
            //Connect the DAO to the shared database connection
            AssignmentDao assignmentDao =
                    new AssignmentDao(
                            DatabaseManager
                                    .getInstance()
                                    .getConnection()
                    );

            //Delete the selected Assignment using its ID
            boolean deleted =
                    assignmentDao.deleteById(
                            selectedAssignment.getAssignmentId()
                    );

            //Refresh the table after the Assignment is deleted
            if (deleted)
            {
                loadAssignments();
            }
            else
            {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR
                );

                alert.setTitle("Delete Failed");
                alert.setHeaderText(null);
                alert.setContentText(
                        "The assignment could not be deleted."
                );

                alert.showAndWait();
            }
        }
        catch (SQLException e)
        {
            //Display the database error without closing the application
            Alert alert = new Alert(
                    Alert.AlertType.ERROR
            );

            alert.setTitle("Database Error");
            alert.setHeaderText(
                    "Unable to delete the assignment."
            );
            alert.setContentText(
                    e.getMessage()
            );

            alert.showAndWait();
        }
    }

}