package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Fee implements EnumType{
    FREE("무료"),
    NON_FREE("유료");

    private final String type;

    public static Fee fromString(String value) {
        for (Fee fee : values()) {
            if (fee.type.equals(value)) {
                return fee;
            }
        }

        return null;
    }

    public String getType(){
        return this.type;
    }
}
