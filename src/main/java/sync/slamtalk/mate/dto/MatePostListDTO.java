package sync.slamtalk.mate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.time.LocalDateTime;
import java.util.List;

public class MatePostListDTO {
    @NonNull
    private long matePostId;
    @NonNull
    private String title;
    @NonNull
    private List<PositionListDTO> positionList;
    @NonNull
    private List<SkillLevelType> skillList;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startScheduledTime;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endScheduledTime;
}
