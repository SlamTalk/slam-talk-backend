package sync.slamtalk.common.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Schedule 객체의 생성을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /**
     * 일정을 생성합니다.
     * 생성한 일정은 DB에 저장됩니다.
     * @param date 날짜
     * @param start 시작 시간
     * @param end 종료 시간
     * @return 영속화된 Schedule 객체
     */
    public Schedule createSchedule(LocalDate date, LocalTime start, LocalTime end) {
        return scheduleRepository.save(Schedule.of(date, start, end));
    }
}
