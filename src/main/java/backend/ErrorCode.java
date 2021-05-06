package backend;

public class ErrorCode {
    public static final Integer NO_LOGIN_ERROR = -101;//need to login in
    public static final Integer CSRF_ERROR = -111;//csrf invalid
    public static final Integer PARAM_ERROR = -400;//parameter validation failed
    public static final Integer SUCCEED = 0;
}
