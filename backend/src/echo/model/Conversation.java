package echo.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private final String id;
    private final ConversationType type;
    private boolean pinned;
    private boolean archived;
    private LocalDateTime lastMessageAt;
    private final List<String> membersId;
    
    // constructor
    public Conversation(String id, ConversationType type) {
        this.id = id;
        this.type = type;
        this.pinned = false;
        this.archived = false;
        this.lastMessageAt = LocalDateTime.now();
        this.membersId = new ArrayList<>();
    }
    
    // getters and setters
    public String getId() {
        return this.id;
    }
    public ConversationType getType() {
        return this.type;
    }
    public boolean getPinned() {
        return this.pinned;
    }
    public boolean getArchived() {
        return this.archived;
    }
    public LocalDateTime getLastMessageAt() {
        return this.lastMessageAt;
    }
    public List<String> getMembersId() {
        return this.membersId;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
    public void setArchived(boolean archived) {
        this.archived = archived;
    }
    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
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
        Conversation other = (Conversation) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Conversation [id = " + this.id + ", type = " + this.type + ", pinned = " + this.pinned + ", archived = " + this.archived
                + ", lastMessageAt = " + this.lastMessageAt + ", membersId = " + this.membersId + "]";
    }
}
