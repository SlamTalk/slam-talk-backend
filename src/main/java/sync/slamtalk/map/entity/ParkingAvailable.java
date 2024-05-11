package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParkingAvailable implements EnumType{
    PARKING_AVAILABLE("가능"),
    PARKING_UNAVAILABLE("불가능");

    private final String type;

    public static ParkingAvailable fromString(String value) {
        for (ParkingAvailable parkingAvailable : values()) {
            if (parkingAvailable.type.equals(value)) {
                return parkingAvailable;
            }
        }

        return null;
    }

    public String getType() {
        return this.type;
    }
}
