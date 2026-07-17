package echo.dto.request;

// for add/delete account and blocked user
public class UserRelationRequest {
    private final String userId;
    private final String otherUserId;

    // constructor
    public UserRelationRequest(String userId, String contactId) {
        this.userId = userId;
        this.otherUserId = contactId;
    }

    // getters
    public String getUserId() {
        return userId;
    }
    public String getOtherUserId() {
        return otherUserId;
    }
}
