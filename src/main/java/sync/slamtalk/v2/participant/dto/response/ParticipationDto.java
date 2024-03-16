package sync.slamtalk.v2.participant.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.user.dto.response.UserSimpleResponseDto;
import sync.slamtalk.v2.mate.model.Position;
import sync.slamtalk.v2.participant.model.ParticipationStatus;
import sync.slamtalk.v2.participant.model.Participation;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationDto {

    private final Long participationId;
    private final UserSimpleResponseDto user;
    private final ParticipationStatus status;
    private final Position position;

    public static ParticipationDto of(Participation participation) {
        return new ParticipationDto(
                participation.getId(),
                UserSimpleResponseDto.from(participation.getUser()),
                participation.getStatus(),
                participation.getPosition()
        );
    }
}
