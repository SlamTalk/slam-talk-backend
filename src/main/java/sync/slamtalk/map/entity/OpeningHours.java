package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OpeningHours implements EnumType{
    ALL_NIGHT("24시"),
    NON_ALL_NIGHT("제한");

    private final String type;

    public static OpeningHours fromString(String value) {
        for (OpeningHours openingHours : values()) {
            if (openingHours.type.equals(value)) {
                return openingHours;
            }
        }

        return null;
    }

    public String getType() {
        return this.type;
    }
}
