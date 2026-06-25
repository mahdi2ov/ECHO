package echo.security.validation.message;

import echo.security.validation.MessageValidationRule;

public class MaxLengthRule implements MessageValidationRule {
    private final int maxMessageLength;
    // constructor
    public MaxLengthRule(int maxMessageLength) {
        this.maxMessageLength = maxMessageLength;
    }

    // validation
    @Override
    public String validate(String message , String senderId) {
        if (message.length() > maxMessageLength) {
            return "Message must not exceed " + maxMessageLength + " characters.";
        }
        return null;
    }
}
