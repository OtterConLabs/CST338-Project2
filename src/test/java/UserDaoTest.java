import static org.junit.jupiter.api.Assertions.*;

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
    User testUser = userDao.checkLogin(user.getUsername(), user.getPassword());
    if (testUser != null) {
      userDao.deleteUser(testUser);
    }
  }

  @Test
  void insertUser() {
    boolean inserted = userDao.insertUser(user);
    assertTrue(inserted);
  }

  @Test
  void checkLogin() {
  }

  @Test
  void getAllUsers() {
  }

  @Test
  void updateUser() {
  }

  @Test
  void deleteUser() {
  }
}