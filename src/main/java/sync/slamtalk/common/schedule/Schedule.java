package sync.slamtalk.common.schedule;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 일정을 저장하는 엔티티 클래스입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 일정 날짜
    private LocalDate date;

    // 일정 시작 시간
    private LocalTime start;

    // 일정 종료 시간
    private LocalTime end;

    public static Schedule of(LocalDate date, LocalTime start, LocalTime end) {
        Schedule schedule = new Schedule();
        schedule.date = date;
        schedule.start = start;
        schedule.end = end;
        return schedule;
    }

    /**
     * 일정을 수정합니다.
     * @param date 날짜
     * @param start 시작 시간
     * @param end 종료 시간
     */
    public void update(LocalDate date, LocalTime start, LocalTime end) {
        this.date = date;
        this.start = start;
        this.end = end;
    }
}
