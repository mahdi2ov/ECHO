package echo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String email;
    private final String id;
    private String passwordHash;
    private String passwordsalt;
    private String profileImagePath;
    private boolean locked;
    private LocalDateTime lockUntil;
    private final List<String> contacts;
    private final List<String> blockedUsers;
    
    // constructor
    public User(String username, String email, String id, String passwordHash) {
        this.username = username;
        this.email =email;
        this.id = id;
        this.passwordHash = passwordHash;
        this.passwordsalt = null;
        this.profileImagePath = null;
        this.locked = false;
        this.lockUntil = null;
        this.contacts = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
    }

    // getters and setters
    public String getUsername() {
        return this.username;
    }
    public String getEmail() {
        return email;
    }
    public String getId() {
        return this.id;
    }
    public String getPasswordHash() {
        return this.passwordHash;
    }
    public String getPasswordsalt() {
        return passwordsalt;
    }
    public String getProfileImagePath() {
        return this.profileImagePath;
    }
    public boolean getLocked() {
        return this.locked;
    }
    public LocalDateTime getLockUntil() {
        return this.lockUntil;
    }
    public List<String> getContacts() {
        return this.contacts;
    }
    public List<String> getBlockedUsers() {
        return this.blockedUsers;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public void setPasswordsalt(String passwordsalt) {
        this.passwordsalt = passwordsalt;
    }
    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public void setLockUntil(LocalDateTime lockUntil) {
        this.lockUntil = lockUntil;
    }
    
    // eauals, hashCode and toString
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "User [username=" + username + ", id=" + id + ", passwordHash=" + passwordHash + ", locked=" + locked
                + "]";
    }
}
