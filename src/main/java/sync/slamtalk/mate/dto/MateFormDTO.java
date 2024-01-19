package sync.slamtalk.mate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class MateFormDTO {
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime scheduledTime;
    private String locationDetail;
    private String content;
    private int maxParticipantsCenters;
    private int maxParticipantsGuards;
    private int maxParticipantsForwards;
    private int maxParticipantsOthers; // 모집 포지션 무관
    private SkillLevelType skillLevel; // 원하는 스킬 레벨 "BEGINNER", "INTERMEDIATE", "MASTER", "IRRELEVANT"

    @JsonIgnore
    public MatePost toEntity(int userId, String userNickname) {
        return MatePost.builder()
                .userId(userId)
                .userNickname(userNickname)
                .title(title)
                .scheduledTime(scheduledTime)
                .locationDetail(locationDetail)
                .content(content)
                .maxParticipantsCenters(maxParticipantsCenters)
                .maxParticipantsGuards(maxParticipantsGuards)
                .maxParticipantsForwards(maxParticipantsForwards)
                .maxParticipantsOthers(maxParticipantsOthers)
                .skillLevel(skillLevel)
                .softDelete(false)
                .recruitmentStatus(RecruitmentStatusType.RECRUITING)
                .build();
    }

}
