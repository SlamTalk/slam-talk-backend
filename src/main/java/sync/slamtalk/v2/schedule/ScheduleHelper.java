package sync.slamtalk.v2.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 스케쥴 객체를 생성 및 저장하는 역할을 하는 클래스입니다.
 */
@Component
@RequiredArgsConstructor
@Transactional
public class ScheduleHelper {

    private final ScheduleRepository scheduleRepository;

    /**
     * 일정을 생성합니다.
     * @param date 생성할 일정의 날짜
     * @param startTime 생성할 일정의 시작 시간
     * @param endTime 생성할 일정의 종료 시간
     * @return 생성된 일정 객체, 영속화된 상태입니다.
     */
    public Schedule createSchedule(LocalDate date, LocalTime startTime, LocalTime endTime) {
        return scheduleRepository.save(Schedule.of(date, startTime, endTime));
    }

}
