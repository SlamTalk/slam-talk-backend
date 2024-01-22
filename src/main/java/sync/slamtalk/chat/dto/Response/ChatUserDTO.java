package sync.slamtalk.chat.dto.Response;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.chat.entity.RoomAction;

@Getter
@AllArgsConstructor
public class ChatUserDTO {
    /* 채팅방에 신규 입장 또는 나가는 유저 */
    @Nullable
    private Long userId;
    private RoomAction roomAction;
}
