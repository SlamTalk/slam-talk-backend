package sync.slamtalk.chat.config;

import sync.slamtalk.common.ResponseCode;

public enum RoomCode implements ResponseCode {
    NOT_FOUND_ROOM(1001,"해당하는 방이 존재하지 않습니다."),// 방을 찾을 수 없을 때, (채팅방 생성이 안되어있을떄?)
    FULL_ROOM(1002,"인원이 가득 찼습니다"),// 채팅방 인원이 가득찼을 때
    NOT_PARTICIPATE_ROOM(1003,"참가하지 않은 방입니다"), // 구독 하지 않은 방을 참가하려할때
    PARTICIPATING_ROOM(1004,"이미 참여하고 있는 방입니다."), // 채팅방 신규/기존 구분
    NO_PERMISSION(1005,"해당 기능을 수행할 권한이 없습니다."); // 권한 밖 요청일 때

    private final int code;
    private final String message;

    RoomCode(int code,String message){
        this.code = code;
        this.message = message;
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
