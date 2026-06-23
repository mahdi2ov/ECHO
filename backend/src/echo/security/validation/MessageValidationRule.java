package echo.security.validation;

public interface MessageValidationRule {
    // for validate each type of conditions
    String validate(String message, String sendrId);
}
