package echo.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import echo.model.Attachment;
import echo.model.Message;
import echo.model.MessageEdit;
import echo.model.Reaction;

public class MessageDTO {
    private final String id;
    private final String senderId;
    private final String conversationId;
    private final String content;
    private final LocalDateTime createdAt;
    private final boolean edited;
    private final boolean deleted;
    private final List<MessageEdit> editHistory;
    private final List<Reaction> reactions;
    private final Attachment attachment;

    // constructor
    public MessageDTO(String id, String senderId, String conversationId, String content,
                      LocalDateTime createdAt, boolean edited, boolean deleted,
                      List<MessageEdit> editHistory, List<Reaction> reactions, Attachment attachment) {
        this.id = id;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.content = content;
        this.createdAt = createdAt;
        this.edited = edited;
        this.deleted = deleted;
        this.editHistory = editHistory;
        this.reactions = reactions;
        this.attachment = attachment;
    }

    // getters
    public String getId() {
        return id;
    }
    public String getConversationId() {
        return conversationId;
    }
    public String getSenderId() {
        return senderId;
    }
    public String getContent() {
        return content;
    }
    public boolean isEdited() {
        return edited;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public List<MessageEdit> getEditHistory() {
        return editHistory;
    }
    public List<Reaction> getReactions() {
        return reactions;
    }
    public Attachment getAttachment() {
        return attachment;
    }
    
    // converting model object into DTO object
    public static MessageDTO toMessageDTO(Message message) {
        return new MessageDTO( message.getId(), message.getSenderId(), message.getConversationId(), message.getContent(),
                                message.getCreatedAt(), message.getEdited(), message.getDeleted(),
                                message.getEditHistory(), message.getReactions(), message.getAttachment());
    }
}
