package sync.slamtalk.v2.schedule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 일정을 표현할 수 있는 엔티티 클래스입니다.
 * 일정이란, 특정 날짜의 특정 기간을 의미합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    //==== 생성 메서드 ====//
    public static Schedule of(LocalDate date, LocalTime startTime, LocalTime endTime) {
        assertStartTimeIsBeforeEndTime(startTime, endTime);

        Schedule schedule = new Schedule();
        schedule.date = date;
        schedule.startTime = startTime;
        schedule.endTime = endTime;
        return schedule;
    }

    //==== 비즈니스 로직 ====//

    public void changeTo(LocalDate date, LocalTime startTime, LocalTime endTime) {
        assertStartTimeIsBeforeEndTime(startTime, endTime);

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    private static void assertStartTimeIsBeforeEndTime(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
        }
    }

}
