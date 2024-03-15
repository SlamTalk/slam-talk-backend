package sync.slamtalk.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ToTeamFormDTO {

    private Long teamMatchingId;

    @NotBlank(message = "팀명을 입력해주세요.")
    private String teamName;

    @NotBlank(message = "작성자 아이디를 입력해주세요.")
    private Long writerId;

    @NotBlank(message = "작성자 닉네임을 입력해주세요.")
    private String writerNickname;

    @NotBlank(message = "이미지를 입력해주세요.")
    private String writerImageUrl;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "상세 위치를 입력해주세요.")
    private String locationDetail;

    private String numberOfMembers;

    private List<String> skillLevelList;

    @NotNull
    private RecruitedSkillLevelType skillLevel;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull
    private LocalDateTime createdAt;

    private RecruitmentStatusType recruitmentStatusType;

    private List<ToApplicantDTO> teamApplicants;
}
