package sync.slamtalk.chat.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    // 일대일, 단체
    DIRECT("DM"), TOGETHER("TM");

    private final String key;
}
