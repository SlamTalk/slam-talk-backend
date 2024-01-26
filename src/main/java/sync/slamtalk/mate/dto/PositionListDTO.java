package sync.slamtalk.mate.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.PositionType;

@Getter
@Setter
public class PositionListDTO {
    @NonNull
    @Enumerated(EnumType.STRING)
    PositionType position;
    @NonNull
    int maxPosition;
    @NonNull
    int currentPosition;

    public PositionListDTO(PositionType position, int maxPosition, int currentPosition) {
        this.position = position;
        this.maxPosition = maxPosition;
        this.currentPosition = currentPosition;
    }
}
