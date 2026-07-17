package echo.dto.request;

public class LoginRequest {
    private final String username;
    private final String password;

    // construcotr
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // getters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
