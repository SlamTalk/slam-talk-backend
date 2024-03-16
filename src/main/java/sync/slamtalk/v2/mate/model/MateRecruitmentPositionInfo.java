package sync.slamtalk.v2.mate.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메이트 모집의 포지션 별 정보를 나타내는 엔티티 클래스입니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MateRecruitmentPositionInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_recruitment_position_info_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mate_recruitment_id")
    private MateRecruitment mateRecruitment;

    @Enumerated(EnumType.STRING)
    private Position position;

    // 해당 포지션에 필요한 인원 수
    private int requiredNumber;

    // 현재 해당 포지션에 참가한 인원 수
    private int currentNumber;

    public static MateRecruitmentPositionInfo of(Position position, int requiredNumber) {
        MateRecruitmentPositionInfo mateRecruitmentPositionInfo = new MateRecruitmentPositionInfo();
        mateRecruitmentPositionInfo.position = position;
        mateRecruitmentPositionInfo.requiredNumber = requiredNumber;
        mateRecruitmentPositionInfo.currentNumber = 0;
        return mateRecruitmentPositionInfo;
    }

    /**
     * 해당 포지션에 참가한 인원이 꽉 찼는지 확인합니다.
     * @return 인원을 더 받을 수 있는 경우 true, 아닌 경우 false를 반환합니다.
     */
    public boolean isFull() {
        return requiredNumber <= currentNumber;
    }

    /**
     *
     */
    public void increaseCurrentNumber() {
        if (isFull()) {
            throw new IllegalStateException("해당 포지션에 참가한 인원이 이미 꽉 찼습니다.");
        }

        currentNumber++;
    }

}
