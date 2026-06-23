package echo.security.validation.password;

import echo.security.validation.PasswordValidationRule;

public class MinLengthRule implements PasswordValidationRule {
    private final int minPasswordLenght;

    // constructor
    public MinLengthRule(int minPasswordLenght) {
        this.minPasswordLenght = minPasswordLenght;
    }

    // validation
    @Override
    public String validate(String password, String username) {
        if (password.length() < minPasswordLenght) {
            return "Password must be at least " + minPasswordLenght + " characters.";
        }
        return null;
    }
}
