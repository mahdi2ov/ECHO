package echo.repository;

import java.util.List;
import echo.model.Group;

public interface GroupRepository {
    // create
    void saveGroup(Group group);

    // read
    Group getGroupById(String id);
    Group getGroupByName(String name);
    boolean existById(String id);
    List<Group> getAllGroups();

    // update
    void updateGroup(Group group);

    // delete
    void deleteGroupById(String id);
    void deleteGroupByName(String name);

    // util methods
    List<Group> getGroupsByName(String searchText);
}
