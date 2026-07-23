import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.security.PrivateKey;

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
    private static final String TITLE = "Grade & Assignment Tracker";
    private static final String USERNAME = "Username: ";
    private static final String PASSWORD = "Password: ";
    private static final String FIRSTNAME = "First Name: ";
    private static final String LASTNAME = "Last Name: ";
    private static final String EMAIL = "Email: ";
    private static final String ROLE= "Role : ";
    private static final String NEW_MEMBER = "New Member?";
    private static final String REGISTER = "Registration Form";

    public static TextField usernameInput = new TextField();
    public static PasswordField  passwordInput = new PasswordField();

    public static Scene create(SceneType type, Stage stage, DatabaseManager db) {
        return switch (type) {
            case LOGIN -> buildLoginScene(stage, db);
            case REGISTER -> buildRegisterScene(stage, db);
            case DASHBOARD -> buildDashboardScene(stage, db);
            case COURSE_LIST -> buildCourseListScene(stage, db);
            case COURSE_EDIT -> buildCourseEditScene(stage, db);
            case ASSIGNMENT_LIST -> buildAssignmentListScene(stage, db);
            case ASSIGNMENT_EDIT -> buildAssignmentEditScene(stage, db);
        };
    }

    private static Scene buildLoginScene(Stage stage, DatabaseManager db) {
        //TODO YOKO:
        Label org = new Label(ORG);
        Label title = new Label(TITLE);
        Label userName = new Label(USERNAME);
        Label loginMsg = new Label("Please enter your username and password.");
        userName.setPrefWidth(80);
        Label password = new Label(PASSWORD);
        password.setPrefWidth(80);
        Label newMember = new Label(NEW_MEMBER);
        org.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

//        TextField usernameInput = new TextField();
        usernameInput.setPromptText(USERNAME);
        usernameInput.setMaxWidth(200);

        HBox usernameField = new HBox(15, userName,usernameInput);
        usernameField.setAlignment(Pos.CENTER);

//        PasswordField  passwordInput = new PasswordField();
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

//              TODO: YOKO
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
        Label register= new Label(REGISTER);
        register.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label msg = new Label("Please enter your information.");
        msg.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        Label username = new Label(USERNAME);
        username.setPrefWidth(80);
        TextField registerUsernameInput = new TextField();
        registerUsernameInput.setPromptText(USERNAME);
        registerUsernameInput.setMaxWidth(200);
        HBox usernameField = new HBox(15, username, registerUsernameInput);
        usernameField.setAlignment(Pos.CENTER);

        Label password = new Label(PASSWORD);
        password.setPrefWidth(80);
        PasswordField registerPasswordInput = new PasswordField();
        registerPasswordInput.setPromptText(PASSWORD);
        registerPasswordInput.setMaxWidth(200);
        HBox passwordField = new HBox(15, password, registerPasswordInput);
        passwordField.setAlignment(Pos.CENTER);

//        TextField userInput = new TextField();

        Label firstName = new Label(FIRSTNAME);
        firstName.setPrefWidth(80);
        TextField firstNameInput = new TextField();
        firstNameInput.setPromptText(FIRSTNAME);
        firstNameInput.setMaxWidth(200);
        HBox firstNameField = new HBox(15, firstName, firstNameInput);
        firstNameField.setAlignment(Pos.CENTER);

        Label lastName = new Label(LASTNAME);
        lastName.setPrefWidth(80);
        TextField lastNameInput = new TextField();
        lastNameInput.setPromptText(LASTNAME);
        lastNameInput.setMaxWidth(200);
        HBox lastNameField = new HBox(15, lastName, lastNameInput);
        lastNameField.setAlignment(Pos.CENTER);

        Label email = new Label(EMAIL);
        email.setPrefWidth(80);
        TextField emailInput = new TextField();
        emailInput.setPromptText(EMAIL);
        emailInput.setMaxWidth(200);
        HBox emailField = new HBox(15, email, emailInput);
        emailField.setAlignment(Pos.CENTER);

        Label role  = new Label("Select Role:");
        role.setPrefWidth(80);
        ComboBox<UserRole> roleInput = new ComboBox<>();
        roleInput.getItems().addAll(UserRole.values());
        roleInput.setPromptText("Role");
//        roleInput.setMaxWidth(100);
        HBox roleField = new HBox(15, role, roleInput);
        roleField.setAlignment(Pos.CENTER);

        Button regBtn = new Button("Register");
        regBtn.setOnAction(event -> {
            //TODO YOKO
//            stage.setScene(create(SceneType.LOGIN, stage, db));
        });

        Button backBtn = new Button("Back");
        regBtn.setOnAction(event -> {
            //TODO YOKO
            stage.setScene(create(SceneType.LOGIN, stage, db));
        });

        HBox regBackBtn = new HBox(200, regBtn, backBtn);
        regBackBtn.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, register, msg, usernameField, passwordField, firstNameField, lastNameField, emailField, roleField, regBackBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        return new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
    }

    private static Scene buildDashboardScene(Stage stage, DatabaseManager db) {
        //TODO YOKO:
        Label title = new Label("Dashboard");
        Label role = new Label(ROLE);
        Label name = new Label("Name: ");

        User userInfo = db.checkLogin(usernameInput.getText(), passwordInput.getText());

        Label userDisplayName = new Label(userInfo.getFirstName() + " " + userInfo.getLastName());
        HBox usernameField = new HBox(3, name, userDisplayName);
        usernameField.setAlignment(Pos.CENTER);

        Label userRole = new Label(userInfo.getRole().toString());
        HBox userRoleField = new HBox(3, role, userRole);
        userRoleField.setAlignment(Pos.CENTER);

        Button courseAndEnrollment = new Button("Courses & Enrollment");
        courseAndEnrollment.setOnAction(event -> {
            stage.setScene(create(SceneType.COURSE_LIST, stage, db));
        });

        Button assignment = new Button("Assignments");
        assignment.setOnAction(event -> {
            stage.setScene(create(SceneType.ASSIGNMENT_LIST, stage, db));
        });

        VBox layout = new VBox(16,title, usernameField, userRoleField, courseAndEnrollment, assignment);
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
