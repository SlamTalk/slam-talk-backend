package sync.slamtalk.mate.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sync.slamtalk.mate.entity.Participant;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnrefinedMatePostDTO {
    private Long writerId;
    private String writerNickname;
    private String imageUrl;
    private Long matePostId;
    private String title;
    private String content;
    private RecruitedSkillLevelType skillLevel;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String locationDetail;
    private RecruitmentStatusType recruitmentStatus;
    private Integer maxParticipantsCenters;
    private Integer currentParticipantsCenters;
    private Integer maxParticipantsGuards;
    private Integer currentParticipantsGuards;
    private Integer maxParticipantsForwards;
    private Integer currentParticipantsForwards;
    private Integer maxParticipantsOthers;
    private Integer currentParticipantsOthers;
    private LocalDateTime createdAt;
    private List<FromParticipantDto> participants;

}
