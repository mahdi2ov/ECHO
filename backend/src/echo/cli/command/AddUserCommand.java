package echo.cli.command;

import java.util.Scanner;
import echo.service.AdminService;

public class AddUserCommand implements Command {
    private final AdminService adminService;

    public AddUserCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "add-user";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Please enter username: ");
        String username = scanner.next();
        System.out.print("Please enter password: ");
        String password = scanner.next();
        System.out.print("Please confirm password: ");
        String confirmPassword = scanner.next();
        System.out.println();

        String error = adminService.addUser(username, password, confirmPassword);
        if (error != null) {
            System.out.println("Error:\n" + error);
        } else {
            System.out.println("User added successfully.");
        }
    }
}