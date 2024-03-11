package sync.slamtalk.team.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;

@Getter
@Setter
public class ToApplicantDto {
    Long teamApplicantTableId;
    String teamName;
    Long applicantId;
    String applicantNickname;
    Long teamMatchingId;
    SkillLevelType skillLevel;
    @Enumerated(EnumType.STRING)
    ApplyStatusType applyStatus;
}
