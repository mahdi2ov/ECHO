package echo.security;

import echo.config.AppConfig;

public class EncryptionUtil {
    // encryption key
    private static final String key = AppConfig.getEncriptionKey();

    // method for encrypt texts
    public static String encrypt(String text) {
        StringBuilder encryptedText = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            encryptedText.append((int)(text.charAt(i) ^ key.charAt(i % key.length())));
            if (i != text.length() - 1) {
                encryptedText.append(",");
            }
        }
        return encryptedText.toString();
    }
    public static String decrypt(String encryptedText) {
        String[] textArray = encryptedText.split(",");
        StringBuilder originalText = new StringBuilder();
        for (int i = 0; i < textArray.length; i++) {
            int encryptedTextChar = Integer.parseInt(textArray[i]);
            originalText.append((char)(encryptedTextChar ^ key.charAt(i % key.length())));
        }
        return originalText.toString();
    }
}
