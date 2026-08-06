import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Logic unit tests for the Course form rules. These run without a database
 * and without a JavaFX window, so they stay fast and never flake.
 *
 * @author Brent Brewington
 * @since 7/30/2026
 */
class   CourseValidatorTest {

    private static final int TEACHER_ID = 1;

    @Test
    @DisplayName("A complete form produces no errors")
    void validate_completeForm_returnsNoErrors() {
        List<String> errors = CourseValidator.validate("CST338", "Software Design", TEACHER_ID);

        assertTrue(errors.isEmpty(), "A valid form should produce no errors");
        assertTrue(CourseValidator.isValid("CST338", "Software Design", TEACHER_ID));
    }

    @Test
    @DisplayName("A blank course code is rejected")
    void validate_blankCourseCode_returnsError() {
        List<String> errors = CourseValidator.validate("   ", "Software Design", TEACHER_ID);

        assertEquals(1, errors.size());
        assertEquals("Course code is required.", errors.get(0));
    }

    @Test
    @DisplayName("A course code that is not letters plus digits is rejected")
    void validate_malformedCourseCode_returnsError() {
        List<String> errors = CourseValidator.validate("338", "Software Design", TEACHER_ID);

        assertTrue(errors.contains("Course code must look like CST338."));
    }

    @Test
    @DisplayName("A blank course name is rejected")
    void validate_blankCourseName_returnsError() {
        List<String> errors = CourseValidator.validate("CST338", "", TEACHER_ID);

        assertTrue(errors.contains("Course name is required."));
    }

    @Test
    @DisplayName("Every problem on the form is reported at once")
    void validate_emptyForm_returnsAllErrors() {
        List<String> errors = CourseValidator.validate(null, null, 0);

        assertEquals(3, errors.size(), "Blank code, blank name, and no teacher");
        assertFalse(CourseValidator.toMessage(errors).isBlank());
    }

    @Test
    @DisplayName("Only a TEACHER can own a course and only a STUDENT can enroll")
    void roleRules_areEnforced() {
        User teacher = new User("mlarkin", "Morgan", "Larkin",
                "mlarkin@otterconlabs.edu", "pass123", UserRole.TEACHER);
        User student = new User("asinclair", "Ava", "Sinclair",
                "asinclair@otterconlabs.edu", "pass123", UserRole.STUDENT);

        assertTrue(CourseValidator.canOwnCourse(teacher));
        assertFalse(CourseValidator.canOwnCourse(student));
        assertTrue(CourseValidator.canEnroll(student));
        assertFalse(CourseValidator.canEnroll(teacher));
        assertFalse(CourseValidator.canEnroll(null), "A null selection is never valid");
    }
}
