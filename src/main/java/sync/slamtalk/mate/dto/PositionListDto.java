package sync.slamtalk.mate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PositionListDto {
    private String position;
    private int maxPosition;
    private int currentPosition;
}
