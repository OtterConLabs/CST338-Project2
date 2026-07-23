import java.sql.*;

/**
 * [CST338 Create DatabaseManager]
 * "jdbc:sqlite:" tells JDBC which driver to use.
 * The path after it is the database file location
 * @author Yoko Mohr
 * @since 7/21/2026
 */

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:app.db";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connected.");
            createTables();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to close: " + e.getMessage());
        }
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

    // Table name is users
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

    //CRUD - Create (INSERT)
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

        //fill in ?s from 1 to 6
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
    //check login validate username
    public User checkLogin(String username, String password) {
        // this is for prepareStatement. later ask  a specific username, the want to get its password
        // use * to receive all information
        String sql = """
                SELECT * FROM users WHERE username = ?
                """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            // select password and email columns from all rows in the database where <<username column>> == <<username variable>>
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String pw = rs.getString("password");
                if(pw.equals(password)) {
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
        } catch (SQLException e) {
            // ???
            System.out.println("Invalid login" + e.getMessage());
        }
        // query the db
        // if row count == 0 user does not  exist
        // if row count == 1 user does exist
        // row count cannot be anything else
        // if row count = 1 then extract password frpm response
        // comparepw from db with pw from user
        // if equal return true else false
        return null;
    }


//
//    //CRUD - Read (SELECT)
//    public List<String > getAllItems() {
//        List<String > items = new ArrayList<>();
//        String sql = "SELECT name FROM items WHERE done = 0 ORDER BY created DESC";
//
//        try (Statement stmt = connection.createStatement();
//             ResultSet rs   = stmt.executeQuery(sql)) {
//            while (rs.next()) {
//                items.add(rs.getString("name"));
//            }
//        } catch (SQLException e) {
//            System.out.println("getALLITEMS failed: " + e.getMessage());
//        }
//        return items;
//    }
//
//    //CRUD - Update
//    public void markDone(int id) {
//        String sql = "UPDATE items SET done = 1 WHERE id = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println("markDone failed: + e.getMessage()");
//        }
//    }
//
//    //CRUD - Delete
//    public void deleteItem(int id) {
//        String sql = "DELETE FROM items WHERE id = ?";
//        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//        } catch (SQLException e) {
//            System.out.println("daleteItem failed: " + e.getMessage());
//        }
//
//    }

}
