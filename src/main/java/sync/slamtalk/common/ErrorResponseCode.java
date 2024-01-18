package sync.slamtalk.common;

import static jakarta.servlet.http.HttpServletResponse.*;

public enum ErrorResponseCode {

    OK(SC_OK, 200,"Request Success"),
    INVALID_TOKEN(SC_BAD_REQUEST,4001,"Token Invalid"),

    // 로그인
    LOGIN_FAIL(SC_UNAUTHORIZED,4011,"Failed Login"),

    // 채팅
    CHAT_FAIL(SC_BAD_REQUEST,4021,"Failed chatting"),

    // 게시판
    BOARD_FAIL(SC_BAD_REQUEST,4031,"Failed Board"),

    // 지도
    MAP_FAIL(SC_BAD_REQUEST,4041,"Failed Map"),

    // 같이 하기
    TOGETHER_FAIL(SC_BAD_REQUEST,4051,"Failed Together"),

    // 팀 매칭
    TEAM_MATCHING_FAIL(SC_BAD_REQUEST,4061,"Failed matching"),


    // 서버에러
    UNCATEGORIZED(SC_INTERNAL_SERVER_ERROR,5000,"Uncategorized");

    private final int code;
    private int status;
    private final String message;


    ErrorResponseCode(int code, int status, String message){
        this.code = code;
        this.status= status;
        this.message = message;
    }

    public int getCode(){
        return code;
    }

    public int getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }



}
