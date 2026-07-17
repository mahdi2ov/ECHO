package echo.dto.request;

import echo.model.ReactionType;

public class AddReactionRequest {
    private final String messageId;
    private final String userId;
    private final ReactionType reactionType;

    // constructor
    public AddReactionRequest(String messageId, String userId, ReactionType reactionType) {
        this.messageId = messageId;
        this.userId = userId;
        this.reactionType = reactionType;
    }

    // getters
    public String getMessageId() {
        return messageId;
    }
    public String getUserId() {
        return userId;
    }
    public ReactionType getReactionType() {
        return reactionType;
    }
}
