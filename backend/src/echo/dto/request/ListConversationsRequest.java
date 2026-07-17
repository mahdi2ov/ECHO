package echo.dto.request;

public class ListConversationsRequest {
    private final String userId;
    // constructor
    public ListConversationsRequest(String userId) {
        this.userId = userId;
    }
    
    // getter
    public String getUserId() {
        return userId;
    }
}
