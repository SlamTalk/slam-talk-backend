package sync.slamtalk.notification.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    /* 다이렉트 채팅, 농구장 채팅, 같이해요 채팅, 팀 매칭 채팅 */

    LEVEL("LEVEL"), // 레벨
    BASKETBALL("BASKETBALL"), // 농구장
    MATE("MATE"), // 메이트
    TEAM("TEAM"), // 팀
    COMMUNITY("COMMUNITY"), // 커뮤니티
    CHAT("chat"); // 채팅

    private final String key;

    @Override
    public String toString() {
        return key;
    }
}
