/**
 * Stores calculated (from Grade records) statistics for one assignment
 *
 * @author Jit Tran
 * @since 08/13/2026
 */
public class GradeStatistics{
    private final int gradedCount;
    private final int ungradedCount;

    private final double mean;
    private final double median;
    private final double minimum;
    private final double maximum;

    private final int below60Count;
    private final int sixtyToSixtyNineCount;
    private final int seventyToSeventyNineCount;
    private final int eightyToEightyNineCount;
    private final int ninetyToOneHundredCount;

    public GradeStatistics(
        int gradedCount,
        int ungradedCount,

        double mean,
        double median,
        double minimum,
        double maximum,

        int below60Count,
        int sixtyToSixtyNineCount,
        int seventyToSeventyNineCount,
        int eightyToEightyNineCount,
        int ninetyToOneHundredCount
    ){
        this.gradedCount = gradedCount;
        this.ungradedCount = ungradedCount;

        this.mean = mean;
        this.median = median;
        this.minimum = minimum;
        this.maximum = maximum;
        
        this.below60Count = below60Count;
        this.sixtyToSixtyNineCount = sixtyToSixtyNineCount;
        this.seventyToSeventyNineCount = seventyToSeventyNineCount;
        this.eightyToEightyNineCount = eightyToEightyNineCount;
        this.ninetyToOneHundredCount = ninetyToOneHundredCount;
    }

    public boolean hasGrades(){
        return gradedCount > 0;
    }

    public int getGradedCount(){
        return gradedCount;
    }

    public int getUngradedCount(){
        return ungradedCount;
    }

    public int getTotalCount(){
        return gradedCount + ungradedCount;
    }

    public double getMean(){
        return mean;
    }

    public double getMedian(){
        return median;
    }

    public double getMinimum(){
        return minimum;
    }

    public double getMaximum(){
        return maximum;
    }

    public int getBelow60Count(){
        return below60Count;
    }

    public int getSixtyToSixtyNineCount(){
        return sixtyToSixtyNineCount;
    }

    public int getSeventyToSeventyNineCount(){
        return seventyToSeventyNineCount;
    }

    public int getEightyToEightyNineCount(){
        return eightyToEightyNineCount;
    }

    public int getNinetyToOneHundredCount(){
        return ninetyToOneHundredCount;
    }
}