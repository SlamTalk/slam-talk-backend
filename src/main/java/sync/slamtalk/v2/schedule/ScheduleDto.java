package sync.slamtalk.v2.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 일정을 표현할 수 있는 DTO 클래스입니다.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime endTime;

    public static ScheduleDto of(Schedule schedule) {
        return new ScheduleDto(
                schedule.getDate(),
                schedule.getStartTime(),
                schedule.getEndTime()
        );
    }

}
