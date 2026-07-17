package echo.repository;

import java.util.List;

public interface ContactAndBlockedUserRepository {
    /// contact
    // create
    void addContact(String userId, String contactId);
    
    // read
    List<String> getContacts(String userId);
    boolean isContact(String userId, String otherUserId);

    // update
    // no method for update because we can only add or remove contact

    // delete
    void removeContact(String userId, String contactId);

    /// block user
    //create
    void blockUser(String userId, String blockedUserId);
    
    // read
    List<String> getBlockedUsers(String userId);
    boolean isBlocked(String userId, String otherUserId);
    
    // update    
    // no method for update because we can only add or remove blocedUser
    
    // delete
    void unblockUser(String userId, String blockedUserId); 
}
