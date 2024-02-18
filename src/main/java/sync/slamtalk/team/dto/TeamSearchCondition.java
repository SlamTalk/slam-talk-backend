package sync.slamtalk.team.dto;

import lombok.Data;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.time.LocalDateTime;

@Data
public class TeamSearchCondition {
    private SkillLevelType skillLevel;
    private String location;
    private String versesN;
    private LocalDateTime cursorTime;
}
