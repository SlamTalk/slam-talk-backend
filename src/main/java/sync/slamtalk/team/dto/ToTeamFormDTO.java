package sync.slamtalk.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;

import java.time.LocalDateTime;

public class ToTeamFormDTO {

    @NotBlank(message = "팀명을 입력해주세요.")
    private String teamName;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NonNull
    private long writerId;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "상세 위치를 입력해주세요.")
    private String locationDetail;

    @NonNull
    private int numberOfMembers;

    @NonNull
    private RecruitedSkillLevelType skillLevel;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startScheduledTime;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endScheduledTime;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private long createdTime;

    @NonNull
    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatusType;
}
