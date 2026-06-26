package echo.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echo.model.Group;
import echo.repository.GroupRepository;

public class InMemoryGroupRepository implements GroupRepository{
    // in memory implementation for deadline one
    private final Map<String, Group> groupsById;

    // constructor
    public InMemoryGroupRepository() {
        this.groupsById = new HashMap<>();
    }

    // create
    @Override
    public synchronized void saveGroup(Group group) {
        this.groupsById.put(group.getId(), group);
    }
    
    // read
    @Override
    public synchronized Group getGroupById(String id) {
        return this.groupsById.get(id);
    }
    
    @Override
    public Group getGroupByName(String name) {
        for (Group group : groupsById.values()) {
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean existById(String id) {
        return this.getGroupById(id) != null;
    }
    
    @Override
    public synchronized List<Group> getAllGroups() {
        return new ArrayList<>(this.groupsById.values());
    }
    
    // update
    @Override
    public synchronized void updateGroup(Group group) {
        this.groupsById.put(group.getId(), group);        
    }

    // delete
    @Override
    public synchronized void deleteGroupById(String id) {
        this.groupsById.remove(id);
    }

    @Override
    public void deleteGroupByName(String name) {
        for (Group group : groupsById.values()) {
            if (group.getName().equals(name)) {
                groupsById.remove(group.getId());
                break;
            }
        }
    }

    // util methods
    @Override
    public synchronized List<Group> getGroupsByName(String searchText) {
        List<Group> groupsFound = new ArrayList<>();
        for (Group group : this.groupsById.values()) {
            if (group.getName().contains(searchText)) {
                groupsFound.add(group);
            }
        }
        return groupsFound;
    }
}
