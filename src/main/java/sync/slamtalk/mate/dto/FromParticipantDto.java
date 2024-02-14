package sync.slamtalk.mate.dto;
import lombok.Data;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.SkillLevelType;

@Data
public class FromParticipantDto {

    private Long participantTableId;

    private Long matePostId;

    private Long participantId;

    private String participantNickname;

    private ApplyStatusType applyStatus;

    private PositionType position;

    private SkillLevelType skillLevel;

}
