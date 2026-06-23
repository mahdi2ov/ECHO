package echo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private final String id;
    private final String senderId;
    private String content;
    private final LocalDateTime createdAt;
    private boolean edited;
    private boolean deleted;
    private final List<MessageEdit> editHistory;
    private final List<Reaction> reactions;
    
    // constructor
    public Message(String id, String senderId, String content) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
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
        return String.format("Message [id = %s, senderId = %s, content = %.10s, createdAt = %s, edited = %b, deleted = %b, editHistory = %s, reactions = %s]",
        this.id, this.senderId, this.content, this.createdAt, this.edited, this.deleted, this.editHistory, this.reactions);
    }
}
