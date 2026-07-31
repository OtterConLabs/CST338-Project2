import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *  Tests database operations performed by UserDao.
 *
 * @author Yoko Mohr
 * @since 7/30/2026
 */

class UserDaoTest {

  private UserDao userDao;
  private User user;

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

  @AfterEach
  void tearDown() {
    User savedUser = userDao.checkLogin("updated_username", "updated_password");
    if (savedUser == null) {
      savedUser = userDao.checkLogin(user.getUsername(), user.getPassword());
    }
    if (savedUser != null) {
      userDao.deleteUser(savedUser);
    }
  }

  @Test
  void insertUser() {
    boolean inserted = userDao.insertUser(user);
    assertTrue(inserted);
  }

  @Test
  void checkLogin() {
    userDao.insertUser(user);

    User checkLoginUser = userDao.checkLogin(user.getUsername(), user.getPassword());
    assertNotNull(checkLoginUser);
    assertEquals(user, checkLoginUser);
  }

  @Test
  void getAllUsers() {
    userDao.insertUser(user);
    List<User> allUsers = userDao.getAllUsers();
    assertNotNull(allUsers);
    assertFalse(allUsers.isEmpty());

    boolean wasUserFound = false;
    for (User currentUser: allUsers) {
      if (currentUser.getUsername().equals(user.getUsername())) {
        wasUserFound = true;
        break;
      }
    }
    assertTrue(wasUserFound);
  }

  @Test
  void updateUser() {
    userDao.insertUser(user);
    User checkedIn = userDao.checkLogin(user.getUsername(), user.getPassword());

    assertNotNull(checkedIn);

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

    User updatedUser = userDao.checkLogin(updated.getUsername(), updated.getPassword());
    assertNotNull(updatedUser);

    assertEquals(updated.getUsername(), updatedUser.getUsername());
    assertEquals(updated.getFirstName(), updatedUser.getFirstName());
    assertEquals(updated.getLastName(), updatedUser.getLastName());
    assertEquals(updated.getEmail(), updatedUser.getEmail());
    assertEquals(updated.getPassword(), updatedUser.getPassword());
  }

  @Test
  void deleteUser() {
  }
}