package echo.repository;

import java.util.List;
import echo.model.User;

public interface UserRepository {
    // create
    void saveUser(User user);
    
    // read
    User getUserById(String id);
    User getUserByUsername(String username);
    boolean existByUsername(String username);
    List<User> getAllUsers();

    // update
    void updateUser(User user);

    // delete
    void deleteUserById(String id);
    void deleteUserByUsername(String username);

    // util methods
    List<User> getUsersByUsername(String searchText);
}
