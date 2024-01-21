package sync.slamtalk.chat.dto;

import sync.slamtalk.common.ResponseCodeDetails;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public enum ChatErrorResponseCode implements ResponseCodeDetails {

    // 채팅
    CHAT_FAIL(SC_BAD_REQUEST,4021,"Failed Chatting"),
    CHAT_LIST_NOT_FOUND(SC_NOT_FOUND,4022,"ChatList Not Found"),

    ;


    private final int code;
    private int status;
    private final String message;


    ChatErrorResponseCode(int code, int status, String message){
        this.code = code;
        this.status= status;
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
