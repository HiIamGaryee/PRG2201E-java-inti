public class User {
    private int id; // Add an id field for the user
    private String username;
    private String password;

    // Constructor without id (for when the user is first created in the database)
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Constructor with id (used when retrieving a user from the database)
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username; // Display username in the JList
    }
}
