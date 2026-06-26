package echo.cli.command;

import java.util.List;
import java.util.Scanner;
import echo.model.Group;
import echo.service.AdminService;

public class ListGroupsCommand implements Command {
    private final AdminService adminService;

    public ListGroupsCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "list-groups";
    }

    @Override
    public void execute(Scanner scanner) {
        List<Group> groups = adminService.listGroups();
        if (groups.isEmpty()) {
            System.out.println("No groups found.");
            return;
        }
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            System.out.println((i + 1) + ". " + group.toString());
        }
    }
}