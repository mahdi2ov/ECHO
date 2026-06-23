package echo.security.validation.password;

import java.util.ArrayList;
import java.util.List;

import echo.config.AppConfig;
import echo.security.validation.PasswordValidationRule;

public class PasswordValidator {
    // list of validations and error
    private final List<PasswordValidationRule> passwordValidationRules;
    private final List<String> errors;

    // constructor
    public PasswordValidator() {
        this.passwordValidationRules = List.of(new HasDigitRule(), new HasLowercaseRule(), new HasUppercaseRule(),
            new HasSpecialCharRule(), new MinLengthRule(AppConfig.getMinLengthPassword()), new NotContainsUsernameRule());
        this.errors = new ArrayList<>();
    }

    // validate all of condition for user and save invalid conditions
    public boolean validate(String password, String username) {
        for (PasswordValidationRule validationRule : this.passwordValidationRules) {
            String error = validationRule.validate(password, username);
            if (error != null) {
                this.errors.add(error);
            }
        }
        return this.errors.size() == 0;
    }

    // get validation errors
    public List<String> getErrors() {
        return this.errors;
    }
}
