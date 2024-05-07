package sync.slamtalk.map.dto;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import sync.slamtalk.common.ResponseCodeDetails;

public enum BasketballCourtErrorResponse implements ResponseCodeDetails {
    MAP_FAIL(SC_NOT_FOUND,4041,"Court Not Found"),
    USER_NOT_FOUND(SC_NOT_FOUND,4042,"User Not Found"),
    UNAUTHORIZED_USER(SC_UNAUTHORIZED, 4043, "User Not Unauthorized"),
    CHATROOM_FAIL(SC_NOT_FOUND,4044,"Chatroom Not Found")

    ;

    private final int code;
    private final int status;
    private final String message;

    BasketballCourtErrorResponse(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
