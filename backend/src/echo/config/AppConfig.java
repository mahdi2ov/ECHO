package echo.config;

// global app configuration
public class AppConfig {

    // network
    private static final int HTTP_PORT = 8080;
    private static final int WEBSOCKET_PORT = 8081;
    
    // database
    private static final String DATA_PATH = "backend/data";

    private static final String USERS_PATH= "backend/data/users.txt";
    private static final String CONVERSATIONS_PATH = "backend/data/conversations.txt";
    private static final String GROUPS_PATH = "backend/data/groups.txt";
    private static final String MESSAGES_PATH = "backend/data/messages.txt";
    private static final String REPORTS_PATH = "backend/data/reports.txt";
    private static final String CONTACTS_AND_BLOCKED_USERS_PATH = "backend/data/contacts-blockedusers.txt";
    private static final String ADMIN_CLI_PATH = "backend/data/admin-cli.txt";
    
    // login
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_ACCOUNT_MINUTES = 30;
    private static final int MIN_LENGTH_PASSWORD = 8;
    
    // message
    private static final int MAX_MESSAGE_CHARACTER_LENGTH = 400;
    private static final int MAX_MESSAGE_PER_SECOND = 5;

    // cli admin
    private static final String DEFAULT_USERNAME = "MMOMRM";
    private static final String DEFAULT_PASSWORD = "ECHOproject";
    
    // security
    private static final String ENCRIPTION_KEY = "EchoKey";
    private static final int RANDOM_STRING_LENGTH = 8;
    
    // private constructor for prevent object creation
    private AppConfig() {
    }
    
    // getters
    public static int getHttpPort() {
        return HTTP_PORT;
    }
    public static int getWebsocketPort() {
        return WEBSOCKET_PORT;
    }
    public static String getDataPath() {
        return DATA_PATH;
    }
    public static String getUsersPath() {
        return USERS_PATH;
    }
    public static String getConversationsPath() {
        return CONVERSATIONS_PATH;
    }
    public static String getGroupsPath() {
        return GROUPS_PATH;
    }
    public static String getMessagesPath() {
        return MESSAGES_PATH;
    }
    public static String getReportsPath() {
        return REPORTS_PATH;
    }
    public static String getContactsAndBlockedUsersPath() {
        return CONTACTS_AND_BLOCKED_USERS_PATH;
    }
    public static String getAdminCLIPath() {
        return ADMIN_CLI_PATH;
    }
    public static int getMaxLoginAttempts() {
        return MAX_LOGIN_ATTEMPTS;
    }
    public static int getLockAccountMinutes() {
        return LOCK_ACCOUNT_MINUTES;
    }
    public static int getMinLengthPassword() {
        return MIN_LENGTH_PASSWORD;
    }
    public static int getMaxMessageCharacterLength() {
        return MAX_MESSAGE_CHARACTER_LENGTH;
    }
    public static int getMaxMessagePerSecond() {
        return MAX_MESSAGE_PER_SECOND;
    }
    public static String getDefaultUsername() {
        return DEFAULT_USERNAME;
    }
    public static String getDefaultPassword() {
        return DEFAULT_PASSWORD;
    }
    public static String getEncriptionKey() {
        return ENCRIPTION_KEY;
    }
    public static int getRandomStringLength() {
        return RANDOM_STRING_LENGTH;
    }
}
