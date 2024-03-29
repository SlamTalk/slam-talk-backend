package sync.slamtalk.chat.dto.response;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.chat.entity.RoomAction;

@Getter
@AllArgsConstructor
public class ChatUserDTO {
    /* 채팅방에 신규 입장 또는 나가는 유저 */
    @NotNull
    private Long userId;
    private RoomAction roomAction;
}
