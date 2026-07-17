package echo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import echo.config.AppConfig;
import echo.controller.AuthController;
import echo.controller.ChatController;
import echo.controller.GroupController;
import echo.controller.SavedMessagesController;
import echo.controller.UserController;
import echo.network.http.ECHOHttp;
import echo.network.websocket.ECHOWebSocket;
import echo.repository.AdminRepository;
import echo.repository.ConversationRepository;
import echo.repository.GroupRepository;
import echo.repository.MessageRepository;
import echo.repository.ReportRepository;
import echo.repository.UserRepository;
import echo.repository.file.FileAdminRepository;
import echo.repository.file.FileConversationRepository;
import echo.repository.file.FileGroupRepository;
import echo.repository.file.FileMessageRepository;
import echo.repository.file.FileReportRepository;
import echo.repository.file.FileUserRepository;
import echo.security.AccountLockManager;
import echo.security.validation.message.MessageValidator;
import echo.security.validation.password.PasswordValidator;
import echo.service.AdminService;
import echo.service.AuthService;
import echo.service.ChatService;
import echo.service.ContactService;
import echo.service.ConversationService;
import echo.service.GroupService;
import echo.service.SavedMessageService;
import echo.service.UserService;

public class App {
    private App() {
    }

    public static void main(String[] args) {
        try {
            startApplication();
        } catch (Exception exception) {
            System.err.println("Application failed to start.");
        }
    }

    private static void startApplication() throws IOException {
        // repository
        UserRepository userRepository = new FileUserRepository();
        GroupRepository groupRepository = new FileGroupRepository();
        ConversationRepository conversationRepository = new FileConversationRepository();
        MessageRepository messageRepository = new FileMessageRepository();
        ReportRepository reportRepository = new FileReportRepository();
        AdminRepository adminRepository = new FileAdminRepository();

        // validator and manager
        PasswordValidator passwordValidator = new PasswordValidator();
        AccountLockManager accountLockManager = new AccountLockManager();
        MessageValidator messageValidator = new MessageValidator();

        // service
        ConversationService conversationService = new ConversationService(conversationRepository);
        AuthService authService = new AuthService(userRepository, passwordValidator, accountLockManager);
        UserService userService = new UserService(userRepository);
        ContactService contactService = new ContactService(userRepository);
        GroupService groupService = new GroupService(groupRepository, conversationService, conversationRepository, userRepository);
        ChatService chatService = new ChatService(messageRepository, messageValidator, conversationService, conversationRepository,
                                                    reportRepository, groupRepository, userRepository);
        SavedMessageService savedMessageService = new SavedMessageService( conversationRepository, messageRepository, userRepository, chatService);
        AdminService adminService = new AdminService(authService, userService, groupService, chatService, adminRepository);

        // controller
        AuthController authController = new AuthController(authService);
        UserController userController = new UserController(userService, contactService);
        GroupController groupController = new GroupController(groupService);
        ChatController chatController = new ChatController(chatService, conversationService, groupRepository, userService);
        SavedMessagesController savedMessagesController = new SavedMessagesController(savedMessageService);

        // websocket server
        ECHOWebSocket webSocketServer = new ECHOWebSocket(AppConfig.getWebsocketPort());
        webSocketServer.start();

        // http server
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(AppConfig.getHttpPort()), 0);
        httpServer.createContext("/", new ECHOHttp(authController, userController, groupController, chatController,
                                                                savedMessagesController, messageRepository, webSocketServer));
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.start();

        System.out.println("HTTP server is running on port " + AppConfig.getHttpPort());
        System.out.println("WebSocket server is running on port " + AppConfig.getWebsocketPort());
        System.out.println("Admin service is ready: " + (adminService != null));
    }
}
