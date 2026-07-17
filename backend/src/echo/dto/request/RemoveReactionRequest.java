package echo.dto.request;

import echo.model.ReactionType;

public class RemoveReactionRequest {
    private final String messageId;
    private final String userId;

    // constructor
    public RemoveReactionRequest(String messageId, String userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    // getters
    public String getMessageId() {
        return messageId;
    }
    public String getUserId() {
        return userId;
    }
}
