import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for JavaFX Practice application.
 *
 * @author Yoko Mohr
 * @since 7/7/2026
 */
public class Main extends Application {

    private DatabaseManager db;

    @Override
    public void start(Stage stage) {
        db = new DatabaseManager();

        User user = new User(
                "mike",
                "Mike",
                "Mohr",
                "m@csumb.edu",
                "234",
                UserRole.TEACHER
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

//    @Override
//    public void start(Stage stage) {
//        stage.setTitle("OtterCon Labs");
//        stage.setScene(SceneFactory.create(SceneType.LOGIN, stage, db));
//        stage.show();
//    }

    /**
     * Application entry point. JavaFX requires calling launch(), which
     * internally creates the JavaFX runtime and calls start().
     */
    public static void main(String[] args) {
        launch(args);
    }
}