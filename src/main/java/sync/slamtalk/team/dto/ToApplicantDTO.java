package sync.slamtalk.team.dto;

import lombok.Getter;
import lombok.Setter;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;
import sync.slamtalk.team.entity.TeamApplicant;

@Getter
@Setter
public class ToApplicantDTO {
    Long teamApplicantTableId;
    String teamName;
    Long applicantId;
    String applicantNickname;
    Long teamMatchingId;
    SkillLevelType skillLevel;
    ApplyStatusType applyStatus;

    public static ToApplicantDTO from(TeamApplicant teamApplicant) {
        ToApplicantDTO dto = new ToApplicantDTO();
        dto.setTeamApplicantTableId(teamApplicant.getTeamApplicantTableId());
        dto.setTeamName(teamApplicant.getTeamName());
        dto.setApplicantId(teamApplicant.getApplicantId());
        dto.setApplicantNickname(teamApplicant.getApplicantNickname());
        dto.setTeamMatchingId(teamApplicant.getTeamMatching().getTeamMatchingId());
        dto.setSkillLevel(teamApplicant.getSkillLevel());
        dto.setApplyStatus(teamApplicant.getApplyStatus());
        return dto;
    }

}
