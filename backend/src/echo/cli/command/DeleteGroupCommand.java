package echo.cli.command;

import java.util.Scanner;
import echo.service.AdminService;

public class DeleteGroupCommand implements Command {
    private final AdminService adminService;

    public DeleteGroupCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "delete-group";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Please enter group id: ");
        String name = scanner.next();
        System.out.println();

        String error = adminService.deleteGroup(name);
        if (error != null) {
            System.out.println("Error:\n" + error);
        } else {
            System.out.println("Group deleted successfully.");
        }
    }
}