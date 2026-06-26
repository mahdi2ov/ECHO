package echo.cli.command;

import java.util.Scanner;

public class HelpCommand implements Command {
    public HelpCommand() {
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Commands:");
        System.out.println("  list-users              - show all registered users");
        System.out.println("  add-user                - register a new user manually");
        System.out.println("  delete-user              - delete a user by username");
        System.out.println("  list-groups              - show all groups");
        System.out.println("  list-group-members       - show members of a group");
        System.out.println("  add-group                - create a new group");
        System.out.println("  delete-group             - delete a group by name");
        System.out.println("  add-user-to-group        - add a user to a group");
        System.out.println("  remove-user-from-group   - remove a user from a group");
        System.out.println("  view-reports             - show all reported messages");
        System.out.println("  resolve-report           - mark a report as resolved");
        System.out.println("  help                     - show this list of commands");
        System.out.println("  exit                     - exit the admin CLI");
    }
}