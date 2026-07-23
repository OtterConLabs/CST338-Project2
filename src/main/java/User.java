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
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private String datetime;

    public User(String username, String firstName, String lastName,
                String email, String password, UserRole role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(int id, String username, String firstName, String lastName,
                String email, String password, UserRole role, String datetime) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.datetime = datetime;
    }

    //methods will be coming soon....

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

}
