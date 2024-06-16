package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NightLighting implements EnumType{
    LIGHT("있음"),
    NON_LIGHT("없음");

    private final String type;

    public static NightLighting fromString(String value) {
        for (NightLighting nightLighting : values()) {
            if (nightLighting.name().equals(value)) {
                return nightLighting;
            }
        }

        return null;
    }

    public String getType(){
        return this.type;
    }
}
