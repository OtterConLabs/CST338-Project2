import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * [CST338 P2 SceneFactory]
 *
 * @author Yoko Mohr
 * @since 7/20/2026
 */
public class SceneFactory {
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 400;

    private static final String ORG = "Welcome to OtterCon Labs!";
    private static final String TITLE = "Grade / Assignment Tracker";
    private static final String USERNAME = "Username or Email: ";
    private static final String PASSWORD = "Password: ";
    private static final String NEW_MEMBER = "New Member?";

    public static Scene create(SceneType type, Stage stage, DatabaseManager db) {
        return switch (type) {
//            case MAIN -> buildMainScene(stage);
            case LOGIN -> buildLoginScene(stage, db);
            case REGISTER -> buildRegisterScene(stage, db);
            case DASHBOARD -> buildDashboardScene(stage, db);
            case COURSE_LIST -> buildCourseListScene(stage, db);
            case COURSE_EDIT -> buildCourseEditScene(stage, db);
            case ASSIGNMENT_LIST -> buildAssignmentListScene(stage, db);
            case ASSIGNMENT_EDIT -> buildAssignmentEditScene(stage, db);
        };
    }

//    private static Scene buildMainScene(Stage stage) {
//        //TODO: YOKO
//        Label org = new Label(ORG);
//        Label title = new Label(TITLE);
//        org.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
//        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
//
//        Button goBtn = new Button("Open Dashboard");
//        goBtn.setOnAction(event -> {
//            stage.setScene(create(SceneType.DASHBOARD, stage));
//        });
//
//        VBox layout = new VBox(16, org, title, goBtn);
//        layout.setAlignment(Pos.CENTER);
//
//        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
//    }

    private static Scene buildLoginScene(Stage stage, DatabaseManager db) {
        //TODO YOKO:
        Label org = new Label(ORG);
        Label title = new Label(TITLE);
        Label userNameOrEmail = new Label(USERNAME);
        Label loginMsg = new Label("Please enter your username and password.");
        userNameOrEmail.setPrefWidth(140);
        Label password = new Label(PASSWORD);
        password.setPrefWidth(140);
        Label newMember = new Label(NEW_MEMBER);
        org.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameInput = new TextField();
        usernameInput.setPromptText(USERNAME);
        usernameInput.setMaxWidth(200);

        HBox usernameField = new HBox(15, userNameOrEmail,usernameInput);
        usernameField.setAlignment(Pos.CENTER);

        PasswordField  passwordInput = new PasswordField();
        passwordInput.setPromptText(PASSWORD);
        passwordInput.setMaxWidth(200);

        HBox passwordField = new HBox(15, password, passwordInput);
        passwordField.setAlignment(Pos.CENTER);

        Button logBtn = new Button("Log in");
        logBtn.setOnAction(event -> {
            String username = usernameInput.getText().trim();
            String passwordTxt = passwordInput.getText().trim();
            if (username.isEmpty() || passwordTxt.isEmpty()) {
                loginMsg.setText("Please enter your username and password.");
                return;
            }
            User loggedInUser = db.checkLogin(username,passwordTxt);
            if (loggedInUser != null) {
                stage.setScene(create(SceneType.DASHBOARD, stage, db)
                );
            } else {
                loginMsg.setText("Incorrect username or password");
            }
        });

        Button regBtn = new Button("Register");
        regBtn.setOnAction(event -> {
            stage.setScene(create(SceneType.REGISTER, stage, db));
        });

        VBox layout = new VBox(16, org, title, loginMsg, usernameField, passwordField, logBtn, newMember, regBtn);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);

        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);


//        loginButton.setOnAction(event -> {
//            User user = accountService.login(
//                    usernameField.getText(),
//                    passwordField.getText()
//            );
//
//            if (user != null) {
//                stage.setScene(
//                        SceneFactory.create(SceneType.DASHBOARD, stage)
//                );
//            } else {
//                errorLabel.setText("Invalid username or password.");
//            }
//        });
//
//        return buildPlaceholderScene("Login", stage);
    }

    private static Scene buildRegisterScene(Stage stage, DatabaseManager db) {
        //TODO YOKO:
        return buildPlaceholderScene("Register", stage, db);
    }

    private static Scene buildDashboardScene(Stage stage, DatabaseManager db) {
        //TODO YOKO:
        Label title = new Label("Dashboard");
        Label role = new Label("Role: ");
        Label name = new Label("Name: ");

        Button course = new Button("Course");
        course.setOnAction(event -> {
            stage.setScene(create(SceneType.COURSE_LIST, stage, db));
        });

        Button assignment = new Button("Assignment");
        assignment.setOnAction(event -> {
            stage.setScene(create(SceneType.ASSIGNMENT_LIST, stage, db));
        });

        VBox layout = new VBox(16, title,role,name, course,assignment);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private static Scene buildCourseListScene(Stage stage, DatabaseManager db) {
        //TODO Brent:
        return buildPlaceholderScene("Course List", stage, db);
    }

    private static Scene buildCourseEditScene(Stage stage, DatabaseManager db) {
        //TODO Brent:
        return buildPlaceholderScene("Course Edit", stage, db);
    }

    private static Scene buildAssignmentListScene(Stage stage, DatabaseManager db) {
        //TODO Jordan:
        return buildPlaceholderScene("Assignment List", stage, db);
    }

    private static Scene buildAssignmentEditScene(Stage stage, DatabaseManager db) {
        //TODO Jordan:
        return buildPlaceholderScene("Assignment Edit", stage, db);
    }

    private static Scene buildPlaceholderScene(String sceneTitle, Stage stage, DatabaseManager db) {
        Label label = new Label(sceneTitle);
        Button backButton = new Button("Back to Login");

        backButton.setOnAction(event ->
                stage.setScene(create(SceneType.LOGIN, stage, db))
        );

        VBox layout = new VBox(16, label, backButton);
        layout.setAlignment(Pos.CENTER);

        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
    }
}
