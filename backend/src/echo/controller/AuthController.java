package echo.controller;

import echo.dto.request.ForgotPasswordRequest;
import echo.dto.request.LoginRequest;
import echo.dto.request.SignupRequest;
import echo.dto.response.UserDTO;
import echo.model.User;
import echo.service.AuthService;
import echo.util.JsonUtil;
import echo.util.ResponseFactory;

public class AuthController {
    private final AuthService authService;

    // constructor
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public String signup(SignupRequest request) {
        try {
            User user = authService.singup(request.getUsername(), request.getConfirmPassword(), request.getPassword(), request.getEmail());
            UserDTO userDTO = UserDTO.toUserDTO(user);
            return ResponseFactory.success("signup was successful.", JsonUtil.toJson(userDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String login(LoginRequest request) {
        try {
            User user = authService.login(request.getUsername(), request.getPassword());
            UserDTO userDTO = UserDTO.toUserDTO(user);
            return ResponseFactory.success("Login was successful.", JsonUtil.toJson(userDTO));
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String forgotPassword(ForgotPasswordRequest request) {
        try {
            String newPassword = authService.passwordRecovery(request.getUsername(), request.getEmail());
            String dataJson = String.format("{\"newPassword\":\"%s\"}", newPassword);
            return ResponseFactory.success("Password reset.", dataJson);
        } catch (RuntimeException exception) {
            return ResponseFactory.error(exception.getMessage());
        }
    }

    public String logout() {
        authService.logout();
        return ResponseFactory.success("Logged out.", null);
    }
}
