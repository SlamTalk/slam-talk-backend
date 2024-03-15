package sync.slamtalk.mate.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PositionListDto {
    @NonNull
    String position;
    @NonNull
    int maxPosition;
    @NonNull
    int currentPosition;

    public PositionListDto(String position, int maxPosition, int currentPosition) {
        this.position = position;
        this.maxPosition = maxPosition;
        this.currentPosition = currentPosition;
    }
}
