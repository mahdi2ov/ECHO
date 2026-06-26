package echo.cli.command;

import java.util.Scanner;
import echo.service.AdminService;

public class DeleteUserCommand implements Command {
    private final AdminService adminService;

    public DeleteUserCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "delete-user";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Please enter user id: ");
        String username = scanner.next();
        System.out.println();

        String error = adminService.deleteUser(username);
        if (error != null) {
            System.out.println("Error:\n" + error);
        } else {
            System.out.println("User deleted successfully.");
        }
    }
}