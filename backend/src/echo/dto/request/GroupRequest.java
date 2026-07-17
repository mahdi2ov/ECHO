package echo.dto.request;

// for create/delete groups
public class GroupRequest {
    private final String requesterId;
    private final String name;

    // constructor
    public GroupRequest(String ownerId, String name) {
        this.requesterId = ownerId;
        this.name = name;
    }

    // getters
    public String getRequesterId() {
        return requesterId;
    }
    public String getName() {
        return name;
    }
}
