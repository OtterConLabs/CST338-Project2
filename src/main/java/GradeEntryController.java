import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Controls the Grade Entry scene.
 *
 * @author Jit Tran
 * @since 08/04/2026
 */
public class GradeEntryController {
    private Stage stage;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    private void handleBack(){
        if(stage == null){
            throw new IllegalStateException(
                "Stage not set."
            );
        }
        stage.setScene(
                SceneFactory.create(SceneType.DASHBOARD, stage)
        );
    }
}