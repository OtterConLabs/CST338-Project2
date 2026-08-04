import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 * [CST338 P2 SceneFactory]
 * Scene Factory receives a Scene Type, creates the corresponding JavaFX Scene,
 * and returns it to the caller.
 * @author Yoko Mohr
 * @since 7/20/2026
 */
public class SceneFactory {
    // Default size used for the application scenes.
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 400;

    // Stores the user who is currently logged in.
    private static User loggedInUser;

    // Saves the currently logged-in user.
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    // Returns the currently logged-in user.
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    // Creates the requested scene based on the provided SceneType.
    public static Scene create(SceneType type, Stage stage) {
        return switch (type) {

            // Yoko
            case LOGIN -> buildLoginScene(stage);
            case REGISTER -> buildRegisterScene(stage);
            case DASHBOARD -> buildDashboardScene(stage);
            case PROFILE -> buildProfileScene(stage);

            // Brent
            case COURSES -> buildCoursesScene(stage);
            case COURSE_EDIT -> buildCourseEditScene(stage);
            case ENROLLMENT -> buildEnrollmentScene(stage);

            // Jourdan
            case ASSIGNMENTS -> buildAssignmentsScene(stage);

            // Jit
            case GRADES -> buildGradesScene(stage);
            case ATTENDANCE -> buildAttendanceScene(stage);
        };
    }

    // Loads the Login scene from its FXML file and connects its controller.
    private static Scene buildLoginScene(Stage stage) {
        //TODO YOKO:
        try {
            // Load the Login screen layout from the resources folder.
            FXMLLoader loader = new FXMLLoader(
                SceneFactory.class.getResource("/LoginScene.fxml")
            );
            // Build a Scene using the root node created from the FXML file.
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            // fx:controller="LoginController
            // Retrieve the controller created by FXMLLoader.
            LoginController controller = loader.getController();
            // Pass the current Stage to the controller for scene navigation.
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException(
                // Preserve the original exception as the cause.
                    "Failed to load LoginScene.fxml",
                    e // create a new exception based on the old one.
            );
        }
    }

    // Loads the Registration scene from its FXML file and connects its controller.
    private static Scene buildRegisterScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource("/RegisterScene.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            RegisterController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load RegisterScene.fxml",
                    e
            );
        }
    }

    // Creates the Dashboard scene for the currently logged-in user.
    private static Scene buildDashboardScene(Stage stage) {
        //TODO YOKO:
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneFactory.class.getResource("/DashboardScene.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            DashboardController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to load DashboardScene.fxml",
                e
            );
        }
    }

    private static Scene buildProfileScene(Stage stage) {
        // TODO Yoko
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneFactory.class.getResource("/ProfileScene.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            ProfileController controller = loader.getController();
            controller.setStage(stage);
            return scene;

        } catch (IOException e) {
            throw new RuntimeException(
                "Failed to load ProfileScene.fxml",
                e
            );
        }
    }

    private static Scene buildCourseEditScene(Stage stage) {
        //TODO Brent: replace with CourseEditScene.fxml
        return buildPlaceholderScene("Course Edit", stage);
    }

    private static Scene buildCoursesScene(Stage stage) {
        //TODO Brent: replace with CourseListScene.fxml
        return buildPlaceholderScene("Course List", stage);
    }

    private static Scene buildEnrollmentScene(Stage stage) {
        //TODO Brent: replace with EnrollmentScene.fxml
        return buildPlaceholderScene("Manage Enrollment", stage);

    }

    /**
     * Creates the Assignment list scene using the course that
     * the user currently selected.
     *
     * @param stage The Stage used for scene navigation.
     * @param activeCourseId The ID of the course currently being viewed.
     * @return The completed Assignment list scene.
     * @throws IllegalArgumentException If the course ID is not valid.
     */
    public static Scene createAssignmentsScene(
            Stage stage,
            int activeCourseId
    )
    {
        //course ID must be greater than zero before opening the Assignment scene
        if (activeCourseId <= 0)
        {
            throw new IllegalArgumentException(
                    "course ID must be greater than 0."
            );
        }

        //build the Assignment scene using the selected course ID
        return buildAssignmentsScene(
                stage,
                activeCourseId
        );
    }

    // Loads the Assignment list scene without requiring a selected course.
    private static Scene buildAssignmentsScene(Stage stage)
    {
        //zero means that no active course was passed into the scene
        return buildAssignmentsScene(
                stage,
                0
        );
    }

    /**
     * Loads the Assignment list scene from its FXML file,
     * connects its controller, and passes the active course ID.
     *
     * @param stage The Stage used for scene navigation.
     * @param activeCourseId The ID of the course currently being viewed.
     * @return The completed Assignment list scene.
     */
    private static Scene buildAssignmentsScene(
            Stage stage,
            int activeCourseId
    )
    {
        try
        {
            //load the Assignment List layout from the resources folder
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource(
                            "/AssignmentListScene.fxml"
                    )
            );

            //build the root node using the Assignment List FXML file
            Parent root = loader.load();

            //build the Assignment List scene using the default scene size
            Scene scene = new Scene(
                    root,
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );

            //get the controller created by FXMLLoader
            AssignmentListController controller =
                    loader.getController();

            //pass the active course ID when a course was selected
            if (activeCourseId > 0)
            {
                controller.setActiveCourseId(activeCourseId);
            }

            //pass the Stage after the course ID so the correct Assignments are loaded
            controller.setStage(stage);

            return scene;
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "Failed to load AssignmentListScene.fxml",
                    e
            );
        }
    }

    /**
     * Creates the Assignment form scene for adding a new Assignment.
     *
     * @param stage The Stage used for scene navigation.
     * @param activeCourseId The ID of the course that will own the Assignment.
     * @return The completed Assignment form scene.
     * @throws IllegalArgumentException If the course ID is not valid.
     */
    public static Scene createAssignmentFormForAdd(
            Stage stage,
            int activeCourseId
    )
    {
        //Course ID must be greater than zero before opening Add mode
        if (activeCourseId <= 0)
        {
            throw new IllegalArgumentException(
                    "course ID must be greater than 0."
            );
        }

        try
        {
            //Load the Assignment Form layout from the resources folder
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource(
                            "/AssignmentFormScene.fxml"
                    )
            );

            //Build the root node using the Assignment Form FXML file
            Parent root = loader.load();

            //Build the Assignment Form scene using the default scene size
            Scene scene = new Scene(
                    root,
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );

            //Get the controller created by FXMLLoader
            AssignmentFormController controller =
                    loader.getController();

            //Pass the Stage and active course ID into Add mode
            controller.setStage(stage);
            controller.prepareForAdd(activeCourseId);

            return scene;
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "Failed to load AssignmentFormScene.fxml",
                    e
            );
        }
    }

    /**
     * Creates the Assignment form scene for editing the selected Assignment.
     *
     * @param stage The Stage used for scene navigation.
     * @param assignment The Assignment selected from the table.
     * @return The completed Assignment form scene.
     * @throws IllegalArgumentException If the Assignment is null.
     */
    public static Scene createAssignmentFormForEdit(
            Stage stage,
            Assignment assignment
    )
    {
        //Stop if no Assignment was supplied
        if (assignment == null)
        {
            throw new IllegalArgumentException(
                    "Assignment cannot be null."
            );
        }

        try
        {
            //Load the Assignment Form layout from the resources folder
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource(
                            "/AssignmentFormScene.fxml"
                    )
            );

            //Build the root node using the Assignment Form FXML file
            Parent root = loader.load();

            //Build the Assignment Form scene using the default scene size
            Scene scene = new Scene(
                    root,
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );

            //Get the controller created by FXMLLoader
            AssignmentFormController controller =
                    loader.getController();

            //Pass the Stage and selected Assignment into Edit mode
            controller.setStage(stage);
            controller.prepareForEdit(assignment);

            return scene;
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "Failed to load AssignmentFormScene.fxml",
                    e
            );
        }
    }

    private static Scene buildGradesScene(Stage stage) {
        //TODO Jit:
        return buildPlaceholderScene("Grades", stage);
    }

    private static Scene buildAttendanceScene(Stage stage) {
        //TODO Jit:
        return buildPlaceholderScene("Attendance", stage);
    }

    private static Scene buildPlaceholderScene(String sceneTitle, Stage stage) {
        Label label = new Label(sceneTitle);
        Button backButton = new Button("Back to Dashboard");

        backButton.setOnAction(event ->
                stage.setScene(create(SceneType.DASHBOARD, stage))
        );

        VBox layout = new VBox(16, label, backButton);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
    }
}
