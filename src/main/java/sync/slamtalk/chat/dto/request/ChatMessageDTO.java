package sync.slamtalk.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageDTO implements Serializable {
    private String messageId; // 메세지 아이디

    private String roomId; // 채팅방 아이디(채팅방 식별자)

    private Long senderId; // 메세지를 보낸 사용자의 아이디

    private String senderNickname; // 메세지를 보낸 사용자의 닉네임 -> 채팅방에 표시될

    private String imgUrl; // 메세지를 보낸 사용자의 프로필

    private String content; // 메세지 내용
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String timestamp; // 메세지를 보낸 시간


}
