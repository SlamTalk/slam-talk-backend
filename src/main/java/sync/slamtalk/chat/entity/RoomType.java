package sync.slamtalk.chat.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    /* 다이렉트 채팅, 농구장 채팅, 같이해요 채팅, 팀 매칭 채팅 */

    DIRECT("DM"), // 다이렉트 메세지
    TOGETHER("TM"), // 같이 해요 메세지
    BASKETBALL("BM"), // 농구장 메세지
    MATCHING("MM"); // 매칭 메세지

    private final String key;
}
