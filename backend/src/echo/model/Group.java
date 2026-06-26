package echo.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final String id;
    private String name;
    private String description;
    private String profileImagePath;
    private String conversationId;
    private String ownerId;
    private final List<String> adminsId;
    
    // constructor
    public Group(String id, String name,String ownerId, String conversationId) {
        this.id = id;
        this.name = name;
        this.description = null;
        this.profileImagePath = null;
        this.conversationId = conversationId;
        this.ownerId = ownerId;
        this.adminsId = new ArrayList<>();
    }
    
    // getters and setters
    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public String getProfileImagePath() {
        return this.profileImagePath;
    }
    public String getConversationId() {
        return conversationId;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public List<String> getAdminsId() {
        return this.adminsId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
        Group other = (Group) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Group [id=" + id + ", name=" + name + ", ownerId=" + ownerId + "]";
    }
}
