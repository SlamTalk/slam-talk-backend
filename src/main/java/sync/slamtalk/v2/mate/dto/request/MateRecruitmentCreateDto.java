package sync.slamtalk.v2.mate.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.v2.mate.model.RecruitingSkillLevel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class MateRecruitmentCreateDto {

    @NotBlank
    private String title;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime endTime;

    @NotBlank
    private String locationDetail;

    private List<RecruitingSkillLevel> skillLevels = new ArrayList<>();

    @NotNull
    private Integer maxParticipantsCenters;

    @NotNull
    private Integer maxParticipantsGuards;

    @NotNull
    private Integer maxParticipantsForwards;

    @NotNull
    private Integer maxParticipantsOthers;

}
