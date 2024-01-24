package sync.slamtalk.chat.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatCreateDTO implements Serializable {
    private String roomType; // 1:1 이냐 단체방 이냐
    private String name; // 채팅방 이름
}
