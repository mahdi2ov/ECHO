package echo.dto.request;

public class ForgotPasswordRequest {
    private final String username;
    private final String email;

    // constructor
    public ForgotPasswordRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // getters
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}
