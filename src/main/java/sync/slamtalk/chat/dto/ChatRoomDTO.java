package sync.slamtalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ChatRoomDTO implements Serializable {
    // 채팅방 고유 아이디
    private String roomId;
    // 채팅방 타입
    private String chatRoomType;
    // 채팅방 이름
    private String name;
}
