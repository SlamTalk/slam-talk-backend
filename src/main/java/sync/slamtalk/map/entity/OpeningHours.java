package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpeningHours {
    ALL_NIGHT("24시"),
    NON_ALL_NIGHT("제한");

    private final String openingHours_type;

    public static OpeningHours fromString(String value) {
        return "ALL_NIGHT".equals(value) ? ALL_NIGHT : NON_ALL_NIGHT;
    }
}
