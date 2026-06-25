package echo.security.validation.password;

import echo.security.validation.PasswordValidationRule;

public class HasUppercaseRule implements PasswordValidationRule {
    // constructor
    public HasUppercaseRule() {
    }

    // validation
    @Override
    public String validate(String password, String username) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i))) {
                return null;
            }
        }
        return "Password must have at least one uppercase letter.";
    }
}
