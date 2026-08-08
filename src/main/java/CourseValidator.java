import java.util.ArrayList;
import java.util.List;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Holds the business rules for the Courses &amp; Enrollment slice so the
 * controllers stay thin and the rules can be unit tested without a database
 * or a JavaFX window.
 *
 * @author Brent Brewington
 * @since 8/7/2026
 */
public final class CourseValidator {

    /** Course codes look like CST338: two to four letters followed by three or four digits. */
    private static final String COURSE_CODE_PATTERN = "[A-Za-z]{2,4}\\d{3,4}";

    /** A blank capacity field means "no limit", stored as 0. */
    public static final int UNLIMITED_CAPACITY = 0;

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
     * Validates the form including the extra-credit capacity field. The
     * capacity text is optional: a blank value means the course has no seat
     * limit. When it is filled in it must be a whole number that is zero or
     * greater.
     *
     * @param courseCode   the code entered on the form
     * @param courseName   the title entered on the form
     * @param teacherId    users.id of the selected teacher, or 0 if none is selected
     * @param capacityText the raw capacity text from the form, may be blank
     * @return the error messages found, or an empty list if the form is valid
     */
    public static List<String> validate(String courseCode, String courseName,
                                        int teacherId, String capacityText) {
        List<String> errors = validate(courseCode, courseName, teacherId);

        if (!isBlank(capacityText)) {
            try {
                int capacity = Integer.parseInt(capacityText.trim());
                if (capacity < 0) {
                    errors.add("Capacity cannot be negative.");
                }
            } catch (NumberFormatException e) {
                errors.add("Capacity must be a whole number.");
            }
        }

        return errors;
    }

    /**
     * Turns the capacity text from the form into the integer the model stores.
     * A blank field becomes {@link #UNLIMITED_CAPACITY}. Call this only after
     * {@link #validate(String, String, int, String)} has passed, so the parse
     * is known to succeed.
     *
     * @param capacityText the raw capacity text from the form, may be blank
     * @return the capacity to store, or 0 for unlimited
     * @throws NumberFormatException if the text is present but not a number
     */
    public static int parseCapacity(String capacityText) {
        if (isBlank(capacityText)) {
            return UNLIMITED_CAPACITY;
        }
        return Integer.parseInt(capacityText.trim());
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
     * True when a course is at or over its seat limit. A capacity of zero
     * means the course is unlimited and is therefore never full.
     *
     * @param enrolledCount how many students are already enrolled
     * @param capacity      the course seat limit, or 0 for unlimited
     * @return true if no more students can be enrolled
     */
    public static boolean isFull(int enrolledCount, int capacity) {
        return capacity > UNLIMITED_CAPACITY && enrolledCount >= capacity;
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
