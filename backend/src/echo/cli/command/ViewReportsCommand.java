package echo.cli.command;

import java.util.List;
import java.util.Scanner;
import echo.model.Report;
import echo.service.AdminService;

public class ViewReportsCommand implements Command {
    private final AdminService adminService;

    public ViewReportsCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "view-reports";
    }

    @Override
    public void execute(Scanner scanner) {
        List<Report> reports = adminService.viewReports();
        if (reports.isEmpty()) {
            System.out.println("No reports found.");
            return;
        }
        for (int i = 0; i < reports.size(); i++) {
            Report report = reports.get(i);
            System.out.println((i + 1) + ". " + report.toString());
        }
    }
}