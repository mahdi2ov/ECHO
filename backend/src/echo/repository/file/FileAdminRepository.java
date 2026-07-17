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
import echo.model.Admin;
import echo.repository.AdminRepository;
import echo.security.PasswordHasher;
import echo.util.FileUtil;

public class FileAdminRepository implements AdminRepository {
    private final Path adminData;
    private final List<Admin> inMemoryAdminData;

    // constructor
    public FileAdminRepository() {
        adminData = Paths.get(AppConfig.getAdminCLIPath());
        inMemoryAdminData = new ArrayList<>();
        readFile();
        if (inMemoryAdminData.isEmpty()) {
            String username = AppConfig.getDefaultUsername();
            String password = AppConfig.getDefaultPassword();
            String salt = PasswordHasher.randomString();
            String hash = PasswordHasher.hash(password, salt);
            Admin defaultAdmin = new Admin(username, hash);
            defaultAdmin.setPasswordSalt(salt);
            inMemoryAdminData.add(defaultAdmin);
            writeFile();
        }
    }

    // create
    @Override
    public void saveAdmin(Admin admin) {
        inMemoryAdminData.add(admin);
        writeFile();
    }

    // read
    @Override
    public Admin getAdminByUsername(String username) {
        for (Admin admin : inMemoryAdminData) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        return null;
    }

    @Override
    public boolean existByUsername(String username) {
        return getAdminByUsername(username) != null;
    }

    @Override
    public List<Admin> getAllAdmins() {
        return new ArrayList<>(inMemoryAdminData);
    }

    // update
    @Override
    public void updateAdmin(Admin admin) {
        for (int i = 0; i < inMemoryAdminData.size(); i++) {
            if (inMemoryAdminData.get(i).getUsername().equals(admin.getUsername())) {
                inMemoryAdminData.set(i, admin);
                break;
            }
        }
        writeFile();
    }

    // delete
    @Override
    public void deleteAdminByUsername(String username) {
        for (int i = 0; i < inMemoryAdminData.size(); i++) {
            if (inMemoryAdminData.get(i).getUsername().equals(username)) {
                inMemoryAdminData.remove(i);
                break;
            }
        }
        writeFile();
    }

    // private util methods for using in this class
    private void readFile() {
        inMemoryAdminData.clear();
        
        try (BufferedReader reader = Files.newBufferedReader(adminData, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                inMemoryAdminData.add(FileUtil.deserializeAdmin(line));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void writeFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(adminData, StandardCharsets.UTF_8)) {
            for (Admin admin : inMemoryAdminData) {
                writer.write(FileUtil.serializeAdmin(admin));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
