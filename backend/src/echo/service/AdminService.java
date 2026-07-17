package echo.service;

import java.util.List;

import echo.model.Admin;
import echo.model.Group;
import echo.model.Report;
import echo.model.User;
import echo.repository.AdminRepository;
import echo.security.PasswordHasher;

public class AdminService {
    private final AuthService authService;
    private final UserService userService;
    private final GroupService groupService;
    private final ChatService chatService;
    private final AdminRepository adminRepository;

    public AdminService(AuthService authService, UserService userService,
                        GroupService groupService, ChatService chatService,
                        AdminRepository adminRepository) {
        this.authService = authService;
        this.userService = userService;
        this.groupService = groupService;
        this.chatService = chatService;
        this.adminRepository = adminRepository;
    }

    // admin methods
    public synchronized Admin login(String username, String password) {
        // getting admin and check for exist
        Admin admin = adminRepository.getAdminByUsername(username);
        if (admin == null) {
            throw new RuntimeException("Username is invalid.");
        }

        // checking amin password
        String passwordHash = PasswordHasher.hash(password, admin.getPasswordSalt());
        if (!passwordHash.equals(admin.getPasswordHash())) {
            throw new RuntimeException("Password is invalid.");
        }

        return admin;
    }

    // users methods
    public List<User> listUsers() {
        return userService.listUsers();
    }

    public String addUser(String username, String password, String confirmPassword, String email) {
        try {
            authService.singup(username, password, confirmPassword, email);
            return null;
        } catch (RuntimeException exception) {
            return exception.getMessage();
        }
    }

    public String deleteUser(String userId) {
        try {
            userService.deleteUser(userId);
            return null;
        } catch (RuntimeException exception) {
            return exception.getMessage();
        }
    }

    // groups methods
    public List<Group> listGroups() {
        return groupService.listGroups();
    }

    // public List<String> listGroupMembers(String groupId) {
    //     try {
    //         return groupService.getGroupMembers(groupId);
    //     } catch (RuntimeException exception) {
    //         return null;
    //     }
    // }

    public String addGroup(String ownerId, String name) {
        Group group = groupService.createGroup(ownerId, name);
        return group == null ? "Create group failed." : null;
    }

    // public String deleteGroup(String groupId) {
    //     try {
    //         groupService.deleteGroupById(groupId);
    //         return null;
    //     } catch (RuntimeException exception) {
    //         return exception.getMessage();
    //     }
    // }

    public String addUserToGroup(String groupId, String userId) {
        try {
            groupService.cliAddMember(groupId, userId);
            return null;
        } catch (RuntimeException exception) {
            return exception.getMessage();
        }
    }

    public String removeUserFromGroup(String groupId, String userId) {
        try {
            groupService.cliRemoveMember(groupId, userId);
            return null;
        } catch (RuntimeException exception) {
            return exception.getMessage();
        }
    }

    // reports methods
    public List<Report> viewReports() {
        return chatService.getAllReports();
    }

    public String resolveReport(String reportId) {
        try {
            chatService.resolveReport(reportId);
            return null;
        } catch (RuntimeException exception) {
            return exception.getMessage();
        }
    }
}