package sync.slamtalk.mate.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatePostRes {
    private long matePostId;
    private long writerId;
    private String writerNickname;
    private String writerImageUrl;
    private String title;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private String locationDetail;
    private RecruitedSkillLevelType skillLevel;
    private List<String> skillLevelList;
    private RecruitmentStatusType recruitmentStatus;
    private LocalDateTime createdAt;

    private List<PositionListDto> positionList;
    private List<ParticipantDto> participants;
}
