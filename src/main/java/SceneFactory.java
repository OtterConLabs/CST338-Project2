import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 * [CST338 P2 SceneFactory]
 *
 * @author Yoko Mohr
 * @since 7/20/2026
 */
public class SceneFactory {
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 400;

    private static User loggedInUser;

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static Scene create(SceneType type, Stage stage) {
        return switch (type) {
            case LOGIN -> buildLoginScene(stage);
            case REGISTER -> buildRegisterScene(stage);
            case DASHBOARD -> buildDashboardScene(stage);
            case COURSE_LIST -> buildCourseListScene(stage);
            case COURSE_EDIT -> buildCourseEditScene(stage);
            case ASSIGNMENT_LIST -> buildAssignmentListScene(stage);
            case ASSIGNMENT_EDIT -> buildAssignmentEditScene(stage);
        };
    }

    private static Scene buildLoginScene(Stage stage) {
        //TODO YOKO:
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneFactory.class.getResource("/LoginScene.fxml")
            );

            Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);

            LoginController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load LoginScene.fxml",
                    e // create a new exception based on the old one.
            );
        }
    }

    private static Scene buildRegisterScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource("/RegisterScene.fxml")
            );

            Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);

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

    private static Scene buildDashboardScene(Stage stage) {
        //TODO YOKO:
        User userInfo = getLoggedInUser();

        if (userInfo == null) {
            return buildLoginScene(stage);
        }

        Label title = new Label("Dashboard");
        Label name = new Label("Name:");
        Label role = new Label("Role:");

        Label userDisplayName = new Label(
                userInfo.getFirstName() + " " + userInfo.getLastName()
        );

        HBox usernameField = new HBox(3, name, userDisplayName);
        usernameField.setAlignment(Pos.CENTER);

        Label userRole = new Label(
                userInfo.getRole().name()
        );

        HBox userRoleField = new HBox(3, role, userRole);
        userRoleField.setAlignment(Pos.CENTER);

        Button courseAndEnrollment =
                new Button("Courses & Enrollment");

        courseAndEnrollment.setOnAction(event ->
                stage.setScene(
                        create(SceneType.COURSE_LIST, stage)
                )
        );

        Button assignment = new Button("Assignments");

        assignment.setOnAction(event ->
                stage.setScene(
                        create(SceneType.ASSIGNMENT_LIST, stage)
                )
        );

        VBox layout = new VBox(16, title, usernameField, userRoleField, courseAndEnrollment, assignment);

        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private static Scene buildCourseListScene(Stage stage) {
        //TODO Brent:
        return buildPlaceholderScene("Course List", stage);
    }

    private static Scene buildCourseEditScene(Stage stage) {
        //TODO Brent:
        return buildPlaceholderScene("Course Edit", stage);
    }

    private static Scene buildAssignmentListScene(Stage stage) {
        //TODO Jordan:
        return buildPlaceholderScene("Assignment List", stage);
    }

    private static Scene buildAssignmentEditScene(Stage stage) {
        //TODO Jordan:
        return buildPlaceholderScene("Assignment Edit", stage);
    }

    private static Scene buildPlaceholderScene(String sceneTitle, Stage stage) {
        Label label = new Label(sceneTitle);
        Button backButton = new Button("Back to Login");

        backButton.setOnAction(event ->
                stage.setScene(create(SceneType.LOGIN, stage))
        );

        VBox layout = new VBox(16, label, backButton);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
    }
}
