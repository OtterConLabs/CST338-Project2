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
            case LOGIN -> buildLoginScene(stage);
            case REGISTER -> buildRegisterScene(stage);
            case DASHBOARD -> buildDashboardScene(stage);
            case PROFILE -> buildProfileScene(stage);
            case COURSES -> buildCoursesScene(stage);
            case ASSIGNMENTS -> buildAssignmentsScene(stage);
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
        return buildPlaceholderScene("Profile", stage);
    }

    private static Scene buildCoursesScene(Stage stage) {
        //TODO Brent:
        return buildPlaceholderScene("Courses", stage);
    }

    private static Scene buildAssignmentsScene(Stage stage) {
        //TODO Jordan:
        return buildPlaceholderScene("Assignments", stage);
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
