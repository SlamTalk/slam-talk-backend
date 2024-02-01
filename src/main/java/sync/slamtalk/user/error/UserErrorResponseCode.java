package sync.slamtalk.user.error;

import sync.slamtalk.common.ResponseCodeDetails;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public enum UserErrorResponseCode implements ResponseCodeDetails {
    INVALID_TOKEN(SC_BAD_REQUEST,4001,"Token Invalid"),
    LOGIN_FAIL(SC_UNAUTHORIZED,4002,"Failed Login"),
    EMAIL_ALREADY_EXISTS(SC_BAD_REQUEST, 4003, "이미 존재하는 유저 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(SC_BAD_REQUEST, 4004, "이미 존재하는 닉네임입니다."),
    BAD_CREDENTIALS(SC_BAD_REQUEST, 4005, "아이디 또는 비밀번호를 다시 확인해주세요."),
    NOT_FOUND_USER(SC_BAD_REQUEST, 4006, "해당 유저가 존재하지 않습니다"),
    ENUM_TYPE_NOT_FOUND(SC_BAD_REQUEST,4007, "요청 입력 타입이 잘못 되었습니다, 정해진 타입으로 요청보내셨는지 다시한번 확인해주세요!")
    ;

    UserErrorResponseCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    private final int code;
    private final int status;
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int getCode(){
        return code;
    }

    @Override
    public int getStatus(){
        return status;
    }

    @Override
    public String getMessage(){
        return message;
    }
}
