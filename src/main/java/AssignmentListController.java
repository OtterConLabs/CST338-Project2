import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    // Zero means the Assignment list is not filtered to one Course.
    private static final int NO_COURSE_ID = 0;

    // Stores the primary Stage used for scene navigation.
    private Stage stage;

    // Stores the ID of the course currently connected to this Assignment list.
    // NO_COURSE_ID means the list is displaying Assignments from every Course.
    private int activeCourseId = NO_COURSE_ID;

    // Provides Course data used by the Assignment list.
    private CourseDao courseDao = new CourseDao();

    // Stores each Course ID with the Course code displayed in the table.
    private final Map<Integer, String> courseCodes = new HashMap<>();

    @FXML
    private TableView<Assignment> assignmentTable;

    @FXML
    private TableColumn<Assignment, String> courseColumn;

    @FXML
    private TableColumn<Assignment, String> titleColumn;

    @FXML
    private TableColumn<Assignment, LocalDate> dueDateColumn;

    @FXML
    private TableColumn<Assignment, Integer> pointsColumn;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button backToCoursesButton;

    @FXML
    private void initialize()
    {
        courseColumn.setCellValueFactory(
                assignment ->
                        new ReadOnlyStringWrapper(
                                getCourseCode(
                                        assignment.getValue().getCourseId()
                                )
                        )
        );

        titleColumn.setCellValueFactory(
                assignment ->
                        new ReadOnlyStringWrapper(
                                assignment.getValue().getTitle()
                        )
        );

        dueDateColumn.setCellValueFactory(
                assignment ->
                        new ReadOnlyObjectWrapper<>(
                                assignment.getValue().getDueDate()
                        )
        );

        pointsColumn.setCellValueFactory(
                assignment ->
                        new ReadOnlyObjectWrapper<>(
                                assignment.getValue().getPointsPossible()
                        )
        );

        assignmentTable.setPlaceholder(
                new Label(
                        "No assignments yet — click Add to create one"
                )
        );

        assignmentTable.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldAssignment, newAssignment) ->
                        {
                            if (newAssignment == null)
                            {
                                descriptionArea.clear();
                                return;
                            }

                            descriptionArea.setText(
                                    newAssignment.getDescription()
                            );
                        }
                );

        // Only show Back to Courses when Assignments was opened
        // from a selected Course.
        backToCoursesButton.setVisible(false);
        backToCoursesButton.setManaged(false);
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
        loadAssignments();
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
    }

    public void setActiveCourseId(int activeCourseId)
    {
        // This method receives an actual Course, so zero is not a valid Course ID.
        if (activeCourseId <= NO_COURSE_ID)
        {
            throw new IllegalArgumentException(
                    "Course ID must be greater than zero."
            );
        }

        this.activeCourseId = activeCourseId;

        // The Assignment list was opened from Courses,
        // so allow the user to return to the Course list.
        if (backToCoursesButton != null)
        {
            backToCoursesButton.setVisible(true);
            backToCoursesButton.setManaged(true);
        }
    }

    private void loadAssignments()
    {
        try
        {
            loadCourseCodes();

            AssignmentDao assignmentDao = new AssignmentDao(
                    DatabaseManager.getInstance().getConnection()
            );

            List<Assignment> assignments;

            if (activeCourseId > NO_COURSE_ID)
            {
                assignments =
                        assignmentDao.findByCourseId(activeCourseId);
            }
            else
            {
                assignments =
                        assignmentDao.findAll();
            }

            assignmentTable.setItems(
                    FXCollections.observableArrayList(assignments)
            );

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

    private void loadCourseCodes()
    {
        courseCodes.clear();

        for (Course course : courseDao.findAll())
        {
            courseCodes.put(
                    course.getCourseId(),
                    course.getCourseCode()
            );
        }
    }

    private String getCourseCode(int courseId)
    {
        return courseCodes.getOrDefault(
                courseId,
                "Course " + courseId
        );
    }

    @FXML
    private void handleAdd()
    {
        if (activeCourseId > NO_COURSE_ID)
        {
            stage.setScene(
                    SceneFactory.createAssignmentFormForAdd(
                            stage,
                            activeCourseId
                    )
            );
            return;
        }

        stage.setScene(
                SceneFactory.createAssignmentFormForAdd(stage)
        );
    }

    @FXML
    private void handleEdit()
    {
        Assignment selectedAssignment =
                assignmentTable.getSelectionModel().getSelectedItem();

        if (selectedAssignment == null)
        {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING
            );

            alert.setTitle("Assignments");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Select an assignment before clicking Edit."
            );

            alert.showAndWait();
            return;
        }

        stage.setScene(
                SceneFactory.createAssignmentFormForEdit(
                        stage,
                        selectedAssignment,
                        activeCourseId
                )
        );
    }

    /**
     * Returns to the Course list when Assignments was opened
     * from a selected Course.
     */
    @FXML
    private void handleBackToCourses()
    {
        stage.setScene(
                SceneFactory.create(
                        SceneType.COURSES,
                        stage
                )
        );
    }

    @FXML
    private void handleBack()
    {
        stage.setScene(
                SceneFactory.create(
                        SceneType.DASHBOARD,
                        stage
                )
        );
    }

    @FXML
    private void handleDelete()
    {
        Assignment selectedAssignment =
                assignmentTable.getSelectionModel().getSelectedItem();

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

        if (result.isEmpty()
                || result.get() != ButtonType.OK)
        {
            return;
        }

        try
        {
            AssignmentDao assignmentDao =
                    new AssignmentDao(
                            DatabaseManager
                                    .getInstance()
                                    .getConnection()
                    );

            boolean deleted =
                    assignmentDao.deleteById(
                            selectedAssignment.getAssignmentId()
                    );

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
