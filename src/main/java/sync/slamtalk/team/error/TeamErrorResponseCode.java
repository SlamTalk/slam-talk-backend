package sync.slamtalk.team.error;

import sync.slamtalk.common.ResponseCodeDetails;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public enum TeamErrorResponseCode implements ResponseCodeDetails {

    TEAM_POST_NOT_FOUND(SC_NOT_FOUND, 4041, "해당 모집 글을 찾을 수 없습니다."),
    TEAM_POST_ALREADY_DELETED(SC_BAD_REQUEST, 4006, "이미 삭제된 모집 글입니다."),
    PROHIBITED_TO_APPLY_TO_YOUR_POST(SC_BAD_REQUEST, 4007, "자신이 작성한 글에는 지원할 수 없습니다."),
    TEAM_POST_IS_NOT_RECRUITING(SC_BAD_REQUEST, 4008, "모집이 마감된 글입니다."),
    ALREADY_APPLIED_TO_THIS_POST(SC_BAD_REQUEST, 4009, "이미 지원한 글입니다."),
    ALEADY_DECLARED_OPPONENT(SC_BAD_REQUEST, 4010, "이미 상대팀을 선언한 글입니다."),
    OVER_LIMITED_NUMBERS(SC_BAD_REQUEST, 4011, "모집 인원을 초과하여 지원할 수 없습니다."),
    APPLICANT_NOT_FOUND(SC_NOT_FOUND, 4042, "해당 지원자를 찾을 수 없습니다."),
    OPPONENT_NOT_DECLARED(SC_BAD_REQUEST, 4012, "상대팀이 선언되지 않은 글입니다.");

    TeamErrorResponseCode(int code, int status, String message) {
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
