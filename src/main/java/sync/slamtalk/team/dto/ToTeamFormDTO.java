package sync.slamtalk.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.entity.TeamApplicant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ToTeamFormDTO {

    private Long teamMatchingId;

    @NotBlank(message = "팀명을 입력해주세요.")
    private String teamName;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NonNull
    private Long writerId;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "상세 위치를 입력해주세요.")
    private String locationDetail;

    @NonNull
    private Integer numberOfMembers;

    @NonNull
    private List<String> skillLevel;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NonNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    @NonNull
    @Enumerated(EnumType.STRING)
    private RecruitmentStatusType recruitmentStatusType;

    @NonNull
    private List<ToApplicantDto> teamApplicantsDto;
}
