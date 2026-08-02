import java.util.ArrayList;
import java.util.List;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Holds the business rules for the Courses &amp; Enrollment slice so the
 * controllers stay thin and the rules can be unit tested without a database
 * or a JavaFX window.
 *
 * @author Brent Brewington
 * @since 7/30/2026
 */
public final class CourseValidator {

    /** Course codes look like CST338: two to four letters followed by three or four digits. */
    private static final String COURSE_CODE_PATTERN = "[A-Za-z]{2,4}\\d{3,4}";

    /** Utility class: no instances. */
    private CourseValidator() {
    }

    /**
     * Validates the values typed into the Add/Edit Course form. Returning a
     * list instead of throwing lets the controller show every problem at once
     * and keep the user's entered data on screen.
     *
     * @param courseCode the code entered on the form
     * @param courseName the title entered on the form
     * @param teacherId  users.id of the selected teacher, or 0 if none is selected
     * @return the error messages found, or an empty list if the form is valid
     */
    public static List<String> validate(String courseCode, String courseName, int teacherId) {
        List<String> errors = new ArrayList<>();

        if (isBlank(courseCode)) {
            errors.add("Course code is required.");
        } else if (!courseCode.trim().matches(COURSE_CODE_PATTERN)) {
            errors.add("Course code must look like CST338.");
        }

        if (isBlank(courseName)) {
            errors.add("Course name is required.");
        }

        if (teacherId <= 0) {
            errors.add("A teacher must be selected.");
        }

        return errors;
    }

    /**
     * Convenience check used by the controller before it calls the DAO.
     *
     * @param courseCode the code entered on the form
     * @param courseName the title entered on the form
     * @param teacherId  users.id of the selected teacher
     * @return true if the form has no errors
     */
    public static boolean isValid(String courseCode, String courseName, int teacherId) {
        return validate(courseCode, courseName, teacherId).isEmpty();
    }

    /**
     * Only users whose role is TEACHER may own a course.
     *
     * @param user the user picked in the teacher dropdown
     * @return true if the user can be assigned to a course
     */
    public static boolean canOwnCourse(User user) {
        return user != null && user.getRole() == UserRole.TEACHER;
    }

    /**
     * Only users whose role is STUDENT may be enrolled.
     *
     * @param user the user picked in the available list
     * @return true if the user can be enrolled
     */
    public static boolean canEnroll(User user) {
        return user != null && user.getRole() == UserRole.STUDENT;
    }

    /**
     * Turns the error list into one message for the inline error label.
     *
     * @param errors the messages produced by validate()
     * @return the joined message, or an empty string if there are no errors
     */
    public static String toMessage(List<String> errors) {
        return errors.isEmpty() ? "" : String.join(" ", errors);
    }

    /**
     * Null-safe blank check.
     *
     * @param value the text to inspect
     * @return true if the value is null, empty, or only whitespace
     */
    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
