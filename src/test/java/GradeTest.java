import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GradeTest {

    @Test
    void validGradeStoresValues(){
        Grade grade = new Grade(7, 14, 92.5, "Good");

        assertEquals(7, grade.getAssignmentID());
        assertEquals(14, grade.getStudentID());
        assertEquals(92.5, grade.getScore(), 0.001);
        assertEquals("Good", grade.getFeedback());
    }

    @Test
    void nullFeedback(){
        Grade grade = new Grade(3, 18, 76.8, null);

        assertEquals("", grade.getFeedback());
    }

    @Test
    void zeroScore(){
        Grade grade = new Grade(9, 21, 0, "");

        assertEquals(0, grade.getScore(), 0.001);
    }

    @Test
    void negativeScore(){
        assertThrows(
                IllegalArgumentException.class,
                () -> new Grade(5, 12, -4.5, "")
        );
    }

    @Test
    void nanScore(){
        assertThrows(
                IllegalArgumentException.class,
                () -> new Grade(8, 25, Double.NaN, "")
        );
    }

    @Test
    void infiniteScore(){
        assertThrows(
                IllegalArgumentException.class,
                () -> new Grade(11, 32, Double.POSITIVE_INFINITY, "")
        );
    }

    @Test
    void invalidAssignmentID(){
        assertThrows(
                IllegalArgumentException.class,
                () -> new Grade(0, 16, 88.4, "")
        );
    }

    @Test
    void invalidStudentID(){
        assertThrows(
                IllegalArgumentException.class,
                () -> new Grade(6, 0, 81.7, "")
        );
    }
}
