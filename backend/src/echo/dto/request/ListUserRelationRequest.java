package echo.dto.request;

public class ListUserRelationRequest {
    private final String userId;
    
    // constructor
    public ListUserRelationRequest(String userId) {
        this.userId = userId;
    }
    
    // getter
    public String getUserId() {
        return userId;
    }
}
