package echo.security.validation.password;

import echo.security.validation.PasswordValidationRule;

public class HasSpecialCharRule implements PasswordValidationRule {
    // constructor
    public HasSpecialCharRule() {
    }

    // validation
    @Override
    public String validate(String password, String username) {
        for (int i = 0; i < password.length(); i++) {
            if (password.matches(".*[*&^%$#@!].*")) {
                return null;
            }
        }
        return "Password must have at least one spacial letter (*&^%$#@!).";
    }
}
