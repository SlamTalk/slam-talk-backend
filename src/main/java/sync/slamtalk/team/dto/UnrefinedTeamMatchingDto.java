package sync.slamtalk.team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnrefinedTeamMatchingDto {

    private Long teamMatchingId;
    private String teamName;
    private Long writerId;
    private String writerNickname;
    private String writerImageUrl;
    private String title;
    private String content;
    private String location;
    private String locationDetail;
    private String numberOfMembers;
    private RecruitedSkillLevelType skillLevel;
    private LocalDate scheduledDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private RecruitmentStatusType recruitmentStatus;
}
