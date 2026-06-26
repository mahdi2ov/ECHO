package echo.service;

import java.util.List;

import echo.model.User;
import echo.repository.UserRepository;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
