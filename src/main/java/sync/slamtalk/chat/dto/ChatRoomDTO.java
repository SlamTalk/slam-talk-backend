package sync.slamtalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
public class ChatRoomDTO implements Serializable {
    private String roomId;
    private String name;

    public static ChatRoomDTO create(String name){
        ChatRoomDTO room = new ChatRoomDTO();
        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;
    }
}
