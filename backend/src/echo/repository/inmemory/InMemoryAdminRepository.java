package echo.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echo.config.AppConfig;
import echo.model.Admin;
import echo.repository.AdminRepository;
import echo.security.PasswordHasher;

public class InMemoryAdminRepository implements AdminRepository {
    // in memory implementation for deadline one
    private final Map<String, Admin> adminsByUsername;
    
    // construcor
    public InMemoryAdminRepository() {
        this.adminsByUsername = new HashMap<>();
        String username = AppConfig.getDefaultUsername();
        String password = AppConfig.getDefaultPassword();
        String salt = PasswordHasher.randomString();
        String hash = PasswordHasher.hash(password, salt);
        Admin defaultAdmin = new Admin(username, hash);
        defaultAdmin.setPasswordSalt(salt);
        this.adminsByUsername.put(username, defaultAdmin);
    }

    // create
    @Override
    public synchronized void saveAdmin(Admin admin) {
        adminsByUsername.put(admin.getUsername(), admin);
    }
    
    // read
    @Override
    public synchronized boolean existByUsername(String username) {
        return getAdminByUsername(username) != null;
    }

    @Override
    public synchronized Admin getAdminByUsername(String username) {
        return adminsByUsername.get(username); 
    }
    
    @Override
    public List<Admin> getAllAdmins() {
        return new ArrayList<>(adminsByUsername.values());
    }

    // update
    @Override
    public synchronized void updateAdmin(Admin admin) {
        adminsByUsername.put(admin.getUsername(), admin);
    }

    // delete
    @Override
    public synchronized void deleteAdminByUsername(String username) {
        adminsByUsername.remove(username);
    }
}
