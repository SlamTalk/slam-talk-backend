package sync.slamtalk.v2.mate.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.v2.mate.model.MateRecruitmentPositionInfo;
import sync.slamtalk.v2.mate.model.Position;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PositionInfoDto {

    private final Position position;
    private final int requiredNumber;
    private final int currentNumber;

    public static PositionInfoDto of(MateRecruitmentPositionInfo info) {
        return new PositionInfoDto(
                info.getPosition(),
                info.getRequiredNumber(),
                info.getCurrentNumber()
        );
    }
}
