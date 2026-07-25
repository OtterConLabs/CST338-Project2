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

//    private static final String ORG = "Welcome to OtterCon Labs!";
//    private static final String TITLE = "Grade & Assignment Tracker";
//    private static final String USERNAME = "Username: ";
//    private static final String PASSWORD = "Password: ";
//    private static final String FIRSTNAME = "First Name: ";
//    private static final String LASTNAME = "Last Name: ";
//    private static final String EMAIL = "Email: ";
//    private static final String ROLE= "Role : ";
//    private static final String NEW_MEMBER = "New Member?";
//    private static final String REGISTER = "Registration Form";

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

            Scene scene = new Scene(
                    loader.load(),
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );

            LoginController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load LoginScene.fxml",
                    e
            );
        }
    }

    private static Scene buildRegisterScene(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneFactory.class.getResource("/RegisterScene.fxml")
            );

            Scene scene = new Scene(
                    loader.load(),
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );

            RegisterController controller = loader.getController();
            controller.setStage(stage);

            return scene;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load RegisterScene.fxml",
                    e
            );
        }
        //TODO YOKO:
//        DatabaseManager db = DatabaseManager.getInstance();
//        Label register= new Label(REGISTER);
//        register.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
//        Label msg = new Label("Please enter your information.");
//        msg.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
//
//        //TODO YOKO: Make this more modular... create a function
//        Label username = new Label(USERNAME);
//        username.setPrefWidth(80);
//        TextField registerUsernameInput = new TextField();
//        registerUsernameInput.setPromptText(USERNAME);
//        registerUsernameInput.setMaxWidth(200);
//        HBox usernameField = new HBox(15, username, registerUsernameInput);
//        usernameField.setAlignment(Pos.CENTER);
//
//        Label password = new Label(PASSWORD);
//        password.setPrefWidth(80);
//        PasswordField registerPasswordInput = new PasswordField();
//        registerPasswordInput.setPromptText(PASSWORD);
//        registerPasswordInput.setMaxWidth(200);
//        HBox passwordField = new HBox(15, password, registerPasswordInput);
//        passwordField.setAlignment(Pos.CENTER);
//
////        TextField userInput = new TextField();
//
//        Label firstName = new Label(FIRSTNAME);
//        firstName.setPrefWidth(80);
//        TextField firstNameInput = new TextField();
//        firstNameInput.setPromptText(FIRSTNAME);
//        firstNameInput.setMaxWidth(200);
//        HBox firstNameField = new HBox(15, firstName, firstNameInput);
//        firstNameField.setAlignment(Pos.CENTER);
//
//        Label lastName = new Label(LASTNAME);
//        lastName.setPrefWidth(80);
//        TextField lastNameInput = new TextField();
//        lastNameInput.setPromptText(LASTNAME);
//        lastNameInput.setMaxWidth(200);
//        HBox lastNameField = new HBox(15, lastName, lastNameInput);
//        lastNameField.setAlignment(Pos.CENTER);
//
//        Label email = new Label(EMAIL);
//        email.setPrefWidth(80);
//        TextField emailInput = new TextField();
//        emailInput.setPromptText(EMAIL);
//        emailInput.setMaxWidth(200);
//        HBox emailField = new HBox(15, email, emailInput);
//        emailField.setAlignment(Pos.CENTER);
//
//        Label role  = new Label("Select Role:");
//        role.setPrefWidth(80);
//        ComboBox<UserRole> roleInput = new ComboBox<>();
//        roleInput.getItems().addAll(UserRole.values());
//        roleInput.setPromptText("Role");
////        roleInput.setMaxWidth(100);
//        HBox roleField = new HBox(15, role, roleInput);
//        roleField.setAlignment(Pos.CENTER);
//
//        Button regBtn = new Button("Register");
//        regBtn.setOnAction(event -> {
//            //TODO YOKO
////            stage.setScene(create(SceneType.LOGIN, stage, db));
//        });
//
//        Button backBtn = new Button("Back");
//        backBtn.setOnAction(event -> {
//            //TODO YOKO
//            stage.setScene(create(SceneType.LOGIN, stage));
//        });
//
//        HBox regBackBtn = new HBox(200, regBtn, backBtn);
//        regBackBtn.setAlignment(Pos.CENTER);
//
//        VBox layout = new VBox(15, register, msg, usernameField, passwordField, firstNameField, lastNameField, emailField, roleField, regBackBtn);
//        layout.setPadding(new Insets(20));
//        layout.setAlignment(Pos.TOP_CENTER);
//
//        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
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

        HBox usernameField = new HBox(
                3,
                name,
                userDisplayName
        );
        usernameField.setAlignment(Pos.CENTER);

        Label userRole = new Label(
                userInfo.getRole().name()
        );

        HBox userRoleField = new HBox(
                3,
                role,
                userRole
        );
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

        VBox layout = new VBox(
                16,
                title,
                usernameField,
                userRoleField,
                courseAndEnrollment,
                assignment
        );

        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);

        return new Scene(
                layout,
                SCENE_WIDTH,
                SCENE_HEIGHT
        );

//
//        DatabaseManager db = DatabaseManager.getInstance();
//        fetched here, not passed in
//        ListView<String> list = new ListView<>();
//        list.getItems().addAll(db.checkLogin());
//
//        User userInfo = getLoggedInUser();
//
//        if (userInfo == null) {
//            return buildLoginScene(stage);
//        }
//
//        Label userDisplayName = new Label(
//                userInfo.getFirstName() + " " + userInfo.getLastName()
//        );
//
//        Label userRole = new Label(
//                userInfo.getRole().name()
//        );
//
//        Label title = new Label("Dashboard");
//        Label role = new Label(ROLE);
//        Label name = new Label("Name: ");
//
//        userInfo = db.checkLogin(usernameInput.getText(), passwordInput.getText());
//
//        userDisplayName = new Label(userInfo.getFirstName() + " " + userInfo.getLastName());
//        HBox usernameField = new HBox(3, name, userDisplayName);
//        usernameField.setAlignment(Pos.CENTER);
//
//        userRole = new Label(userInfo.getRole().toString());
//        HBox userRoleField = new HBox(3, role, userRole);
//        userRoleField.setAlignment(Pos.CENTER);
//
//        Button courseAndEnrollment = new Button("Courses & Enrollment");
//        courseAndEnrollment.setOnAction(event -> {
//            stage.setScene(create(SceneType.COURSE_LIST, stage));
//        });
//
//        Button assignment = new Button("Assignments");
//        assignment.setOnAction(event -> {
//            stage.setScene(create(SceneType.ASSIGNMENT_LIST, stage));
//        });
//
//        VBox layout = new VBox(16,title, usernameField, userRoleField, courseAndEnrollment, assignment);
//        layout.setPadding(new Insets(30));
//        layout.setAlignment(Pos.CENTER);
//
//        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
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
