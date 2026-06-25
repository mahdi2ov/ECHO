package echo.security.validation.password;

import echo.security.validation.PasswordValidationRule;

public class HasDigitRule implements PasswordValidationRule {
    // constructor
    public HasDigitRule() {
    }

    // validation
    @Override
    public String validate(String password, String username) {
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                return null;
            }
        }
        return "Password must have at least one digit letter.";
    }
}
