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
    private boolean includingExpired = false; // 만료된 팀 매칭도 포함할지 여부. 기본값은 false(포함하지 않음)
}
