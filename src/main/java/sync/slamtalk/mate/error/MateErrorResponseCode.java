package sync.slamtalk.mate.error;

import sync.slamtalk.common.ResponseCodeDetails;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public enum MateErrorResponseCode implements ResponseCodeDetails {

    PARTICIPANT_ALREADY_APPLIED(SC_BAD_REQUEST, 4001, "이미 신청하였거나 신청했던 신청자입니다."),
    USER_NOT_AUTHORIZED(SC_BAD_REQUEST, 4002, "접근한 유저는 해당 권한이 없습니다."),
    PARTICIPANT_NOT_ALLOWED_TO_CHANGE_STATUS(SC_BAD_REQUEST, 4003, "해당 참여자는 상태를 변경할 수 없습니다."),
    DECREASE_POSITION_NOT_AVAILABLE(SC_BAD_REQUEST, 4004, "해당 포지션의 모집 인원을 감소시킬 수 없습니다."),
    INCREASE_POSITION_NOT_AVAILABLE(SC_BAD_REQUEST, 4005, "해당 포지션의 모집 인원을 증가시킬 수 없습니다."),
    MATE_POST_NOT_FOUND(SC_NOT_FOUND, 4041, "해당 모집 글을 찾을 수 없습니다."),
    PARTICIPANT_NOT_FOUND(SC_NOT_FOUND, 4042, "해당 참여자를 찾을 수 없습니다."),

    MATE_POST_ALREADY_CANCELED_OR_COMPLETED(SC_BAD_REQUEST, 4006, "이미 취소 되었거나 모집 완료된 글입니다."),
    NOT_ALLOWED_TO_PARTICIPATE(SC_BAD_REQUEST, 4008, "해당 글에 참여할 수 없습니다."),
    NO_ACCEPTED_PARTICIPANT(SC_BAD_REQUEST, 4009, "수락된 참여자가 없습니다."),
    MATE_POST_ALREADY_DELETED(SC_BAD_REQUEST, 4010, "이미 삭제된 글입니다.");

    MateErrorResponseCode(int code, int status, String message) {
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
