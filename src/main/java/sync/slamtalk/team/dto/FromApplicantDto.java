package sync.slamtalk.team.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.SkillLevelType;

@Getter
@Setter
public class FromApplicantDto {

    @NonNull
    private String teamName;
    @NonNull
    private SkillLevelType skillLevel;

}
