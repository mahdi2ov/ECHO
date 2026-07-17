package echo.service;

import echo.model.User;
import echo.repository.UserRepository;

/*  because the text database will be ready for deadline two and implementation of deadline one is in memory,
    this class uses UserRepository.java instead of ContactRepository. */
public class ContactService {
    private final UserRepository userRepository;

    // constructor
    public ContactService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // TODO: methods input validation
    // TODO: make spacial exceptions

    public synchronized void addContact(String userId, String contactId) {
        // user can't make himself contact
        if (userId.equals(contactId)) {
            throw new RuntimeException("You can't make yourself your contact.");
        }
        
        // getting users and check for exist
        User user = userRepository.getUserById(userId);
        User contactUser = userRepository.getUserById(contactId);
        if (user == null || contactUser == null) {
            throw new RuntimeException("User or requset contact doesn't exist.");
        }

        // add contact
        if (user.getContacts().contains(contactId)) {
            throw new RuntimeException("Contact already exists.");
        }
        user.getContacts().add(contactId);

        // update user
        userRepository.updateUser(user);
    }
    
    public synchronized void deleteContact(String userId, String contactId) {
        // user isn't him contact
        if (userId.equals(contactId)) {
            throw new RuntimeException("You are not your contact!");
        }

        // getting users and check for exist
        User user = userRepository.getUserById(userId);
        User contactUser = userRepository.getUserById(contactId);
        if (user == null || contactUser == null) {
            throw new RuntimeException("User or requset contact doesn't exist.");
        }

        // delete contact
        user.getContacts().remove(contactId);

        // update user
        userRepository.updateUser(user);
    }

    public synchronized void blockUser(String userId, String blockedId) {
        // user can't block himself
        if (userId.equals(blockedId)) {
            throw new RuntimeException("You can't block yourself.");
        }
        
        // getting users and check for exist
        User user = userRepository.getUserById(userId);
        User blockedUser = userRepository.getUserById(blockedId);
        if (user == null || blockedUser == null) {
            throw new RuntimeException("User or requseted block doesn't exist.");
        }

        // block user
        if (user.getBlockedUsers().contains(blockedId)) {
            throw new RuntimeException("User already blocked.");
        }
        user.getBlockedUsers().add(blockedId);

        // update user
        userRepository.updateUser(user);
    }
    public synchronized void unblockUser(String userId, String blockedId) {
        // user can't unblock himself
        if (userId.equals(blockedId)) {
            throw new RuntimeException("You can't unblock yourself.");
        }
        
        // getting users and check for exist
        User user = userRepository.getUserById(userId);
        User blockedUser = userRepository.getUserById(blockedId);
        if (user == null || blockedUser == null) {
            throw new RuntimeException("User or requseted block doesn't exist.");
        }

        // unblock user
        user.getBlockedUsers().remove(blockedId);

        // update user
        userRepository.updateUser(user);
    }
}
