package echo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import echo.util.DateTimeUtil;

public class Message implements Comparable<Message> {
    private final String id;
    private final String senderId;
    private final String conversationId;
    private String content;
    private final LocalDateTime createdAt;
    private boolean edited;
    private boolean deleted;
    private final List<MessageEdit> editHistory;
    private final List<Reaction> reactions;
    
    // constructor
    public Message(String id, String senderId,String conversationId, String content) {
        this.id = id;
        this.senderId = senderId;
        this.conversationId = conversationId;
        this.content = content;
        this.createdAt = DateTimeUtil.now();
        this.edited = false;
        this.deleted = false;
        this.editHistory = new ArrayList<>();
        this.reactions = new ArrayList<>();
    }
    
    // getters and setters
    public String getId() {
        return this.id;
    }
    public String getSenderId() {
        return this.senderId;
    }
    public String getConversationId() {
        return conversationId;
    }
    public String getContent() {
        return this.content;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public boolean getEdited() {
        return this.edited;
    }
    public boolean getDeleted() {
        return this.deleted;
    }
    public List<MessageEdit> getEditHistory() {
        return this.editHistory;
    }
    public List<Reaction> getReactions() {
        return this.reactions;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setEdited(boolean edited) {
        this.edited = edited;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // equals, hashCode and toString
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Message other = (Message) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Message [id=" + id + ", senderId=" + senderId + ", conversationId=" + conversationId + ", content="
                + content + ", createdAt=" + createdAt + ", edited=" + edited + ", deleted=" + deleted
                + ", editHistory=" + editHistory + ", reactions=" + reactions + "]";
    }

    // compare messages
    @Override
    public int compareTo(Message other) {
        return this.createdAt.compareTo(other.createdAt);
    }
}
