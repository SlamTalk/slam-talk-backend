package sync.slamtalk.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;

import java.time.LocalDateTime;

@Getter
@Setter
public class FromTeamFormDTO {
    @NotBlank(message = "팀명을 입력해주세요.")
    private String teamName;

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "상세 위치를 입력해주세요.")
    private String locationDetail;

    @NonNull
    private int numberOfMembers;

    @NonNull
    @Enumerated(EnumType.STRING)
    private RecruitedSkillLevelType skillLevel;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startScheduledTime;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endScheduledTime;


    public String toString() {
        return "FromTeamFormDTO(teamName=" + this.getTeamName() + ", title=" + this.getTitle() + ", content=" + this.getContent() + ", locationDetail=" + this.getLocationDetail() + ", numberOfMembers=" + this.getNumberOfMembers() + ", skillLevel=" + this.getSkillLevel() + ", startScheduledTime=" + this.getStartScheduledTime() + ", endScheduledTime=" + this.getEndScheduledTime() + ")";
    }

}
