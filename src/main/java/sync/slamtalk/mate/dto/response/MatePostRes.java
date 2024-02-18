package sync.slamtalk.mate.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatePostRes {
    @JsonIgnore
    private EntityToDtoMapper entityToDtoMapper;
    private long matePostId;
    private long writerId;
    private String writerNickname;
    private String writerImageUrl;
    private String title;
    private String content;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String locationDetail;
    private RecruitedSkillLevelType skillLevel;
    private List<String> skillLevelList;
    private RecruitmentStatusType recruitmentStatus;

    private List<PositionListDto> positionList;
    private List<ParticipantDto> participants;
}
