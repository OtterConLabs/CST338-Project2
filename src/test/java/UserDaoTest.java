import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *  Tests the insert, login, retrieval, update, and delete operations
 *  performed by UserDao.
 *
 * @author Yoko Mohr
 * @since 7/30/2026
 */

class UserDaoTest {

  private UserDao userDao;
  private User user;

  // Creates a new UserDao and test User before each test.
  @BeforeEach
  void setUp() {
    userDao = new UserDao();

    user = new User(
        "unit_test",
        "Unit",
        "Test",
        "unit_test@csumb.edu",
        "unit_test1",
        UserRole.STUDENT
    );
  }

  // Removes any test User remaining in the database after each test.
  // The method checks both the original and updated login credentials.
  @AfterEach
  void tearDown() {
    // First check whether the update test changed the login credentials.
    User savedUser = userDao.checkLogin("updated_username", "updated_password");
    // If the updated User was not found, look for the original User.
    if (savedUser == null) {
      savedUser = userDao.checkLogin(user.getUsername(), user.getPassword());
    }
    // Delete the test User only when it exists.
    if (savedUser != null) {
      userDao.deleteUser(savedUser);
    }
  }
  @AfterAll
  static void close() {
    DatabaseManager.getInstance().close();
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

  // TODO duplicate email addresses
  // TODO searching for a user who does not exist
  // TODO invalid username
  // TODO invalid password
}