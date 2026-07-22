import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point for JavaFX Practice application.
 *
 * @author Yoko Mohr
 * @since 7/7/2026
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("OtterCon Labs");
        stage.setScene(SceneFactory.create(SceneType.LOGIN, stage));
        stage.show();
    }

    /**
     * Application entry point. JavaFX requires calling launch(), which
     * internally creates the JavaFX runtime and calls start().
     */
    public static void main(String[] args) {
        launch(args);
    }
}