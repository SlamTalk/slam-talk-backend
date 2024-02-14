package sync.slamtalk.community.dto;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import sync.slamtalk.common.ResponseCodeDetails;

public enum CommunityErrorResponseCode implements ResponseCodeDetails {

    POST_FAIL(SC_BAD_REQUEST,4031,"Failed Post"),
    POST_NOT_FOUND(SC_NOT_FOUND,4032,"Post Not Found"),
    USER_NOT_FOUND(SC_NOT_FOUND, 4032, "User Not Found"),
    UNAUTHORIZED_USER(SC_UNAUTHORIZED, 4033, "User Not Unauthorized"),
    COMMENT_FAIL(SC_BAD_REQUEST, 4034, "Empty Comment"),
    COMMENT_NOT_FOUND(SC_NOT_FOUND, 4035, "Comment Not Found");
    private final int code;
    private final int status;
    private final String message;

    CommunityErrorResponseCode(int code, int status, String message) {
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
