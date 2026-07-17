package echo.util;

public class ResponseFactory {
    // some methods for creatin API response objects
    public static String success(String successMessage, String dataJson) {
        return JsonUtil.toApiResponseJson(true, successMessage, dataJson);
    }
    
    public static String error(String errorMessage) {
        return JsonUtil.toApiResponseJson(false, errorMessage, null);
    }
}
