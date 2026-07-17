package echo.dto.response;

import echo.model.User;

public class UserDTO {
    private final String id;
    private final String username;
    private final String profileImagePath;

    // constructor
    public UserDTO(String id, String username, String profileImagePath) {
        this.id = id;
        this.username = username;
        this.profileImagePath = profileImagePath;
    }

    // getters
    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getProfileImagePath() {
        return profileImagePath;
    }

    // converting model object into DTO object
    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getProfileImagePath());
    }
}
