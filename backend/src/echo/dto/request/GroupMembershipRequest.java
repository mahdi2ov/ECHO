package echo.dto.request;

// for add/delete member and admin form/to group
public class GroupMembershipRequest {
    private final String requesterId;
    private final String userId;
    private final String groupId;
    
    // constructor
    public GroupMembershipRequest(String requesterId, String userId, String groupId) {
        this.requesterId = requesterId;
        this.userId = userId;
        this.groupId = groupId;
    }

    // getters
    public String getRequesterId() {
        return requesterId;
    }
    public String getUserId() {
        return userId;
    }
    public String getGroupId() {
        return groupId;
    }
}
