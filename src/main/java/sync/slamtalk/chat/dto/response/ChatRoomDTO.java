package sync.slamtalk.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO implements Serializable {
    // 채팅방 고유 아이디
    private String roomId;
    // 채팅방 타입
    private String roomType;
    // 채팅방 상대방 아이디
    private String partnerId;
    // 채팅방 이름
    private String name;
    // 채팅방 이미지
    private String imgUrl;
    // 채팅방 마지막 메세지
    private String lastMessage;
    // 농구장 아이디
    private Long courtId;
    // 채팅방 마지막 메세지 날짜
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String lastMessageTime;
    // 읽지 않은 메세지 표시
    private boolean newMsg = false;


    // 채팅방 마지막 메세지 업데이트
    public void setLast_message(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void updateNoReadCnt(Boolean newMsg) {
        this.newMsg = newMsg;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void updatecourtId(Long courtId) {
        this.courtId = courtId;
    }

    public void updatePartnerId(String pid) {
        this.partnerId = pid;
    }

    public void setLastMessageTime(String lastMessageTime){
        this.lastMessageTime = lastMessageTime;
    }
}
