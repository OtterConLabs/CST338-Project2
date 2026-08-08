import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * [CST338 Project2 - Slice 2: Courses &amp; Enrollment]
 * Logic tests for the extra-credit capacity rules added to CourseValidator.
 * These are kept separate from CourseValidatorTest so the original base-form
 * and role tests stay untouched. No database or JavaFX window is needed.
 *
 * @author Brent Brewington
 * @since 8/7/2026
 */
class CourseCapacityValidatorTest {

    private static final int TEACHER_ID = 1;

    @Test
    @DisplayName("A blank or null capacity means unlimited and is valid")
    void blankCapacityIsValid() {
        assertTrue(CourseValidator.validate("CST338", "Software Design", TEACHER_ID, "")
                .isEmpty());
        assertTrue(CourseValidator.validate("CST338", "Software Design", TEACHER_ID, null)
                .isEmpty());
    }

    @Test
    @DisplayName("A positive whole-number capacity is valid")
    void positiveCapacityIsValid() {
        assertTrue(CourseValidator.validate("CST338", "Software Design", TEACHER_ID, "30")
                .isEmpty());
    }

    @Test
    @DisplayName("Zero capacity is valid and means unlimited")
    void zeroCapacityIsValid() {
        assertTrue(CourseValidator.validate("CST338", "Software Design", TEACHER_ID, "0")
                .isEmpty());
    }

    @Test
    @DisplayName("A negative capacity is rejected")
    void negativeCapacityIsRejected() {
        assertTrue(CourseValidator.validate("CST338", "Software Design", TEACHER_ID, "-3")
                .contains("Capacity cannot be negative."));
    }

    @Test
    @DisplayName("A non-integer capacity is rejected")
    void nonIntegerCapacityIsRejected() {
        assertTrue(CourseValidator.validate("CST338", "Software Design", TEACHER_ID, "thirty")
                .contains("Capacity must be a whole number."));
        assertTrue(CourseValidator.validate("CST338", "Software Design", TEACHER_ID, "3.5")
                .contains("Capacity must be a whole number."));
    }

    @Test
    @DisplayName("The capacity check runs on top of the base rules, not instead of them")
    void capacityValidationKeepsBaseRules() {
        // Blank name AND a bad capacity should both be reported.
        assertEquals(2,
                CourseValidator.validate("CST338", "", TEACHER_ID, "-1").size());
    }

    @Test
    @DisplayName("parseCapacity treats blank as unlimited and trims real numbers")
    void parseCapacityHandlesBlankAndSpaces() {
        assertEquals(0, CourseValidator.parseCapacity(""));
        assertEquals(0, CourseValidator.parseCapacity(null));
        assertEquals(25, CourseValidator.parseCapacity("  25 "));
    }

    @Test
    @DisplayName("An unlimited course is never full")
    void unlimitedCourseIsNeverFull() {
        assertFalse(CourseValidator.isFull(1000, 0));
    }

    @Test
    @DisplayName("A course is full only at or over its seat limit")
    void fullAtOrOverLimit() {
        assertFalse(CourseValidator.isFull(29, 30));
        assertTrue(CourseValidator.isFull(30, 30));
        assertTrue(CourseValidator.isFull(31, 30));
    }
}
