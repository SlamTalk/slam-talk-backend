package sync.slamtalk.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class UnrefinedTeamMatchingDto {

    private Long teamMatchingId;
    private String teamName;
    private Long writerId;
    private String writerNickname;
    private String writerImageUrl;
    private Long opponentId;
    private String opponentNickname;
    private String opponentTeamName;
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
