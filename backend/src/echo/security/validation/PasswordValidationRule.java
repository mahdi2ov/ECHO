package echo.security.validation;

public interface PasswordValidationRule {
    // for validate each type of conditions
    String validate(String password, String username);
}
