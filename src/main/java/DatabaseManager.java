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
     *     private int id;
     *     private String username;
     *     private String firstName;
     *     private String lastName;
     *     private String email;
     *     private String password;
     *     private UserRole role;
     */

    // Creates the users table.
    // The username and email columns are case-insensitive and must be unique.
    // The role column accepts only STUDENT or TEACHER.
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

    // Inserts a new user into the users table.
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
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPassword());
            pstmt.setString(6, user.getRole().name());

            pstmt.executeUpdate();
            System.out.println("User inserted.");
        } catch (SQLException e) {
            System.err.println("insertUser failed: " + e.getMessage());
        }
    }

    /**
     *      CREATE TABLE IF NOT EXISTS users (
     *      id INTEGER PRIMARY KEY AUTOINCREMENT,
     *      username TEXT NOT NULL UNIQUE COLLATE NOCASE,
     *      first_name TEXT NOT NULL,
     *      last_name TEXT NOT NULL,
     *      email TEXT NOT NULL UNIQUE COLLATE NOCASE,
     *      password TEXT NOT NULL,
     *      role TEXT NOT NULL
     *      CHECK (role IN ('STUDENT', 'TEACHER')),
     *      created TEXT DEFAULT (datetime('now'))
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
     *      CREATE TABLE IF NOT EXISTS users (
     *      id INTEGER PRIMARY KEY AUTOINCREMENT,
     *      username TEXT NOT NULL UNIQUE COLLATE NOCASE,
     *      first_name TEXT NOT NULL,
     *      last_name TEXT NOT NULL,
     *      email TEXT NOT NULL UNIQUE COLLATE NOCASE,
     *      password TEXT NOT NULL,
     *      role TEXT NOT NULL
     *      CHECK (role IN ('STUDENT', 'TEACHER')),
     *      created TEXT DEFAULT (datetime('now'))
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

    // Updates the editable profile information for an existing user
    //TODO YOKO
    public void updateUser(User user) {
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
            pstmt.setString(5, user.getPassword());
            pstmt.setInt(6, user.getId());

            int updateProfile = pstmt.executeUpdate();
            if (updateProfile == 1) {
                System.out.println("User profile updated successfully.");
            } else {
                System.out.println("No matching user found.");
            }
        } catch (SQLException e) {
            System.out.println("updateUser failed: " + e.getMessage());
        }
    }


    // Deletes the database row belonging to the specified user.
    // TODO YOKO
    public void deleteUser(User user) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            int deleteProfile = pstmt.executeUpdate();
            if (deleteProfile == 1) {
                System.out.println("User profile deleted successfully.");
            } else {
                System.out.println("No matching user found.");
            }
        } catch (SQLException e) {
            System.out.println("daleteUser failed: " + e.getMessage());
        }

    }

}
