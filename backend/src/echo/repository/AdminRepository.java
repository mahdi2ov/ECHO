package echo.repository;

import java.util.List;

import echo.model.Admin;

public interface AdminRepository {
    // create
    void saveAdmin(Admin admin);
    
    // read
    Admin getAdminByUsername(String username);
    boolean existByUsername(String username);
    List<Admin> getAllAdmins();

    // update
    void updateAdmin(Admin admin);

    // delete
    void deleteAdminByUsername(String username);
}
