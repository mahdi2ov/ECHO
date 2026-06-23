package echo.security;

import echo.config.AppConfig;

public class PasswordHasher {
    private static final long PRIME = 31;
    private static final long MODULO = 50000017;
    private static final long RANDOM_STRING_LENGTH = 8;

    // hashing password and random string
    public static String hash(String password, String random) {
        password += random;
        long hash = 7;
        for (int i = 0; i < password.length(); i++) {
            hash = ((hash * PRIME) + password.charAt(i)) % MODULO; 
        }
        return Long.toHexString(hash);
    } 

    // make a random String
    public static String randomString() {
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
            int random = (int)(Math.random() * 52); 
            randomString.append(random < 26 ? (char)random + 'a' : (char)random - 26 + 'A');
        }
        return randomString.toString();
    }
}
