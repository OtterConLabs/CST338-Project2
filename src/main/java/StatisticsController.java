import javafx.fxml.FXML;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import javafx.scene.control.Label;

import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

/**
 * Controls the Grade Statistics scene
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
public class StatisticsController{
    private Stage stage;

    private GradeDao gradeDao;
    private EnrollmentDao enrollmentDao;
    private GradeService gradeService;

    @FXML
    private Label assignmentTitleLabel;

    @FXML
    private Label gradedCountLabel;

    @FXML
    private Label ungradedCountLabel;

    @FXML
    private Label meanLabel;

    @FXML
    private Label medianLabel;

    @FXML
    private Label minimumLabel;

    @FXML
    private Label maximumLabel;

    @FXML
    private Label statisticsMessageLabel;

    @FXML
    private BarChart<String, Number> distributionChart;

    @FXML
    private void initialize(){
        initializeDependencies();
        showUnavailableStatistics();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    void setDependencies(
        GradeDao gradeDao,
        EnrollmentDao enrollmentDao
    ){
        if(gradeDao == null || enrollmentDao == null){
            throw new IllegalArgumentException(
                "Statistics dependencies cannot be null");
        }

        this.gradeDao = gradeDao;
        this.enrollmentDao = enrollmentDao;

        gradeService = new GradeService(
            gradeDao,
            enrollmentDao
        );
    }

    public void prepareForAssignment(Assignment assignment){
        if(assignment == null){
            throw new IllegalArgumentException(
                "Assignment cannot be null");
        }

        assignmentTitleLabel.setText(
            assignment.getTitle()
            + " ("
            + assignment.getPointsPossible()
            + " points)"
        );

        loadStatistics(assignment);
    }

    private void initializeDependencies(){
        if(gradeDao != null
            && enrollmentDao != null
            && gradeService != null){
            return;
        }

        Connection connection = DatabaseManager
            .getInstance()
            .getConnection();

        setDependencies(
            new GradeDao(connection),
            new EnrollmentDao(connection)
        );
    }

    private void loadStatistics(Assignment assignment){
        distributionChart.getData().clear();

        try{
            List<Grade> grades =
                gradeDao.findbyAssignmentID(
                    assignment.getAssignmentId()
                );

            int enrolledCount =
                enrollmentDao.countEnrolled(
                    assignment.getCourseId()
                );

            gradedCountLabel.setText(
                Integer.toString(grades.size())
            );

            ungradedCountLabel.setText(
                Integer.toString(
                    Math.max(
                        0,
                        enrolledCount - grades.size()
                    )
                )
            );

            if(assignment.getPointsPossible() <= 0){
                showUnavailableStatistics();

                statisticsMessageLabel.setText(
                    "Statistics are unavailable for a zero point assignment");

                return;
            }

            GradeStatistics statistics =
                gradeService.calculateStatistics(
                    grades,
                    assignment.getPointsPossible(),
                    enrolledCount
                );

            displayStatistics(statistics);

        }catch(SQLException e){
            showUnavailableStatistics();

            statisticsMessageLabel.setText(
                "Could not load statistics: "
                + e.getMessage()
            );
        }
    }

    private void displayStatistics(
        GradeStatistics statistics
    ){
        gradedCountLabel.setText(
            Integer.toString(
                statistics.getGradedCount()
            )
        );

        ungradedCountLabel.setText(
            Integer.toString(
                statistics.getUngradedCount()
            )
        );

        if(!statistics.hasGrades()){
            showUnavailableStatistics();

            statisticsMessageLabel.setText(
                "No grades recorded yet"
            );

            return;
        }

        meanLabel.setText(
            formatPercentage(statistics.getMean())
        );

        medianLabel.setText(
            formatPercentage(statistics.getMedian())
        );

        minimumLabel.setText(
            formatPercentage(statistics.getMinimum())
        );

        maximumLabel.setText(
            formatPercentage(statistics.getMaximum())
        );

        XYChart.Series<String, Number> series =
            new XYChart.Series<>();

        series.getData().add(
            new XYChart.Data<>(
                "Below 60",
                statistics.getBelow60Count()
            )
        );

        series.getData().add(
            new XYChart.Data<>(
                "60-69",
                statistics.getSixtyToSixtyNineCount()
            )
        );

        series.getData().add(
            new XYChart.Data<>(
                "70-79",
                statistics.getSeventyToSeventyNineCount()
            )
        );

        series.getData().add(
            new XYChart.Data<>(
                "80-89",
                statistics.getEightyToEightyNineCount()
            )
        );

        series.getData().add(
            new XYChart.Data<>(
                "90-100",
                statistics.getNinetyToOneHundredCount()
            )
        );

        distributionChart.getData().clear();
        distributionChart.getData().add(series);

        statisticsMessageLabel.setText(
            "Grade distribution for assignment"
        );
    }

    private void showUnavailableStatistics(){
        meanLabel.setText("N/A");
        medianLabel.setText("N/A");
        minimumLabel.setText("N/A");
        maximumLabel.setText("N/A");

        distributionChart.getData().clear();
    }

    private String formatPercentage(double value){
        return String.format(
            "%.1f%%",
            value
        );
    }

    @FXML
    private void handleBack(){
        if(stage == null){
            throw new IllegalStateException(
                "Stage not set"
            );
        }

        stage.setScene(
            SceneFactory.create(
                SceneType.GRADES,
                stage
            )
        );
    }
}