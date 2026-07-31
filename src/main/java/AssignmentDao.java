import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * SQL Queries
 * @author Jordan Browning
 * @since 7/24/2026
 */
public class AssignmentDao {
    private final Connection conn;

    /**
     * store the connection
     */
    public AssignmentDao(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("null error.");
        }

        this.conn = conn;
    }

    /**
     * Screens the Assignment, Builds the SQL statement and creates the SQL Command,
     * executes SQL.
     * Fill in placeholders, Verify SQL, ASK for ID.
     *
     * @param assignment The Assignment to insert.
     * @return The generated assignment ID.
     * @throws SQLException If a database error occurs.
     */
    public int insert(Assignment assignment) throws SQLException {
        //Screens the Assignment
        reqAssignment(assignment);

        //Builds the SQL statement
        String sql = """

                INSERT INTO assignments (
                course_id,
                title,
                description,
                due_date,
                points_possible
                )
                VALUES (?, ?, ?, ?, ?)""";

        //Creates the SQL Command and executes SQL
        try (PreparedStatement statement = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            //Fill in the placeholders
            setStatementValues(statement, assignment);

            //Verify SQL
            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Assignment insertion affected "
                                + affectedRows
                                + " rows."
                );
            }

            //After inserting the row, ask for the new primary key
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                //Verify that an ID was returned
                if (!generatedKeys.next()) {
                    throw new SQLException(
                            "Assignment insertion did not return an ID."
                    );
                }

                //Get the generated ID
                int generatedId = generatedKeys.getInt(1);

                //Assign the generated ID to the Assignment object
                assignment.setAssignmentId(generatedId);

                //Return the generated ID
                return generatedId;
            }
        }
    }

    /**
     * Searches the database for an Assignment using its assignment ID.
     *
     * @param assignmentId The ID of the Assignment to look up.
     * @return An Optional containing the Assignment if it exists,
     * otherwise an empty Optional.
     * @throws SQLException If a database error occurs while searching.
     */
    public Optional<Assignment> findById(int assignmentId)
            throws SQLException {
        //assignment cannot be negative, return nothing if negative
        if (assignmentId <= 0) {
            return Optional.empty();
        }

        //SQL query statements
        String sql = """
                SELECT
                    assignment_id,
                    course_id,
                    title,
                    description,
                    due_date,
                    points_possible
                FROM assignments
                WHERE assignment_id = ?
                """;

        //create placeholders for the assignment before its filled in
        try (PreparedStatement statement =
                     conn.prepareStatement(sql)) {

            //fill in the placeholder with the assignment ID
            statement.setInt(1, assignmentId);

            //sends the SQL to the database.
            try (ResultSet resultSet = statement.executeQuery()) {

                //checks if the database returned an assignment
                if (resultSet.next()) {

                    //turns the database information into an Assignment and returns it
                    return Optional.of(
                            mapAssignment(resultSet)
                    );
                }

                //return nothing if the assignment was not found
                return Optional.empty();
            }
        }
    }

    /**
     * Builds the SQL statement and creates the SQL Command, executes SQL.
     * Creates a list, converts each SQL row into an Assignment object,
     * and returns all of the assignments that were found.
     *
     * @return A list containing all Assignment objects in the database.
     * @throws SQLException If a database error occurs.
     */
    public List<Assignment> findAll() throws SQLException {

        //SQL Statements
        String sql = """
            
                SELECT
                assignment_id,
                course_id,
                title,
                description,
                due_date,
                points_possible
                            FROM assignments
                            ORDER BY due_date,
                assignment_id
            """;

        //create a list to store all of the assignments
        List<Assignment> assignments = new ArrayList<>();

        //attempt to create placeholders for the assignment before its filled in
        try (PreparedStatement statement =
                     conn.prepareStatement(sql);
             //sends the SQL to the database
             ResultSet resultSet = statement.executeQuery()) {

            //keep going until there are no more assignments left
            while (resultSet.next()) {

                //convert the current SQL row into an Assignment object and add it to the list
                assignments.add(mapAssignment(resultSet));
            }
        }

        //return all of the assignments that were found
        return assignments;
    }



    /**
     * Screens the course ID, Builds the SQL statement and creates the SQL Command,
     * executes SQL.
     * Fill in placeholders, converts each SQL row into an Assignment object,
     * and returns all of the assignments for the matching course.
     *
     * @param courseId The course ID to search for.
     * @return A list containing all Assignment objects for the given course.
     * @throws IllegalArgumentException If the course ID is less than or equal to zero.
     * @throws SQLException             If a database error occurs.
     */
    public List<Assignment> findByCourseId(int courseId)
            throws SQLException
    {

        //courseId cannot be negative, thrw an error if negative
        if ( courseId <= 0)
        {
            throw new IllegalArgumentException(
                    "Course ID must be greater than zero."
            );
        }

        //SQL statements
        String sql =
                """
       
                                    assignment_id,
                                    course_id,
                
                          des
                             due_da
                       points_po
                                    FROM assignme
                   WHERE
                        course_id
                        = ?
           
                                     due_date, assignment_id
            """;

        //create a list to store all of the assignments
        List<Assignment> assignments = new ArrayList<>();

        //attempt to create placeholders for the assignment before its filled in
        try (PreparedStatement statement = conn.prepareStatement(sql))
        {
            //fill in the placeholder with the courseId
            statement.setInt(1, courseId);//sends the SQL to the database
            try ( ResultSet resultSet = statement.executeQuery())
            {
                //keep going until there a ments left
                while (resultSet.next())
                {
                    //convert the SQL row into an Assignment object and add it to the list
                    assignments.add(mapAssignment(resultSet));
                }
            }
        }

        //return all of the assignments that were found
        return assignments;


    }

    /**
     * Deletes an Assignment from the database using its assignment ID.
     *
     * @param assignmentId The assignment ID to delete.
     * @return True if the assignment was deleted, otherwise false.
     * @throws
    SQLException If a database error occurs.
     */ public boolean deleteById(int assignmentId) throws SQLException
    {
        //if le r equal return false
        if (assignmentId <= 0)
        {
            return false;
        }
        //sql statements
        String sql = """
 
                               DELETE FROM
                assignments
            WHERE assignment_id = ?
            """;

        //attempt to create placeholders for the assignment before its n
        try ( PreparedStatement statement = conn.prepareStatement(sql))
        {
            //fill in the placeholder with the assignment ID
            statement.setInt(1, assignmentId);

            //send the SQL to the database and return true if one row was deleted
            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Fills in the SQL placeholders using the values from the Assignment object.
     *
     * @param statement  The PreparedStatement that contains the SQL placeholders.
     * @param assignment The Assignment that provides the values for the SQL statement.
     * @throws SQLException If a database error occurs while filling in the placeholders.
     */
    private void setStatementValues(PreparedStatement statement, Assignment assignment)
            throws SQLException
    {
        //fill in the course ID placeholder
        statement.setInt(1, assignment.getCourseId());

        //fill in the title placeholder
        statement.setString(2, assignment.getTitle());

        //fill in the description placeholder
        statement.setString(3, assignment.getDescription());

        //fill in the due date placeholder by converting the LocalDate into a String
        statement.setString(
                4,
                assignment.getDueDate().toString()
        );

        //fill in the points possib placeholder
        statement.setInt(5, assignment.getPointsPossible());
    }

    /**
     * Takes the information returned from the database and creates
     * an Assignment object from it.
     *
     * @param resultSet The database result containing the Assignment information.
     * @return The Assignment object created from the database values.
     * @throws SQLException If a database error occurs while reading the values.
     */
    private Assignment mapAssignment(ResultSet resultSet)
            throws SQLException
    {
        //Takes the database values and creates an Assignment object
        return new Assignment(
                //Gets the assignment ID
                resultSet.getInt("assignment_id"),

                //Gets the course ID
                resultSet.getInt("course_id"),

                //Gets the assignment title
                resultSet.getString("title"),

                //Gets the assignment description
                resultSet.getString("description"),

                //Gets the due date and converts it into a LocalDate
                LocalDate.parse(
                        resultSet.getString("due_date")
                ),

                //Gets the maximum points possible
                resultSet.getInt("points_possible")
        );
    }

    /**
     * Checks that the Assignment exists before using it in a database method.
     *
     * @param assignment The Assignment being checked.
     * @throws IllegalArgumentException If the Assignmen null.
     */
    private void reqAssignment(Assignment assignment)
    { //Checks if the Assignment exists
        if (assignment == null)
        {
            //Stops the method if the Assignment does not exist
            throw new IllegalArgumentException(
                    "Assignment cannot be null."
            );
        }
    }
}



