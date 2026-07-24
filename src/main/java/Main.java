import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for JavaFX Practice application.
 *
 * @author Yoko Mohr
 * @since 7/20/2026
 */
public class Main extends Application {

    private DatabaseManager db;

    @Override
    public void start(Stage stage) {

        db = new DatabaseManager();

        /*
         * Optional sample data for local testing.
         * Uncomment db.insertUser(user) once to add this user to your local app.db.
         * Comment it out again afterward because username and email must be unique.
         */
        User user = new User(
                "teststudent",
                "Test",
                "Student",
                "teststudent@csumb.edu",
                "1234",
                UserRole.STUDENT
        );
//        db.insertUser(user);

        stage.setTitle("OtterCon Labs");
        stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
        stage.show();
    }

    @Override
    public void stop() {
        if (db != null) {
            db.close();
        }
    }


    /**
     * Application entry point. JavaFX requires calling launch(), which
     * internally creates the JavaFX runtime and calls start().
     */
    public static void main(String[] args) {
        launch(args);
    }
}