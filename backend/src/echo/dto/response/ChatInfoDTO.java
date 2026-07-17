package echo.dto.response;

import echo.model.ConversationType;

public class ChatInfoDTO {
    private final String conversationId;
    private final String title;
    private final String subtitle;
    private final ConversationType type;
    private final String otherUserId;
    private final String groupId;

    // constructor
    public ChatInfoDTO(String conversationId, String title, String subtitle, ConversationType type, String otherUserId,
                            String groupId) {
        this.conversationId = conversationId;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.otherUserId = otherUserId;
        this.groupId = groupId;
    }

    // getters
    public String getConversationId() {
        return conversationId;
    }
    public String getTitle() {
        return title;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public ConversationType getType() {
        return type;
    }
    public String getOtherUserId() {
        return otherUserId;
    }
    public String getGroupId() {
        return groupId;
    }
}
