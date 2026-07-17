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
import echo.model.User;
import echo.repository.UserRepository;
import echo.util.FileUtil;

public class FileUserRepository implements UserRepository {
    private final Path userData;
    private final List<User> inMemoryUserData;
    
    // constructor
    public FileUserRepository() {
        userData = Paths.get(AppConfig.getUsersPath());
        inMemoryUserData = new ArrayList<>();
        readFile();
    }

    // create
    @Override
    public synchronized void saveUser(User user) {
        inMemoryUserData.add(user);
        writeFile();
    }

    // read
    @Override
    public synchronized User getUserById(String id) {
        for (User user : inMemoryUserData) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public synchronized User getUserByUsername(String username) {
        for (User user : inMemoryUserData) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public synchronized boolean existByUsername(String username) {
        return getUserByUsername(username) != null;
    }

    @Override
    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(inMemoryUserData);
    }

    // update
    @Override
    public synchronized void updateUser(User user) {
        for (int i = 0; i < inMemoryUserData.size(); i++) {
            if (inMemoryUserData.get(i).getId().equals(user.getId())) {
                inMemoryUserData.set(i, user);
            }
        }
        writeFile();
    }

    // delete
    @Override
    public synchronized void deleteUserById(String id) {
        for (int i = 0; i < inMemoryUserData.size(); i++) {
            if (inMemoryUserData.get(i).getId().equals(id)) {
                inMemoryUserData.remove(i);
                break;
            }
        }
        writeFile();
    }

    @Override
    public synchronized void deleteUserByUsername(String username) {
        for (int i = 0; i < inMemoryUserData.size(); i++) {
            if (inMemoryUserData.get(i).getUsername().equals(username)) {
                inMemoryUserData.remove(i);
                break;
            }
        }
        writeFile();
    }

    // util methods
    @Override
    public synchronized List<User> getUsersByUsername(String searchText) {
        List<User> usersFound = new ArrayList<>();
        for (int i = 0; i < inMemoryUserData.size(); i++) {
            if (inMemoryUserData.get(i).getUsername().contains(searchText)) {
                usersFound.add(inMemoryUserData.get(i));
            }
        }
        return usersFound;
    }

    // private util methods for using in this class
    private synchronized void readFile() {
        inMemoryUserData.clear();

        try (BufferedReader reader = Files.newBufferedReader(userData, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                inMemoryUserData.add(FileUtil.deserializeUser(line));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    private synchronized void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(userData, StandardCharsets.UTF_8)) {
            for (User user : inMemoryUserData) {
                writer.write(FileUtil.serializeUser(user));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
