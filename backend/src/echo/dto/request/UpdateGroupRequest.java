package echo.dto.request;

public class UpdateGroupRequest {
    private final String requesterId;
    private final String groupId;
    private final String name;
    private final String description;
    private final String profileImagePath;

    // constructor
    public UpdateGroupRequest(String requesterId, String groupId, String name, String description, String profileImagePath) {
        this.requesterId = requesterId;
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.profileImagePath = profileImagePath;
    }

    // getters
    public String getRequesterId() {
        return requesterId;
    }
    public String getGroupId() {
        return groupId;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getProfileImagePath() {
        return profileImagePath;
    }
}
