package echo.cli.command;

import java.util.List;
import java.util.Scanner;
import echo.model.User;
import echo.service.AdminService;

public class ListUsersCommand implements Command {
    private final AdminService adminService;

    public ListUsersCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "list-users";
    }

    @Override
    public void execute(Scanner scanner) {
        List<User> users = adminService.listUsers();
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". " + user.toString());
        }
    }
}