package echo.service;

import java.util.ArrayList;
import java.util.List;

import echo.model.User;
import echo.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getContacts(String userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        List<User> contacts = new ArrayList<>();
        for (String contact : user.getContacts()) {
            contacts.add(userRepository.getUserById(contact));
        }

        return contacts;
    }

    public List<User> getBlockedUsers(String userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        List<User> blockedUsers = new ArrayList<>();
        for (String blockedUser : user.getBlockedUsers()) {
            blockedUsers.add(userRepository.getUserById(blockedUser));
        }
        
        return blockedUsers;
    }

    public List<User> listUsers() {
        return userRepository.getAllUsers();
    }

    public User findUser(String userId) {
        return userRepository.getUserById(userId);
    }

    public void deleteUser(String userId) {
        // cheking for user exist
        User user = userRepository.getUserById(userId); 
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        // delete user
        userRepository.deleteUserById(userId);
    }

    public User updateProfile(String userId , String username, String profileImagePath) {
        // cheking for user exist
        User user = findUser(userId);
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        // set profile changes
        user.setUsername(username);
        user.setProfileImagePath(profileImagePath);

        // update user
        userRepository.updateUser(user);
        return user;
    }

    public void clideleteUser(String username) {
        // cheking for user exist
        User user = userRepository.getUserByUsername(username); 
        if (user == null) {
            throw new RuntimeException("User not found.");
        }

        // delete user
        userRepository.deleteUserByUsername(username);
    }
}
