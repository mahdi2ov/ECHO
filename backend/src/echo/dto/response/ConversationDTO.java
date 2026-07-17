package echo.dto.response;

import java.time.LocalDateTime;

import echo.model.Conversation;
import echo.model.ConversationType;

public class ConversationDTO {
    private final String id;
    private final String title;
    private final String profileImagePath;
    private final boolean pinned;
    private final boolean archived;
    private final LocalDateTime lastMessageAt;
    private final ConversationType type;

    // constructor
    public ConversationDTO(String id, String title, String profileImagePath, boolean pinned, boolean archived,
                                  LocalDateTime lastMessageAt, ConversationType type) {
        this.id = id;
        this.title = title;
        this.profileImagePath = profileImagePath;
        this.pinned = pinned;
        this.archived = archived;
        this.lastMessageAt = lastMessageAt;
        this.type = type;
    }

    // getters
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getProfileImagePath() {
        return profileImagePath;
    }
    public boolean isPinned() {
        return pinned;
    }
    public boolean isArchived() {
        return archived;
    }
    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }
    public ConversationType getType() {
        return type;
    }

    // converting model object into DTO object
    public static ConversationDTO toConversationDTO(Conversation conversation, String title, String profileImagePath) {
        return new ConversationDTO(conversation.getId(), title, profileImagePath, conversation.getPinned(),
                    conversation.getArchived(), conversation.getLastMessageAt(), conversation.getType());
    }
}
