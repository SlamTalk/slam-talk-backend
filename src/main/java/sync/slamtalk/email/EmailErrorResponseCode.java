package sync.slamtalk.email;

import sync.slamtalk.common.ResponseCodeDetails;

import static jakarta.servlet.http.HttpServletResponse.*;

public enum EmailErrorResponseCode implements ResponseCodeDetails {
    DATABASE_ERROR(SC_INTERNAL_SERVER_ERROR, 5001, "데이터베이스 서버 에러"),
    AUTHENTICATION_CODE_DOES_NOT_EXIST(SC_BAD_REQUEST, 5002, "인증번호를 재발급 받으세요"),
    POST_USERS_INVALID_CODE(SC_BAD_REQUEST, 5003, "잘못된 인증번호 입니다."),
    POST_USERS_INVALID_EMAIL(SC_BAD_REQUEST, 5004, "올바르지 않은 이메일 입니다."),
    MAIL_FAIL(SC_INTERNAL_SERVER_ERROR, 5005, "메일 전송 실패");
    EmailErrorResponseCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    private final int code;
    private final int status;
    private String message;

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