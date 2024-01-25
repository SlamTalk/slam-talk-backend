package sync.slamtalk.mate.error;

import sync.slamtalk.common.ResponseCodeDetails;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public enum UserErrorResponseCode implements ResponseCodeDetails {
    MATE_POST_NOT_FOUND(SC_NOT_FOUND, 4041, "해당 모집 글을 찾을 수 없습니다."),
    PARTICIPANT_NOT_FOUND(SC_NOT_FOUND, 4042, "해당 참여자를 찾을 수 없습니다."),
    PARTICIPANT_ALREADY_REJECTED(SC_BAD_REQUEST, 4001, "이미 거절된 참여자입니다."),

    PARTICIPANT_ALREADY_ACCEPTED(SC_BAD_REQUEST, 4002, "이미 수락된 참여자입니다."),
    PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS(SC_BAD_REQUEST, 4003, "해당 참여자는 상태를 변경할 수 없습니다."),

    USER_NOT_AUTHORIZED(SC_BAD_REQUEST, 4002, "접근한 유저는 해당 권한이 없습니다."),
    APPLY_STATUS_NOT_FOUND(SC_NOT_FOUND, 4043, "해당 신청 상태를 찾을 수 없습니다.");

    UserErrorResponseCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    private final int code;
    private final int status;
    private String message;

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
