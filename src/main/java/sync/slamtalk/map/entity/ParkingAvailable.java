package sync.slamtalk.map.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParkingAvailable {
    PARKING_AVAILABLE("가능"),
    PARKING_UNAVAILABLE("불가능");

    private final String parkingAvailableType;

    public static ParkingAvailable fromString(String value) {
        return "PARKING_AVAILABLE".equals(value) ? PARKING_AVAILABLE : PARKING_UNAVAILABLE;
    }
}
