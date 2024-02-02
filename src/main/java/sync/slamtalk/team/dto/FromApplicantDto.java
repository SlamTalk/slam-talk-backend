package sync.slamtalk.team.dto;

import lombok.Getter;
import lombok.Setter;
import sync.slamtalk.mate.entity.SkillLevelType;

@Getter
@Setter
public class FromApplicantDto {

    private String teamName;
    private SkillLevelType skillLevel;

}
