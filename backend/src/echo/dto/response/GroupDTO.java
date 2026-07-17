package echo.dto.response;

import java.util.List;

import echo.model.Group;

public class GroupDTO {
    private final String id;
    private final String name;
    private final String description;
    private final String profileImagePath;
    private final String conversationId;
    private final String ownerId;
    private final List<String> adminsId;

    // constructor
    public GroupDTO(String id, String name, String description, String profileImagePath,
                    String conversationId, String ownerId, List<String> adminsId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.profileImagePath = profileImagePath;
        this.conversationId = conversationId;
        this.ownerId = ownerId;
        this.adminsId = adminsId;
    }

    // getters
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getProfileImagePath() {
        return profileImagePath;
    }
    public String getConversationId() {
        return conversationId;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public List<String> getAdminsId() {
        return adminsId;
    }

    // converting model object into DTO object
    public static GroupDTO toGroupDTO(Group group) {
        return new GroupDTO( group.getId(), group.getName(), group.getDescription(), group.getProfileImagePath(),
                        group.getConversationId(), group.getOwnerId(), group.getAdminsId());
    }
}
