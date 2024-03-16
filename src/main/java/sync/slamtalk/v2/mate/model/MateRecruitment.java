package sync.slamtalk.v2.mate.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.v2.participant.model.Participation;
import sync.slamtalk.v2.schedule.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * 팀원을 모집하는 글의 정보를 담는 엔티티 클래스입니다.
 * <p>
 * 기존의 {@link sync.slamtalk.mate.entity.MatePost}를 대체합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MateRecruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mate_recruitment_id")
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    private String title;

    private String content;

    // 경기를 할 상세 주소가 들어갑니다 ex) 서울특별시 강남구 테헤란로 427
    private String locationDetail;

    // 경기를 할 날짜와 시간
    @OneToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    // 모집하는 지원자의 수준
    @ElementCollection
    private List<RecruitingSkillLevel> skillLevels = new ArrayList<>();

    // 모집 상태 (모집중, 모집완료, 모집취소)
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus status; // 모집 상태

    // 모집하는 포지션과 필요한 인원 정보
    @OneToMany(mappedBy = "mateRecruitment", cascade = CascadeType.ALL)
    private List<MateRecruitmentPositionInfo> positionInfos = new ArrayList<>(); // 모집하는 포지션 정보

    @OneToMany(mappedBy = "mateRecruitment")
    private List<Participation> participations = new ArrayList<>(); // 참가 요청 정보

    // ==== 생성 메서드 ==== //
    public static MateRecruitment of(
            User author, String title, String content,
            Schedule schedule, List<RecruitingSkillLevel> skillLevels,
            List<MateRecruitmentPositionInfo> positionInfos
    ) {
        MateRecruitment mateRecruitment = new MateRecruitment();
        mateRecruitment.author = author;
        mateRecruitment.title = title;
        mateRecruitment.content = content;
        mateRecruitment.schedule = schedule;
        mateRecruitment.skillLevels = skillLevels;
        mateRecruitment.positionInfos = positionInfos;
        mateRecruitment.status = RecruitmentStatus.RECRUITING;
        return mateRecruitment;
    }

    //==== 비즈니스 로직 ====//

    public void cancel() {
        if (this.status == RecruitmentStatus.COMPLETED) {
            throw new IllegalStateException("이미 모집이 완료된 글은 취소할 수 없습니다.");
        }

        this.status = RecruitmentStatus.CANCELED;
    }

    public void complete() {
        if (this.status == RecruitmentStatus.CANCELED) {
            throw new IllegalStateException("취소된 글은 완료처리 할 수 없습니다.");
        }

        this.status = RecruitmentStatus.COMPLETED;
    }


}
