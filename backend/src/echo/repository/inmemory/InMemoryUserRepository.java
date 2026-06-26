package echo.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echo.model.User;
import echo.repository.UserRepository;

public class InMemoryUserRepository implements UserRepository {
    // in memory implementation for deadline one
    private final Map<String, User> usersById;

    // constructor
    public InMemoryUserRepository() {
        this.usersById = new HashMap<>();
    }
    
    // create
    @Override
    public synchronized void saveUser(User user) {
        this.usersById.put(user.getId(), user);
        
    }
    
    // read
    @Override
    public synchronized User getUserById(String id) {
        return this.usersById.get(id);
    }

    @Override
    public synchronized User getUserByUsername(String username) {
        for (User user : this.usersById.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean existByUsername(String username) {
        return this.getUserByUsername(username) != null;
    }

    @Override
    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(this.usersById.values());
    }

    // update
    @Override
    public synchronized void updateUser(User user) {
        this.usersById.put(user.getId(), user);
    }

    // delete
    @Override
    public synchronized void deleteUserById(String id) {
        this.usersById.remove(id);
    }

    @Override
    public synchronized void deleteUserByUsername(String username) {
        User userToDelete = this.getUserByUsername(username);
        if (userToDelete != null) {
            this.deleteUserById(userToDelete.getId());
        }
    }

    // util methods
    @Override
    public synchronized List<User> getUsersByUsername(String searchText) {
        List<User> UsersFound = new ArrayList<>();
        for (User user : this.usersById.values()) {
            if (user.getUsername().contains(searchText)) {
                UsersFound.add(user);
            }
        }
        return UsersFound;
    }
}
