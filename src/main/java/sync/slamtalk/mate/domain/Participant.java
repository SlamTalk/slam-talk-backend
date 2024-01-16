package sync.slamtalk.mate.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sync.slamtalk.common.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_post_id")
    private MatePost matePost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String position; // 포지션

    @Column(nullable = false)
    private String skillLevel; // 스킬 레벨

    @Column(nullable = false)
    private String isHost; // 호스트 여부

    @Column(nullable = false)
    private String applyStatus; // 수락 여부

    public Participant() {
    }
}
