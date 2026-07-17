package echo.dto.response;

// generic class for any DTO
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;

    // constructor
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // getters
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
    public T getData() {
        return data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
