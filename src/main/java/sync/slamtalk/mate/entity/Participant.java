package sync.slamtalk.mate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;

import java.util.Objects;


@Entity
@Getter
@AllArgsConstructor
@Table(name = "participant")
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_table_id")
    private long participantTableId; // 참여자 테이블 아이디

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_post_id")
    private MatePost matePost; // 참여자가 참여한 글

    @Column(nullable = false, name="participant_email")
    private long participantId; // 참여자 아이디 * 매핑 불필요

    @Column(nullable = false, name="participant_nickname")
    private String participantNickname; // 참여자 닉네임

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplyStatusType applyStatus; // 참여자 신청 상태 "WAITING", "ACCEPTED", "REJECTED", "CANCEL"

    private PositionType position; // 포지션 "CENTER", "GUARD", "FORWARD", "UNSPECIFIED", * 향후에 통일된 Enum으로 변경 예정

    private SkillLevelType skillLevel; // 스킬 레벨  HIGH, MIDDLE, LOW, BEGINNER

    public Participant() {
    }

    public Participant(long participantId, String participantNickname, PositionType position, SkillLevelType skillLevel) {

        this.participantId = participantId;
        this.participantNickname = participantNickname;
        this.position = position;
        this.skillLevel = skillLevel;
        this.applyStatus = ApplyStatusType.WAITING;
        this.matePost = null;
    }

    public ApplyStatusType updateApplyStatus(ApplyStatusType applyStatus) {
        this.applyStatus = applyStatus;
        return this.applyStatus;
    }

    public boolean softDeleteParticipant() {
        this.delete();
        return true;
    }

    public boolean connectParent(MatePost matePost) {
        this.matePost = matePost;
        matePost.getParticipants().add(this);
        return true;

    }

    public boolean disconnectParent() {
        this.matePost.getParticipants().remove(this);
        this.matePost = null;
        return true;
    }

    @Override
    public String toString() { // 양방향 연관 관계로 인한 순환 참조 고려한 toString
        return "Participant{" +
                "participantTableId=" + participantTableId +
                ", participantId='" + participantId + '\'' +
                ", participantNickname='" + participantNickname + '\'' +
                ", applyStatus=" + applyStatus +
                ", position=" + position +
                ", skillLevel=" + skillLevel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return participantId == that.participantId && Objects.equals(participantTableId, that.participantTableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantNickname, participantId);
    }
}
