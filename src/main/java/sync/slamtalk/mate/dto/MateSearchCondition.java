package sync.slamtalk.mate.dto;

import lombok.Data;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.time.LocalDateTime;

@Data
public class MateSearchCondition {

    private PositionType position;
    private SkillLevelType skillLevel;
    private String location;
    private LocalDateTime cursorTime;
}
