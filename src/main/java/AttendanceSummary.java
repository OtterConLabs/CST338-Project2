/**
 * Count summary for Attendance Report.
 *
 * @author Jit Tran
 * @since 08/14/2026
 */
public class AttendanceSummary{
    private final int presentCount;
    private final int absentCount;
    private final int lateCount;
    private final int excusedCount;

    private final double attendanceRate;

    public AttendanceSummary(
        int presentCount,
        int absentCount,
        int lateCount,
        int excusedCount,

        double attendanceRate
    ){
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.lateCount = lateCount;
        this.excusedCount = excusedCount;
        this.attendanceRate = attendanceRate;
    }

    public int getPresentCount(){
        return presentCount;
    }

    public int getAbsentCount(){
        return absentCount;
    }

    public int getLateCount(){
        return lateCount;
    }

    public int getExcusedCount(){
        return excusedCount;
    }

    public double getAttendanceRate(){
        return attendanceRate;
    }

    public boolean hasAttendanceRate(){
        return !Double.isNaN(attendanceRate);
    }

    public String getAttendanceRateText(){
        if(!hasAttendanceRate()){
            return "N/A";
        }

        return String.format(
            "%.1f%%",
            attendanceRate
        );
    }
}