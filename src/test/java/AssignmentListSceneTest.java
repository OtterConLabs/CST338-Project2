import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

class AssignmentListSceneTest extends ApplicationTest
{
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/AssignmentListScene.fxml")
        );

        Parent root = loader.load();

        AssignmentListController controller = loader.getController();
        controller.setStage(stage);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void addButtonOpensAssignmentForm()
    {
        clickOn("Add");

        verifyThat("#formTitleLabel", isVisible());
    }

    @Test
    void cancelReturnsToAssignmentList()
    {
        clickOn("Add");

        verifyThat("#formTitleLabel", isVisible());

        clickOn("Cancel");

        verifyThat("#assignmentTable", isVisible());
    }
}