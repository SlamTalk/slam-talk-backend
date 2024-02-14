package sync.slamtalk.map.dto;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import sync.slamtalk.common.ResponseCodeDetails;

public enum BasketballCourtErrorResponse implements ResponseCodeDetails {
    MAP_FAIL(SC_BAD_REQUEST,4041,"Court Not Found")
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
