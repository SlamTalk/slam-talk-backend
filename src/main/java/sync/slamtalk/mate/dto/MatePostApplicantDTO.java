package sync.slamtalk.mate.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.*;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.SkillLevelType;

@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Valid
public class MatePostApplicantDTO {

    private long participantTableId;
    private String participantNickname;
    private ApplyStatusType applyStatus;
    private PositionType position;
    private SkillLevelType skillLevel;

    public MatePostApplicantDTO(long participantTableId, ApplyStatusType applyStatus) {
        this.participantTableId = participantTableId;
        this.applyStatus = applyStatus;
    }

    public MatePostApplicantDTO(PositionType position, SkillLevelType skillLevel) {
        this.skillLevel = skillLevel;
        this.position = position;
    }

    public MatePostApplicantDTO(long participantTableId, String participantNickname, PositionType position, SkillLevelType skillLevel, ApplyStatusType applyStatus) {
        this.participantTableId = participantTableId;
        this.participantNickname = participantNickname;
        this.position = position;
        this.skillLevel = skillLevel;
        this.applyStatus = applyStatus;
    }
}
