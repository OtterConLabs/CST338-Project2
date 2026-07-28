import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [CST338 Create DatabaseManager]
 * Manages the shared SQLite database connection and creates the application's
 * database tables.
 * The class currently also contains user-related CRUD operations. These
 * operations will later be moved to a separate UserDao class
 *
 * @author Yoko Mohr
 * @since 7/21/2026
 */

public class DatabaseManager {
    // JDBC URL for the local SQLite database file.
    private static final String DB_URL = "jdbc:sqlite:app.db";

    // Stores the single DatabaseManager instance used by the application.
    private static DatabaseManager instance;
    // Shared database connection used throughout the application.
    private Connection connection;

    // Opens the SQLite connection and creates the required tables.
    // The constructor is private so outside classes cannot create additional
    // DatabaseManager objects.
    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connected.");
            createTables();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    // Returns the shared DatabaseManager instance.
    // The instance is created only the first time this method is called.
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    // Closes the shared database connection.
    // The Singleton instance is reset after the connection is closed so a
    // new instance can be created later if necessary.
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                instance = null;
            }
        } catch (SQLException e) {
            System.out.println("Failed to close: " + e.getMessage());
        }
    }

    // TODO: Create UserDao and move user CRUD operations out of DatabaseManager.
    //  UserDao will access this shared connection through getConnection().
    public Connection getConnection() {
        return connection;
    }

    /**
     * createTables() runs when the shared database connection is first created.
     * It uses CREATE TABLE IF NOT EXISTS, so the users table is created only when it is missing.
     * The id is an auto-generated primary key. Username and email are required, unique,
     * and case-insensitive. The role column is restricted to STUDENT or TEACHER,
     * and SQLite automatically stores the account creation time.
     */
    private void createTables() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE COLLATE NOCASE,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE COLLATE NOCASE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                        CHECK (role IN ('STUDENT', 'TEACHER')),
                    created TEXT DEFAULT (datetime('now'))
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table ready.");
        } catch (SQLException e) {
            System.out.println("createTable failed: " + e.getMessage());
        }
    }

    /**
     * insertUser() receives a User object and inserts its account data into the users table.
     * The SQL uses six placeholders, and the PreparedStatement assigns each User field
     * to the matching placeholder.
     * The database generates the user ID and creation timestamp automatically.
     * executeUpdate() performs the insert, while the database constraints prevent
     * duplicate usernames or emails and reject invalid roles.
     *
     * @param user
     */
    public void insertUser(User user) {
        String sql = """
                 INSERT INTO users (
                 username, 
                 first_name, 
                 last_name, 
                 email, 
                 password, 
                 role
                 )
                 VALUES (?, ?, ?, ?, ?, ?) 
                 """;

        // Replace the six SQL placeholders ? with values from the User.
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // setting for all the ?s
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole().name());

            // actual insertion happens here.
            int rowsInserted = pstmt.executeUpdate();
            // 1 is success
            if (rowsInserted == 1) {
                System.out.println("User inserted.");
            } else {
                System.out.println("insertUser failed.");
            }
        } catch (SQLException e) {
            System.out.println("insertUser failed: " + e.getMessage());
        }
    }

    /**
     * checkLogin() searches the users table for the supplied username
     * using a prepared SELECT statement. If a row is found,
     * it compares the stored password with the password entered by the user.
     * When both credentials match, the method reconstructs and returns a complete
     * User object from the database row.
     * If the username is missing or the password does not match, it returns null.
     *
     * @param username
     * @param password
     * @return
     */
    // Checks whether the supplied username and password match a stored user.
    public User checkLogin(String username, String password) {
        // this is for prepareStatement. later ask  a specific username, the want to get its password
        // use * to receive all information
        String sql = """
                SELECT * FROM users WHERE username = ?
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Search for the specific username entered by the user.
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String pw = rs.getString("password");
                    if (pw.equals(password)) {
                        // Return a complete User object when the password matches.
                        return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            UserRole.valueOf(
                                rs.getString("role")
                            ),
                            rs.getString("created")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("checkLogin failed: " + e.getMessage());
        }
        return null;
    }


    //TODO: YOKO

    /**
     * getAllUsers() retrieves every row from the users table and
     * orders the results from newest to oldest.
     * It uses a while loop because the query may return multiple rows.
     * Each row in the ResultSet is converted into a User object and added to a list.
     * If the table has no users, the method returns an empty list rather than null.
     *
     * @return
     */

    // Retrieves all users from the users table.
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            // Convert each database row into a User object.
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    UserRole.valueOf(rs.getString("role")),
                    rs.getString("created")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("getALLUsers failed: " + e.getMessage());
        }
        return users;
    }

    /**
     * updateUser() modifies an existing row in the users table.
     * The SET clause lists the editable profile fields,
     * while WHERE id = ? identifies the exact user to update.
     * The ID is used because it remains stable even when the username changes.
     * executeUpdate() returns the number of affected rows,
     * so a result of one means the profile was updated,
     * while zero means no matching user was found.
     * @param user
     */
    // Updates the editable profile information for an existing user
    //TODO YOKO
    public boolean updateUser(User user) {
        String sql = """
            UPDATE users
            SET username = ?,
                first_name = ?,
                last_name = ?,
                email = ?,
                password = ?
            WHERE id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername().trim());
            pstmt.setString(2, user.getFirstName().trim());
            pstmt.setString(3, user.getLastName().trim());
            pstmt.setString(4, user.getEmail().trim());
            pstmt.setString(5, user.getPassword()); // no need to trim here
            pstmt.setInt(6, user.getId()); // WHERE id

            int updateProfile = pstmt.executeUpdate();
            return updateProfile == 1;
//            if (updateProfile == 1) {
//                System.out.println("User profile updated successfully.");
//            } else {
//                System.out.println("No matching user found.");
//            }
        } catch (SQLException e) {
            System.out.println("updateUser failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * deleteUser() removes one user row from the users table.
     * The WHERE id = ? clause is essential because it limits the deletion
     * to the exact user identified by the primary key.
     * executeUpdate() returns the number of deleted rows,
     * so one means the account was deleted and zero means no matching account existed.
     * After a successful deletion, the application should clear
     * the logged-in user and return to the Login screen.
     * @param user
     * @return
     */
    // Deletes the database row belonging to the specified user.
    // TODO YOKO
    public boolean deleteUser(User user) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            int deleteProfile = pstmt.executeUpdate();
            return deleteProfile == 1;
//            if (deleteProfile == 1) {
//                System.out.println("User profile deleted successfully.");
//            } else {
//                System.out.println("No matching user found.");
//            }
        } catch (SQLException e) {
            System.out.println("daleteUser failed: " + e.getMessage());
            return false;
        }

    }

}
