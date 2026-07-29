//import java.sql.*;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
///**
// *
// * SQL Queries
// * @author Jordan Browning
// * @since 7/24/2026
// */
//public class AssignmentDao
//{
//    /**
//     * store the connection
//     */
//    public AssignmentDao(Connection conn)
//    {
//        if (conn == null) {
//            throw new IllegalArgumentException("null error.");
//        }
//
//        this.conn = conn;
//    }
//
//    /**
//     * Screens the Assignment, Builds the SQL statement and creates the SQL Command,
//     * executes SQL.
//     * Fill in placeholders, Verify SQL, ASK for ID.
//     *
//     * @param assignment The Assignment to insert.
//     * @return The generated assignment ID.
//     * @throws SQLException If a database error occurs.
//     */
//    public int insert(Assignment assignment) throws SQLException
//    {
//        //Screens the Assignment
//        requireAssignment(assignment);
//
//        //Builds the SQL statement
//        String sql = """
//
//        INSERT INTO assignments (
//        course_id,
//        title,
//        description,
//        due_date,
//        points_possible
//        )
//        VALUES (?, ?, ?, ?, ?)""";
//
//        //Creates the SQL Command and executes SQL
//        try (PreparedStatement statement = conn.prepareStatement(
//                sql,
//                Statement.RETURN_GENERATED_KEYS
//        ))
//        {
//
//            //Fill in the placeholders
//            setStatementValues(statement, assignment);
//
//            //Verify SQL
//            int affectedRows = statement.executeUpdate();
//
//            if (affectedRows != 1)
//            {
//                throw new SQLException(
//                        "Assignment insertion affected "
//                                + affectedRows
//                                + " rows."
//                );
//            }
//
//            //After inserting the row, ask for the new primary key
//            try (ResultSet generatedKeys = statement.getGeneratedKeys())
//            {
//
//                //Verify that an ID was returned
//                if (!generatedKeys.next())
//                {
//                    throw new SQLException(
//                            "Assignment insertion did not return an ID."
//                    );
//                }
//
//                //Get the generated ID
//                int generatedId = generatedKeys.getInt(1);
//
//                //Assign the generated ID to the Assignment object
//                assignment.setAssignmentId(generatedId);
//
//                //Return the generated ID
//                return generatedId;
//            }
//        }
//    }
