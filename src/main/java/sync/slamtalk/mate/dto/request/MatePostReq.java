package sync.slamtalk.mate.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.entity.SkillLevelList;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatePostReq {

    @NotBlank
    private String title;

    private String content;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotBlank
    private String locationDetail;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RecruitedSkillLevelType skillLevel;

    private Integer maxParticipantsCenters;

    private Integer maxParticipantsGuards;

    private Integer maxParticipantsForwards;

    private Integer maxParticipantsOthers;


    public MatePost toEntity() {
        EntityToDtoMapper entityToDtoMapper = new EntityToDtoMapper();
        SkillLevelList tempSkillList = entityToDtoMapper.fromRecruitSkillLevel(skillLevel);
        String[] temp = locationDetail.split(" ", 2);
        String location = temp[0];
        String locationDetail = temp.length > 1 ? temp[1] : "";
        MatePost resultMatePost = MatePost.builder()
                .title(title)
                .scheduledDate(scheduledDate)
                .startTime(startTime)
                .endTime(endTime)
                .location(location)
                .locationDetail(locationDetail)
                .content(content)
                .skillLevel(skillLevel)
                .maxParticipantsCenters(maxParticipantsCenters)
                .currentParticipantsCenters(0)
                .maxParticipantsGuards(maxParticipantsGuards)
                .currentParticipantsGuards(0)
                .maxParticipantsForwards(maxParticipantsForwards)
                .currentParticipantsForwards(0)
                .maxParticipantsOthers(maxParticipantsOthers)
                .currentParticipantsOthers(0)
                .recruitmentStatus(RecruitmentStatusType.RECRUITING)
                .build();
        resultMatePost.configureSkillLevel(tempSkillList);
        return resultMatePost;
    }
}
