import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class AssignmentTest
{
    @Test
    void negativePointsThrowsException()
    {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Assignment(
                        1,
                        "Test Assignment",
                        "Testing negative points",
                        LocalDate.of(2026, 8, 15),
                        -1
                )
        );
    }
}