package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Fee {
    FREE("무료"),
    NON_FREE("유료");

    private final String fee_type;

    public static Fee fromString(String value) {
        return "NON_FREE".equals(value) ?  NON_FREE : FREE;
    }
}
