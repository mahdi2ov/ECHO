package echo.cli;

import java.util.Scanner;

import echo.model.Admin;
import echo.repository.AdminRepository;
import echo.repository.ConversationRepository;
import echo.repository.GroupRepository;
import echo.repository.MessageRepository;
import echo.repository.ReportRepository;
import echo.repository.UserRepository;
import echo.repository.inmemory.*;
import echo.security.AccountLockManager;
import echo.security.validation.message.MessageValidator;
import echo.security.validation.password.PasswordValidator;
import echo.service.*;

public class AdminCLI {

    public static void main(String[] args) {
        // making repositories(in memory databases)
        UserRepository userRepository = new InMemoryUserRepository();
        GroupRepository groupRepository = new InMemoryGroupRepository();
        ConversationRepository conversationRepository = new InMemoryConversationRepository();
        MessageRepository messageRepository = new InMemoryMessageRepository();
        ReportRepository reportRepository = new InMemoryReportRepository();
        AdminRepository adminRepository = new InMemoryAdminRepository();

        // making validator classes
        PasswordValidator passwordValidator = new PasswordValidator();
        AccountLockManager accountLockManager = new AccountLockManager();
        MessageValidator messageValidator = new MessageValidator();

        // making services
        AuthService authService = new AuthService(userRepository, passwordValidator, accountLockManager);
        UserService userService = new UserService(userRepository);
        ConversationService conversationService = new ConversationService(conversationRepository);
        GroupService groupService = new GroupService(groupRepository, conversationService, conversationRepository);
        ChatService chatService = new ChatService(messageRepository, messageValidator, conversationService, conversationRepository, reportRepository);
        AdminService adminService = new AdminService(authService, userService, groupService, chatService, adminRepository);

        Scanner scanner = new Scanner(System.in);
        System.out.println("-------------<<< Welcome to the admin cli! >>>-------------");

        System.out.print("---Please enter admin username: ");
        String username = scanner.next();
        System.out.print("---Please enter admin password: ");
        String password = scanner.next();

        try {
            Admin admin = adminService.login(username, password);
            System.out.println("---Logged in as " + admin.getUsername());
        } catch (RuntimeException e) {
            System.out.println("---Login failed: " + e.getMessage());
            return;
        }

        CommandRegistry commandRegistry = new CommandRegistry(adminService);

        System.out.println("---Type a command (type 'help' to show commands):");
        boolean running = true;
        while (running) {
            System.out.print(">>> ");
            String commandName = scanner.next();
            running = commandRegistry.execute(commandName, scanner);
        }

        System.out.println("Closing admin cli...");
        scanner.close();
    }
}