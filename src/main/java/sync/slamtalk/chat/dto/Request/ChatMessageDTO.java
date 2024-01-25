package sync.slamtalk.chat.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageDTO implements Serializable {
    @Nullable
    private String roomId; // 채팅방 아이디(채팅방 식별자)
    private String senderId; // 메세지를 보낸 사용자의 고유 식별자
    private String senderNickname; // 메세지를 보낸 사용자의 닉네임 -> 채팅방에 표시될
    @Nullable
    private String content; // 메세지 내용
    private String messageType; // 메세지 타입 (뒤로가기 : back / 나가기: out) TODO : general 추가
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp; // 메세지를 보낸 시간


    // 생성될 때 현재 시간을 저장
    public ChatMessageDTO(){
        this.timestamp = LocalDateTime.now();
    }
}
