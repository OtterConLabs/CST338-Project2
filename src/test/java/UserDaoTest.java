import static org.junit.jupiter.api.Assertions.*;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tests the insert, login, retrieval, update, and delete operations
 * performed by UserDao using a fresh in-memory SQLite database
 * for each test.
 *
 * @author Yoko Mohr
 * @since 7/30/2026
 */

class UserDaoTest {

  private Connection connection;
  private UserDao userDao;
  private User user;

  // Creates a fresh in-memory database, users table, UserDao,
  // and test User before each test.
  @BeforeEach
  void setUp() throws SQLException {
    connection = DriverManager.getConnection("jdbc:sqlite::memory:");

    try (Statement stmt = connection.createStatement()) {
      stmt.execute("""
          CREATE TABLE users (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT NOT NULL UNIQUE,
              first_name TEXT NOT NULL,
              last_name TEXT NOT NULL,
              email TEXT NOT NULL UNIQUE,
              password TEXT NOT NULL,
              role TEXT NOT NULL,
              created TEXT DEFAULT CURRENT_TIMESTAMP
          )
          """);
    }

    userDao = new UserDao(connection);

    user = new User(
        "unit_test",
        "Unit",
        "Test",
        "unit_test@csumb.edu",
        "unit_test1",
        UserRole.STUDENT
    );
  }

  // Closes the in-memory database connection after each test.
  // Closing the connection also removes all test data created during the test.
  @AfterEach
  void tearDown() throws SQLException {
    connection.close();
  }

  // Verifies that insertUser successfully adds a User to the database.
  @Test
  void insertUser() {
    assertTrue(userDao.insertUser(user));
  }

  // Verifies that checkLogin returns the correct User when valid
  // username and password credentials are supplied.
  @Test
  void checkLogin() {
    // Add a User that can be retrieved through checkLogin.
    userDao.insertUser(user);

    User checkLoginUser = userDao.checkLogin(user.getUsername(), user.getPassword());
    assertNotNull(checkLoginUser);
    assertTrue(checkLoginUser.getId() > 0);

    // Verify that the retrieved database values match the test User.
    assertEquals(user.getUsername(), checkLoginUser.getUsername());
    assertEquals(user.getFirstName(), checkLoginUser.getFirstName());
    assertEquals(user.getLastName(), checkLoginUser.getLastName());
    assertEquals(user.getEmail(), checkLoginUser.getEmail());
    assertEquals(user.getPassword(), checkLoginUser.getPassword());
    assertEquals(user.getRole(), checkLoginUser.getRole());
  }

  // Verifies that getAllUsers includes a User previously inserted into the database.
  @Test
  void getAllUsers() {
    userDao.insertUser(user);
    List<User> allUsers = userDao.getAllUsers();
    assertNotNull(allUsers);
    assertFalse(allUsers.isEmpty());

    // Search the returned list for the test username.
    boolean wasUserFound = false;
    for (User currentUser: allUsers) {
      if (currentUser.getUsername().equals(user.getUsername())) {
        wasUserFound = true;
        break;
      }
    }
    assertTrue(wasUserFound);
  }

  // Verifies that updateUser saves the modified User information
  // and that the updated values can be retrieved from the database.
  @Test
  void updateUser() {
    userDao.insertUser(user);
    // Retrieve the saved User so the generated database ID is available.
    User checkedIn = userDao.checkLogin(user.getUsername(), user.getPassword());
    assertNotNull(checkedIn);

    // Create a new User with updated values while preserving the
    // database ID, role, and creation date.
    User updated = new User(
        checkedIn.getId(),
        "updated_username",
        "updated_firstName",
        "updatedLastName",
        "updated@email.com",
        "updated_password",
        checkedIn.getRole(),
        checkedIn.getDatetime()
    );
    boolean result = userDao.updateUser(updated);
    assertTrue(result);

    // Retrieve the User using the updated login credentials.
    User updatedUser = userDao.checkLogin(updated.getUsername(), updated.getPassword());
    assertNotNull(updatedUser);

    // Verify that each editable field was updated.
    assertEquals(updated.getUsername(), updatedUser.getUsername());
    assertEquals(updated.getFirstName(), updatedUser.getFirstName());
    assertEquals(updated.getLastName(), updatedUser.getLastName());
    assertEquals(updated.getEmail(), updatedUser.getEmail());
    assertEquals(updated.getPassword(), updatedUser.getPassword());
  }

  // Verifies that deleteUser removes a User from the database.
  @Test
  void deleteUser() {
    assertTrue(userDao.insertUser(user));

    User loggedIn = userDao.checkLogin(user.getUsername(), user.getPassword());
    assertNotNull(loggedIn);

    boolean deleted = userDao.deleteUser(loggedIn);
    assertTrue(deleted);

    // A deleted User should no longer be returned by checkLogin.
    User deletedUser = userDao.checkLogin(user.getUsername(), user.getPassword());
    assertNull(deletedUser);
  }

  // Verify that duplicate usernames are rejected.
  @Test
  void duplicateUsername_isRejected() {
    User duplicate = new User(
        "unit_test",
        "Unit",
        "Test",
        "duplicate@csumb.edu",
        "unit_test1",
        UserRole.STUDENT
    );
    assertTrue(userDao.insertUser(user));
    assertFalse(userDao.insertUser(duplicate));
  }

  @Test
  void duplicateEmail_isRejected() {
    assertTrue(userDao.insertUser(user));
    User duplicate = new User(
        "duplicate",
        "Unit",
        "Test",
        "unit_test@csumb.edu",
        "unit_test1",
        UserRole.STUDENT
    );
    assertFalse(userDao.insertUser(duplicate));
  }

  @Test
  void loginWithInvalidUsername_isRejected() {
    assertTrue(userDao.insertUser(user));

    User result = userDao.checkLogin("invalid_username", user.getPassword());
    assertNull(result);
  }

  @Test
  void loginWithInvalidPassword_isRejected() {
    assertTrue(userDao.insertUser(user));
    User result = userDao.checkLogin(user.getUsername(), "wrong_password");
    assertNull(result);
  }
}