package sync.slamtalk.mate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;


@Entity
@Getter
@AllArgsConstructor
@Table(name = "participant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_table_id")
    private long participantTableId; // 참여자 테이블 아이디

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_post_id")
    private MatePost matePost; // 참여자가 참여한 글

    @Column(nullable = false, name="participant_id")
    private long participantId; // 참여자 아이디 * 매핑 불필요

    @Column(nullable = false, name="participant_nickname")
    private String participantNickname; // 참여자 닉네임

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplyStatusType applyStatus; // 참여자 신청 상태 "APPLYING", "ACCEPTED", "REJECTED", "CANCEL"

    private PositionType position; // 포지션 "CENTER", "GUARD", "FORWARD", "OTHER", * 향후에 통일된 Enum으로 변경 예정

    private String skillLevel; // 스킬 레벨

    private boolean softDelete; // 삭제 여부 * 향후에 BaseEntity에 softDelete 추가 시 삭제 예정

    @Builder
    public Participant(MatePost matePost, long participantId, String participantNickname, PositionType position, String skillLevel, ApplyStatusType applyStatus) {
        this.matePost = matePost;
        this.participantId = participantId;
        this.participantNickname = participantNickname;
        this.position = position;
        this.skillLevel = skillLevel;
        this.applyStatus = applyStatus;
    }

    public boolean softDeleteParticipant() {
        this.softDelete = true;
        return true;
    }
}
