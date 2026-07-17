package echo.dto.request;

public class DeleteMessageRequest {
    private final String messageId;
    private final String requesterId;
    
    // conastructor
    public DeleteMessageRequest(String messageId, String requesterId) {
        this.messageId = messageId;
        this.requesterId = requesterId;
    }
    
    // getters
    public String getMessageId() {
        return messageId;
    }
    public String getRequesterId() {
        return requesterId;
    }
}
