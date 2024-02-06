package sync.slamtalk.team.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;
import sync.slamtalk.team.entity.TeamMatching;

@Getter
@Setter
public class ToApplicantDto {


    Long teamApplicantTableId;
    Long applicantId;
    String applicantNickname;
    Long chatroomId;
    Long TeamMatchingId;
    @Enumerated(EnumType.STRING)
    ApplyStatusType applyStatusType;

    String teamName;
    SkillLevelType skillLevel;
}
