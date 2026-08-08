import java.util.Objects;

/**
 * [CST338 ]
 * Represents a user account in the Grade & Assignment Tracker.
 * Stores account information used for registration, login, and user roles.
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

    public int getId() {
        return id;
    }

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

    public String getDatetime() {
        return datetime;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof User user)) {
            return false;
        }

      return getId() == user.getId() && Objects.equals(getUsername(), user.getUsername())
            && Objects.equals(getFirstName(), user.getFirstName()) && Objects.equals(
            getLastName(), user.getLastName()) && Objects.equals(getEmail(), user.getEmail())
            && Objects.equals(getPassword(), user.getPassword()) && getRole() == user.getRole()
            && Objects.equals(datetime, user.datetime);
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + Objects.hashCode(getUsername());
        result = 31 * result + Objects.hashCode(getFirstName());
        result = 31 * result + Objects.hashCode(getLastName());
        result = 31 * result + Objects.hashCode(getEmail());
        result = 31 * result + Objects.hashCode(getPassword());
        result = 31 * result + Objects.hashCode(getRole());
        result = 31 * result + Objects.hashCode(datetime);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
