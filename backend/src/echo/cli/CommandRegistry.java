package echo.cli;

import java.util.Scanner;
import echo.cli.command.*;
import echo.service.AdminService;

public class CommandRegistry {
    private final AdminService adminService;

    public CommandRegistry(AdminService adminService) {
        this.adminService = adminService;
    }

    public boolean execute(String commandName, Scanner scanner) {
        Command command;
        switch (commandName) {
            // matching command
            case "list-users": {
                command = new ListUsersCommand(this.adminService);
                break;
            }
            case "add-user": {
                command = new AddUserCommand(this.adminService);
                break;
            }
            case "delete-user": {
                command = new DeleteUserCommand(this.adminService);
                break;
            }
            case "list-groups": {
                command = new ListGroupsCommand(this.adminService);
                break;
            }
            case "list-group-members": {
                command = new ListGroupMembersCommand(this.adminService);
                break;
            }
            case "add-group": {
                command = new AddGroupCommand(this.adminService);
                break;
            }
            case "delete-group": {
                command = new DeleteGroupCommand(this.adminService);
                break;
            }
            case "add-user-to-group": {
                command = new AddUserToGroupCommand(this.adminService);
                break;
            }
            case "remove-user-from-group": {
                command = new RemoveUserFromGroupCommand(this.adminService);
                break;
            }
            case "view-reports": {
                command = new ViewReportsCommand(this.adminService);
                break;
            }
            case "resolve-report": {
                command = new ResolveReportCommand(this.adminService);
                break;
            }
            // to print all commands
            case "help": {
                command = new HelpCommand();
                break;
            }
            // retrun false to close admin cli
            case "exit": {
                return false;
            }
            default: {
                System.out.println("Unknown command: " + commandName);
                return true;
            }
        }

        command.execute(scanner);
        return true;
    }
}