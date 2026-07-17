package echo.dto.request;

public class SavedMessageRequest {
    private final String userId;
    private final String messageId;
    
    // constructor
    public SavedMessageRequest(String userId, String messageId) {
        this.userId = userId;
        this.messageId = messageId;
    }

    // getters
    public String getUserId() {
        return userId;
    }
    public String getMessageId() {
        return messageId;
    }
}
