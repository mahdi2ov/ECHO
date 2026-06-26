package echo.security.validation.message;

import java.util.ArrayList;
import java.util.List;

import echo.config.AppConfig;
import echo.security.validation.MessageValidationRule;

public class MessageValidator {
    // list of validations and error
    private final List<MessageValidationRule> messageValidationRules;
    private final List<String> errors;
    
    // constructor
    public MessageValidator() {
        this.messageValidationRules = List.of(new MaxLengthRule(AppConfig.getMaxMessageCharacterLength()),
                                            new SpamRateRule());
        this.errors = new ArrayList<>();
    }

    // validate two condition for message and save invalid conditions
    public List<String> validate(String message, String senderId) {
        for (MessageValidationRule messageValidationRule : this.messageValidationRules) {
            String error = messageValidationRule.validate(message, senderId);
            if (error != null) {
                this.errors.add(error);
            }
        }
        return this.errors;
    }
}
