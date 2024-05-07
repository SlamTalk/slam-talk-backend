package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NightLighting {
    LIGHT("있음"),
    NON_LIGHT("없음");

    private final String lightingType;

    public static NightLighting fromString(String value) {
        return "LIGHT".equals(value) ? LIGHT : NON_LIGHT;
    }
}
