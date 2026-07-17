package echo.dto.request;

// only use for user profile
public class ProfileRequest {
    private final String userId;
    private final String username;
    private final String profileImagePath;

    // constructor
    public ProfileRequest(String userId, String username, String profileImagePath) {
        this.userId = userId;
        this.username = username;
        this.profileImagePath = profileImagePath;
    }

    // getters
    public String getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getProfileImagePath() {
        return profileImagePath;
    }
}
