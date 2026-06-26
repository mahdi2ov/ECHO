package echo.cli.command;

import java.util.Scanner;
import echo.service.AdminService;

public class AddUserToGroupCommand implements Command {
    private final AdminService adminService;

    public AddUserToGroupCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "add-user-to-group";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Please enter groupId: ");
        String groupId = scanner.next();
        System.out.print("Please enter userId: ");
        String userId = scanner.next();
        System.out.println();

        String error = adminService.addUserToGroup(groupId, userId);
        if (error != null) {
            System.out.println("Error:\n" + error);
        } else {
            System.out.println("User added to group successfully.");
        }
    }
}