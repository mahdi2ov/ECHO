package echo.dto.request;

import java.time.LocalDateTime;

public class PollMessagesRequest {
    private final String conversationId;
    private final LocalDateTime since;

    // constructor
    public PollMessagesRequest(String conversationId, LocalDateTime since) {
        this.conversationId = conversationId;
        this.since = since;
    }

    // getters
    public String getConversationId() {
        return conversationId;
    }
    public LocalDateTime getSince() {
        return since;
    }
}
