package sync.slamtalk.mate.dto.response;
import lombok.Data;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.Participant;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.SkillLevelType;

@Data
public class ParticipantDto {

    private Long participantTableId;

    private Long matePostId;

    private Long participantId;

    private String participantNickname;

    private ApplyStatusType applyStatus;

    private PositionType position;

    private SkillLevelType skillLevel;

    public ParticipantDto() {
    }

    public ParticipantDto(Participant participant) {
        this.participantTableId = participant.getParticipantTableId();
        this.matePostId = participant.getMatePost().getMatePostId();
        this.participantId = participant.getParticipantId();
        this.participantNickname = participant.getParticipantNickname();
        this.applyStatus = participant.getApplyStatus();
        this.position = participant.getPosition();
        this.skillLevel = participant.getSkillLevel();
    }

}
