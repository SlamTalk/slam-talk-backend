package sync.slamtalk.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@Builder(access = AccessLevel.PROTECTED)
public class ChatMessageDTO implements Serializable {
    @NotNull
    private String roomId; // 채팅방 아이디(채팅방 식별자)
    private String senderId; // 메세지를 보낸 사용자의 고유 식별자
    @Nullable
    private String content; // 메세지 내용
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timestamp; // 메세지를 보낸 시간
    private Boolean read; // 읽음 여부를 나타내는 상태 필드


    // 생성될 때 현재 시간을 저장
    public ChatMessageDTO(){
        this.timestamp = LocalDateTime.now();
    }
}
