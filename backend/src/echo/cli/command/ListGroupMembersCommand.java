package echo.cli.command;

import java.util.List;
import java.util.Scanner;
import echo.service.AdminService;

public class ListGroupMembersCommand implements Command {
    private final AdminService adminService;

    public ListGroupMembersCommand(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public String getName() {
        return "list-group-members";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Please enter groupId: ");
        String groupId = scanner.next();
        System.out.println();

        List<String> members = adminService.listGroupMembers(groupId);
        if (members == null || members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }
        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i));
        }
    }
}