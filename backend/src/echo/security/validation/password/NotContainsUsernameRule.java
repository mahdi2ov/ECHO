package echo.security.validation.password;

import echo.security.validation.PasswordValidationRule;

public class NotContainsUsernameRule implements PasswordValidationRule {
    // constructor
    public NotContainsUsernameRule() {
    }

    // validation
    @Override
    public String validate(String password, String username) {
        if (password.contains(username)) {
            return "Password must not contain your username.";
        }
        return null;
    }
}
