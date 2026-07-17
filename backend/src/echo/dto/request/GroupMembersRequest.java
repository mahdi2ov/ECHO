package echo.dto.request;

// for getting group members
public class GroupMembersRequest {
    private final String groupId;
    
    // constructor
    public GroupMembersRequest(String groupId) {
        this.groupId = groupId;
    }
    
    // getter
    public String getGroupId() {
        return groupId;
    }
}
