/**
 * [CST338 Brief one-sentence description of what this class does.]
 *
 * @author Yoko Mohr
 * * @since 7/20/2026
 */

/**
 * [CST338 ]
 *
 * @author Yoko Mohr
 * @since 7/20/2026
 */
public class User {
    private int userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;

    public User(int userId, String username, String firstName, String lastName,
         String email, String password, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    //methods will be coming soon....

}
