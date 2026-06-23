package echo.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final String id;
    private final String name;
    private String description;
    private String profileImagePath;
    private final List<String> adminsId;
    private final List<String> membersId;
    
    // constructor
    public Group(String id, String name) {
        this.id = id;
        this.name = name;
        this.description = null;
        this.profileImagePath = null;
        this.adminsId = new ArrayList<>();
        this.membersId = new ArrayList<>();
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
    public List<String> getAdminsId() {
        return this.adminsId;
    }
    public List<String> getMembersId() {
        return this.membersId;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
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
        return "Group [id = " + this.id + ", name = " + this.name + ", description = " + this.description + ", profileImagePath = "
                + profileImagePath + ", adminsId = " + this.adminsId + ", membersId = " + this.membersId + "]";
    }
}
