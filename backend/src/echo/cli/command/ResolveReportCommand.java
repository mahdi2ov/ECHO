package echo.cli.command;

import java.util.Scanner;
import echo.service.AdminService;

public class ResolveReportCommand implements Command {
    private final AdminService adminService;

    public ResolveReportCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "resolve-report";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Please enter reportId: ");
        String reportId = scanner.next();
        System.out.println();

        String error = adminService.resolveReport(reportId);
        if (error != null) {
            System.out.println("Error:\n" + error);
        } else {
            System.out.println("Report resolved successfully.");
        }
    }
}