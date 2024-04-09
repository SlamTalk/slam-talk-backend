package sync.slamtalk.mate.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import sync.slamtalk.common.schedule.Schedule;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatePostReq {

    @NotBlank
    private String title;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotBlank
    private String locationDetail;

    @Enumerated(EnumType.STRING)
    private RecruitedSkillLevelType skillLevel;

    private int maxParticipantsCenters;

    private int maxParticipantsGuards;

    private int maxParticipantsForwards;

    private int maxParticipantsOthers;


    @JsonIgnore
    public MatePost toEntity(Schedule schedule) {
        EntityToDtoMapper entityToDtoMapper = new EntityToDtoMapper();
        SkillLevelList tempSkillList = entityToDtoMapper.fromRecruitSkillLevel(skillLevel);
        String[] temp = locationDetail.split(" ", 2);
        String location = temp[0];
        String locationDetail = temp.length > 1 ? temp[1] : "";
        MatePost resultMatePost = MatePost.builder()
                .title(title)
                .schedule(schedule)
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
                .participants(new ArrayList<>())
                .build();
        resultMatePost.configureSkillLevel(tempSkillList);
        return resultMatePost;
    }
}
