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

    // Stores the course the user picked on the Course List screen so the
    // Edit and Enrollment scenes know which course they are working on.
    private static Course selectedCourse;

    /**
     * Saves the course being edited or managed. Pass null to indicate that
     * the Course Edit scene should create a new course rather than update one.
     *
     * @param course the selected course, or null for a new course
     */
    public static void setSelectedCourse(Course course) {
        selectedCourse = course;
    }

    /**
     * Returns the course being edited or managed.
     *
     * @return the selected course, or null if a new course is being added
     */
    public static Course getSelectedCourse() {
        return selectedCourse;
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

            // Jordan
            case ASSIGNMENTS -> buildAssignmentsScene(stage);
            // Jit
            case GRADES -> buildGradesScene(stage);
            case ATTENDANCE -> buildAttendanceScene(stage);
        };
    }

    // Loads the Login scene from its FXML file and connects its controller.
    private static Scene buildLoginScene(Stage stage) {
        try {
            // Load the Login screen layout from the resources' folder.
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

    // Loads the Course List scene from its FXML file and connects its controller.
    private static Scene buildCoursesScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource("/CourseListScene.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            CourseListController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load CourseListScene.fxml", e);
        }
    }

    // Loads the Add/Edit Course scene from its FXML file and connects its controller.
    private static Scene buildCourseEditScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource("/CourseEditScene.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            CourseEditController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load CourseEditScene.fxml", e);
        }
    }

    // Loads the Manage Enrollment scene from its FXML file and connects its controller.
    private static Scene buildEnrollmentScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource("/EnrollmentScene.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

            EnrollmentController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load EnrollmentScene.fxml", e);
        }
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
        //Course ID must be greater than zero before opening the Assignment scene
        if (activeCourseId <= 0)
        {
            throw new IllegalArgumentException(
                    "Course ID must be greater than zero."
            );
        }

        //Build the Assignment scene using the selected course ID
        return buildAssignmentsScene(
                stage,
                activeCourseId
        );
    }

    // Loads the Assignment list scene without requiring a selected course.
    private static Scene buildAssignmentsScene(Stage stage)
    {
        //Zero means that no active course was passed into the scene
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
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource(
                            "/AssignmentListScene.fxml"
                    )
            );

            Parent root = loader.load();

            Scene scene = new Scene(
                    root,
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );

            AssignmentListController controller =
                    loader.getController();

            //Pass the active course ID when a course was selected
            if (activeCourseId > 0)
            {
                controller.setActiveCourseId(activeCourseId);
            }

            //Pass the Stage after the course ID so the correct Assignments are loaded
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

    public static Scene createAssignmentFormForAdd(Stage stage, int courseId)
    {
        //Course ID must be greater than zero before opening Add mode
        if (courseId <= 0)
        {
            throw new IllegalArgumentException(
                    "Course ID must be greater than zero."
            );
        }

        try
        {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource(
                            "/AssignmentFormScene.fxml"
                    )
            );

            Parent root = loader.load();

            AssignmentFormController controller =
                    loader.getController();

            controller.setStage(stage);
            controller.prepareForAdd(courseId);

            return new Scene(
                    root,
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                    "Failed to load AssignmentFormScene.fxml",
                    e
            );
        }
    }

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
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource(
                            "/AssignmentFormScene.fxml"
                    )
            );

            Parent root = loader.load();

            AssignmentFormController controller =
                    loader.getController();

            controller.setStage(stage);
            controller.prepareForEdit(assignment);

            return new Scene(
                    root,
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );
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
