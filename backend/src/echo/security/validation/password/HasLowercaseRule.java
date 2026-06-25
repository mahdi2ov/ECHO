package echo.security.validation.password;

import echo.security.validation.PasswordValidationRule;

public class HasLowercaseRule implements PasswordValidationRule {
    // constructor
    public HasLowercaseRule() {
    }
    
    // validation
    @Override
    public String validate(String password, String username) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isLowerCase(password.charAt(i))) {
                return null;
            }
        }
        return "Password must have at least one lowercase letter.";
    }
}
