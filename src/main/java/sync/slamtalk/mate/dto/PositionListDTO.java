package sync.slamtalk.mate.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.PositionType;

@Getter
@Setter
public class PositionListDTO {
    PositionType position;
    int maxPosition;
    int currentPosition;

    public PositionListDTO(PositionType position, int maxPosition, int currentPosition) {
        this.position = position;
        this.maxPosition = maxPosition;
        this.currentPosition = currentPosition;
    }
}
