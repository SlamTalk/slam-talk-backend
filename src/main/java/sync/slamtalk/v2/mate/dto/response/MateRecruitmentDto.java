package sync.slamtalk.v2.mate.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.user.dto.response.UserSimpleResponseDto;
import sync.slamtalk.v2.mate.model.MateRecruitment;
import sync.slamtalk.v2.mate.model.RecruitingSkillLevel;
import sync.slamtalk.v2.mate.model.RecruitmentStatus;
import sync.slamtalk.v2.participant.dto.response.ParticipationDto;
import sync.slamtalk.v2.schedule.ScheduleDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MateRecruitmentDto {
    private Long matePostId;
    private UserSimpleResponseDto author;
    private ScheduleDto schedule;
    private String locationDetail;
    private List<RecruitingSkillLevel> skillLevels;
    private RecruitmentStatus status;
    private LocalDateTime createdAt;
    private List<PositionInfoDto> positionInfos;
    private List<ParticipationDto> participants;

    public static MateRecruitmentDto of(MateRecruitment mateRecruitment) {

        List<PositionInfoDto> positionInfos = mateRecruitment.getPositionInfos().stream()
                .map(PositionInfoDto::of)
                .toList();

        List<ParticipationDto> participations = mateRecruitment.getParticipations().stream()
                .map(ParticipationDto::of)
                .toList();

        return new MateRecruitmentDto(
                mateRecruitment.getId(),
                UserSimpleResponseDto.from(mateRecruitment.getAuthor()),
                ScheduleDto.of(mateRecruitment.getSchedule()),
                mateRecruitment.getLocationDetail(),
                mateRecruitment.getSkillLevels(),
                mateRecruitment.getStatus(),
                mateRecruitment.getCreatedAt(),
                positionInfos,
                participations
        );
    }

}
