import java.time.LocalDate;
import java.util.Objects;

/**
 * The model class representing an assignment belonging to a course.
 * @author Jordan Browning
 * @since 7/24/2026
 */
public class Assignment
{
    private int assignmentId;
    /** The ID of the course that owns this assignment. */
    private int courseId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private int pointsPossible;

    /**
     * constructor passes or delegates values to constructor via constructor chaining
     *
     * @param courseId The ID of the course
     * @param title The assignment title
     * @param description The assignment description
     * @param dueDate The assignment due date
     * @param pointsPossible The maximum points possible
     */
    public Assignment(int courseId, String title, String description, LocalDate dueDate, int pointsPossible)
    {
        this(0, courseId, title, description, dueDate, pointsPossible);
    }

    /**
     * constructors defined by setter-based initialization
     *
     * @param assignmentId The unique ID for the assignment
     * @param courseId The ID of the course
     * @param title The assignment title
     * @param description The assignment description
     * @param dueDate The assignment due date
     * @param pointsPossible The maximum points possible
     */
    public Assignment(int assignmentId, int courseId, String title, String description, LocalDate dueDate, int pointsPossible)
    {
        setAssignmentId(assignmentId);
        setCourseId(courseId);
        setTitle(title);
        setDescription(description);
        setDueDate(dueDate);
        setPointsPossible(pointsPossible);
    }

    /**
     * standard getter for assignmentId returns a value
     *
     * @return the assignment ID
     */
    public int getAssignmentId()
    {
        return assignmentId;
    }

    /**
     * standard setter for assignmentId sets a value
     * Also throws an exception if assignmentId is negative
     *
     * @param assignmentId the ID to set
     * @throws IllegalArgumentException if assignmentId is negative
     */
    public void setAssignmentId(int assignmentId)
    {
        if (assignmentId < 0)
        {
            throw new IllegalArgumentException("Assignment ID cannot be negative.");
        }

        this.assignmentId = assignmentId;
    }

    /**
     * standard getter for courseId returns a value
     *
     * @return the course ID
     */
    public int getCourseId()
    {
        return courseId;
    }

    /**
     * standard setter for courseId sets a value
     * Also throws an exception if courseId is negative or zero
     *
     * @param courseId the ID of the course
     * @throws IllegalArgumentException if courseId is negative or zero
     */
    public void setCourseId(int courseId)
    {
        if (courseId <= 0)
        {
            throw new IllegalArgumentException("Course ID must be greater than zero.");
        }

        this.courseId = courseId;
    }

    /**
     * standard getter for title, returns the title value
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * sets the title name and throws an exception if the string is empty or null
     *
     * @param title the title to set
     * @throws IllegalArgumentException if the string is empty or null
     */
    public void setTitle(String title) {
        if (title == null || title.isBlank())
        {
            throw new IllegalArgumentException("Assignment title is required.");
        }

        this.title = title.trim();
    }

    /**
     * standard getter for description, returns the string.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * setter for description, sets a string value if null empty string, if not, returns string without
     * padded whitespaces at start and end
     *
     * @param description the description text to set
     */
    public void setDescription(String description)
    {
        if (description == null)
        {
            this.description = "";
        }
        else
        {
            this.description = description.trim();
        }
    }

    /**
     * getter for dueDate, returns a LocalDate object
     *
     * @return the due date as a LocalDate
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * setter for dueDate object, if null throws requires due date statement
     *
     * @param dueDate the LocalDate object to set
     * @throws IllegalArgumentException if dueDate is null
     */
    public void setDueDate(LocalDate dueDate) {
        if (dueDate == null)
        {
            throw new IllegalArgumentException("Assignment due date is required.");
        }

        this.dueDate = dueDate;
    }

    /**
     * returns the points possible to achieve
     *
     * @return total possible points
     */
    public int getPointsPossible()
    {
        return pointsPossible;
    }

    /**
     * sets the possible points and throws an error if the value is negative
     *
     * @param pointsPossible points possible
     * @throws IllegalArgumentException if the value is negative
     */
    public void setPointsPossible(int pointsPossible)
    {
        if (pointsPossible < 0)
        {
            throw new IllegalArgumentException("Points possible cannot be negative.");
        }

        this.pointsPossible = pointsPossible;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Assignment that)) return false;
        return getAssignmentId() == that.getAssignmentId() && getCourseId() == that.getCourseId() && getPointsPossible() == that.getPointsPossible() && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getDueDate(), that.getDueDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAssignmentId(), getCourseId(), getTitle(), getDescription(), getDueDate(), getPointsPossible());
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentId=" + assignmentId +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", pointsPossible=" + pointsPossible +
                '}';
    }
}