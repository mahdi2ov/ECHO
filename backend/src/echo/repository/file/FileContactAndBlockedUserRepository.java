package echo.repository.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import echo.config.AppConfig;
import echo.repository.ContactAndBlockedUserRepository;
import echo.util.FileUtil;

public class FileContactAndBlockedUserRepository implements ContactAndBlockedUserRepository {

    private final Path contactAndBlockedUserData;
    private final Map<String, List<String>> contactsByUserId;
    private final Map<String, List<String>> blockedByUserId;

    public FileContactAndBlockedUserRepository() {
        this.contactAndBlockedUserData = Paths.get(AppConfig.getContactsAndBlockedUsersPath());
        this.contactsByUserId = new HashMap<>();
        this.blockedByUserId = new HashMap<>();

        readFile();
    }

    /// contact
    // create
    @Override
    public void addContact(String userId, String contactId) {
        List<String> contacts = contactsByUserId.get(userId);
        
        if (contacts == null) {
            contacts = new ArrayList<>();
            contactsByUserId.put(userId, contacts);
        }
        
        if (!contacts.contains(contactId)) {
            contacts.add(contactId);
            writeFile();
        }
    }

    // read
    @Override
    public List<String> getContacts(String userId) {
        List<String> contacts = contactsByUserId.get(userId);

        return contacts == null ? new ArrayList<>() : new ArrayList<>(contacts);
    }
    @Override
    public boolean isContact(String userId, String otherUserId) {
        List<String> contacts = contactsByUserId.get(userId);

        if (contacts != null) {
            return contacts.contains(otherUserId);
        }
        return false;
    }

    // update
    // delete
    @Override
    public void removeContact(String userId, String contactId) {
        List<String> contacts = contactsByUserId.get(userId);
        
        if (contacts != null) {
            contacts.remove(contactId);
            writeFile();
        }
    }

    /// blockedUser
    // create
    @Override
    public void blockUser(String userId, String blockedUserId) {
        List<String> blockedUsers = blockedByUserId.get(userId);

        if (blockedUsers == null) {
            blockedUsers = new ArrayList<>();
            blockedByUserId.put(userId, blockedUsers);
        }

        if (!blockedUsers.contains(blockedUserId)) {
            blockedUsers.add(blockedUserId);
            writeFile();
        }
    }
    
    // read
    @Override
    public List<String> getBlockedUsers(String userId) {
        List<String> blockedUsers = blockedByUserId.get(userId);
        
        return blockedUsers == null ? new ArrayList<>() : new ArrayList<>(blockedUsers);
    }
    
    @Override
    public boolean isBlocked(String userId, String otherUserId) {
        List<String> blockedUsers = blockedByUserId.get(userId);

        if (blockedUsers != null) {
            return blockedUsers.contains(otherUserId);
        }
        return false;
    }

    // update
    // delete
    @Override
    public void unblockUser(String userId, String blockedUserId) {
        List<String> blockedUsers = blockedByUserId.get(userId);

        if (blockedUsers != null) {
            blockedUsers.remove(blockedUserId);
            writeFile();
        }
    }
    
    // private util methods for using in this class
    private void readFile() {
        contactsByUserId.clear();
        blockedByUserId.clear();

        try (BufferedReader reader = Files.newBufferedReader(contactAndBlockedUserData, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] contactAndBlockedUserParts = line.split("\\|\\|", -1);
                String userId = contactAndBlockedUserParts[0];
                List<List<String>> contactsAndBlockedUsers = FileUtil.deserializeContactsAndBlockedUsers(line);

                List<String> contacts = contactsAndBlockedUsers.get(0);
                List<String> blockedUsers = contactsAndBlockedUsers.get(1);
                
                contactsByUserId.put(userId, contacts);
                blockedByUserId.put(userId, blockedUsers);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(contactAndBlockedUserData, StandardCharsets.UTF_8)) {
            Set<String> allUserIds = new HashSet<>();
            allUserIds.addAll(contactsByUserId.keySet());
            allUserIds.addAll(blockedByUserId.keySet());

            for (String userId : allUserIds) {
                List<String> contacts = contactsByUserId.get(userId);
                if (contacts == null) {
                    contacts = new ArrayList<>();
                }

                List<String> blockedUsers = blockedByUserId.get(userId);
                if (blockedUsers == null) {
                    blockedUsers = new ArrayList<>();
                }

                writer.write(FileUtil.serializeContactsAndBlockedUsers(userId, contacts, blockedUsers));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
