import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
        // The Assignment form navigation will be added next.
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

        // The selected Assignment will be passed to the form next.
    }
}