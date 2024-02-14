package sync.slamtalk.community.dto;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import sync.slamtalk.common.ResponseCodeDetails;

public enum CommunityErrorResponseCode implements ResponseCodeDetails {

    POST_FAIL(SC_BAD_REQUEST,4031,"Failed Post"),
    POST_NOT_FOUND(SC_NOT_FOUND,4032,"Post Not Found"),
    USER_NOT_FOUND(SC_NOT_FOUND, 4032, "User Not Found"),
    UNAUTHORIZED_USER(SC_UNAUTHORIZED, 4033, "User Not Unauthorized");
    private final int code;
    private int status;
    private final String message;

    CommunityErrorResponseCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
