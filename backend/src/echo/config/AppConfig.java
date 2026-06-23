package echo.config;

// global app configuration
public class AppConfig {

    // network
    private static final int HTTP_PORT = 8080;
    private static final int WEBSOCKET_PORT = 8081;
    
    // database
    private static final String DATA_PATH = "backend/data";
    
    // login
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_ACCOUNT_MINUTES = 30;
    private static final int MIN_LENGTH_PASSWORD = 8;
    
    // message
    private static final int MAX_MESSAGE_CHARACTER_LENGTH = 400;
    private static final int MAX_MESSAGE_PER_SECOND = 5;

    // cli admin
    private static final String DEFAULT_USERNAME = "username";
    private static final String DEFAULT_PASSWORD = "password";

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
