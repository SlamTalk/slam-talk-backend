package sync.slamtalk.v2.mate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.v2.mate.dto.request.MateRecruitmentCreateDto;
import sync.slamtalk.v2.mate.dto.response.MateRecruitmentDto;
import sync.slamtalk.v2.mate.model.MateRecruitment;
import sync.slamtalk.v2.mate.model.MateRecruitmentPositionInfo;
import sync.slamtalk.v2.mate.model.Position;
import sync.slamtalk.v2.mate.repository.MateRecruitmentRepository;
import sync.slamtalk.v2.schedule.Schedule;
import sync.slamtalk.v2.schedule.ScheduleHelper;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MateRecruitmentService {

    private final ScheduleHelper scheduleHelper;
    private final MateRecruitmentRepository mateRecruitmentRepository;
    private final UserRepository userRepository;

    /**
     * 메이트 모집을 생성합니다.
     *
     * @param dto 메이트 모집 생성 DTO
     * @return 생성된 메이트 모집의 ID
     */
    @Transactional
    public Long createMateRecruitment(MateRecruitmentCreateDto dto, Long userId) {

        User author = userRepository.findById(userId).orElseThrow(() -> new BaseException(null));

        Schedule schedule = scheduleHelper.createSchedule(dto.getDate(), dto.getStartTime(), dto.getEndTime());

        List<MateRecruitmentPositionInfo> positionInfos = List.of(
                MateRecruitmentPositionInfo.of(Position.CENTER, dto.getMaxParticipantsCenters()),
                MateRecruitmentPositionInfo.of(Position.GUARD, dto.getMaxParticipantsGuards()),
                MateRecruitmentPositionInfo.of(Position.FORWARD, dto.getMaxParticipantsForwards()),
                MateRecruitmentPositionInfo.of(Position.UNSPECIFIED, dto.getMaxParticipantsOthers())
        );


        MateRecruitment savedMateRecruitment = mateRecruitmentRepository.save(MateRecruitment.of(
                author,
                dto.getTitle(),
                dto.getContent(),
                schedule,
                dto.getSkillLevels(),
                positionInfos
        ));

        return savedMateRecruitment.getId();
    }

    /**
     * 메이트 모집을 조회합니다.
     * @param mateRecruitmentId 조회할 메이트 모집의 ID
     * @return 조회된 메이트 모집
     */
    public MateRecruitmentDto getMatePost(Long mateRecruitmentId) {
        MateRecruitment mateRecruitment = mateRecruitmentRepository.findActiveOneById(mateRecruitmentId)
                .orElseThrow(() -> new BaseException(null));

        return MateRecruitmentDto.of(mateRecruitment);
    }

}
