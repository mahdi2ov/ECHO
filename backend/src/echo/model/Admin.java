package echo.model;

public class Admin {
    private String username;
    private String passwordHash;

    // constructor
    public Admin(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // getters and setters
    public String getUsername() {
        return username;
    }
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
