import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for User objects.
 *
 * @author Yoko Mohr
 * @since 7/27/2026
 */
public class UserDao {
  //TODO YOKO

  private final Connection connection;

  public UserDao() {
    this.connection =
        DatabaseManager.getInstance().getConnection();
  }

  /**
   * Inserts a new User into the users table.
   *
   * @param user the User containing the new account information
   * @return true if one user was inserted; otherwise false
   */
  public boolean insertUser(User user) {
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
      return rowsInserted == 1;
    } catch (SQLException e) {
      System.out.println("insertUser failed: " + e.getMessage());
      return false;
    }
  }

  /**
   * Searches for a user with the supplied login credentials.
   *
   * @param username the username entered on the Login screen
   * @param password the password entered on the Login screen
   * @return the matching User, or null if the credentials are invalid
   */
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
          String storedPassword = rs.getString("password");
          if (storedPassword.equals(password)) {
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

  /**
   * Retrieves all users from the users table, ordered from newest to oldest.
   *
   * @return a list of all users, or an empty list if no users exist
   */
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
      System.out.println("getAllUsers failed: " + e.getMessage());
    }
    return users;
  }

  /**
   * Updates the editable profile fields for an existing user.
   *
   * @param user the User containing the updated profile information
   * @return true if one user was updated; otherwise false
   */
  public boolean updateUser(User user) {
    String sql = """
            UPDATE users
            SET username = ?,
                password = ?,
                first_name = ?,
                last_name = ?,
                email = ?                
            WHERE id = ?
            """;

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, user.getUsername().trim());
      pstmt.setString(2, user.getPassword()); // no need to trim here
      pstmt.setString(3, user.getFirstName().trim());
      pstmt.setString(4, user.getLastName().trim());
      pstmt.setString(5, user.getEmail().trim());

      pstmt.setInt(6, user.getId()); // WHERE id

      int rowsUpdated = pstmt.executeUpdate();
      return rowsUpdated == 1;
    } catch (SQLException e) {
      System.out.println("updateUser failed: " + e.getMessage());
      return false;
    }
  }

  /**
   * Deletes the database row belonging to the specified User.
   *
   * @param user the User whose account should be deleted
   * @return true if one user was deleted; otherwise false
   */
  public boolean deleteUser(User user) {
    String sql = "DELETE FROM users WHERE id = ?";

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setInt(1, user.getId());
      int rowsDeleted = pstmt.executeUpdate();
      return rowsDeleted == 1;
    } catch (SQLException e) {
      System.out.println("deleteUser failed: " + e.getMessage());
      return false;
    }
  }
}
