import java.sql.Connection;

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

}
