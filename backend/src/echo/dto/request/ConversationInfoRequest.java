package echo.dto.request;

public class ConversationInfoRequest {
    private final String conversationId;
    private final String requesterId;
    
    // constructor
    public ConversationInfoRequest(String conversationId, String requesterId) {
        this.conversationId = conversationId;
        this.requesterId = requesterId;
    }
    
    // getters
    public String getConversationId() {
        return conversationId;
    }
    public String getRequesterId() {
        return requesterId;
    }
}
