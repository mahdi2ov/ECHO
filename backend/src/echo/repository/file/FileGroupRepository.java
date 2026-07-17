package echo.repository.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import echo.config.AppConfig;
import echo.model.Group;
import echo.repository.GroupRepository;
import echo.util.FileUtil;

public class FileGroupRepository implements GroupRepository {
    private final Path groupData;
    private final List<Group> inMemoryGroupData;

    // constructor
    public FileGroupRepository() {
        groupData = Paths.get(AppConfig.getGroupsPath());
        inMemoryGroupData = new ArrayList<>();
        readFile();
    }

    // create
    @Override
    public synchronized void saveGroup(Group group) {
        inMemoryGroupData.add(group);
        writeFile();
    }

    // read
    @Override
    public synchronized Group getGroupById(String id) {
        for (Group group : inMemoryGroupData) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public synchronized Group getGroupByConversationId(String conversationId) {
        for (Group group : inMemoryGroupData) {
            if (group.getConversationId().equals(conversationId)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public synchronized Group getGroupByName(String name) {
        for (Group group : inMemoryGroupData) {
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }
    
    @Override
    public synchronized boolean existById(String id) {
        return getGroupById(id) != null;
    }

    @Override
    public synchronized List<Group> getAllGroups() {
        return new ArrayList<>(inMemoryGroupData);
    }

    // update
    @Override
    public synchronized void updateGroup(Group group) {
        for (int i = 0; i < inMemoryGroupData.size(); i++) {
            if (inMemoryGroupData.get(i).getId().equals(group.getId())) {
                inMemoryGroupData.set(i, group);
                break;
            }
        }
        writeFile();
    }

    // delete
    @Override
    public synchronized void deleteGroupById(String id) {
        for (int i = 0; i < inMemoryGroupData.size(); i++) {
            if (inMemoryGroupData.get(i).getId().equals(id)) {
                inMemoryGroupData.remove(i);
                break;
            }
        }
        writeFile();
    }

    @Override
    public synchronized void deleteGroupByName(String name) {
        for (int i = 0; i < inMemoryGroupData.size(); i++) {
            if (inMemoryGroupData.get(i).getName().equals(name)) {
                inMemoryGroupData.remove(i);
                break;
            }
        }
        writeFile();
    }

    // util methods
    @Override
    public synchronized List<Group> getGroupsByName(String searchText) {
        List<Group> groupsFound = new ArrayList<>();
        for (int i = 0; i < inMemoryGroupData.size(); i++) {
            if (inMemoryGroupData.get(i).getName().contains(searchText)) {
                groupsFound.add(inMemoryGroupData.get(i));
            }
        }
        return groupsFound;
    }

    // private util methods for using in this class
    private synchronized void readFile() {
        inMemoryGroupData.clear();

        try (BufferedReader reader = Files.newBufferedReader(groupData, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                inMemoryGroupData.add(FileUtil.deserializeGroup(line));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private synchronized void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(groupData, StandardCharsets.UTF_8)) {
            for (Group group : inMemoryGroupData) {
                writer.write(FileUtil.serializeGroup(group));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
