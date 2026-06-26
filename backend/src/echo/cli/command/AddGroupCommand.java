package echo.cli.command;

import java.util.Scanner;
import echo.service.AdminService;

public class AddGroupCommand implements Command {
    private final AdminService adminService;

    public AddGroupCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "add-group";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Please enter ownerId: ");
        String ownerId = scanner.next();
        System.out.print("Please enter group name: ");
        String name = scanner.next();
        System.out.println();

        String error = adminService.addGroup(ownerId, name);
        if (error != null) {
            System.out.println("Error:\n" + error);
        } else {
            System.out.println("Group created successfully.");
        }
    }
}