package sync.slamtalk.mate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sync.slamtalk.common.BaseEntity;


@Getter
@AllArgsConstructor
@Table(name = "participant")
public class Participant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mate_post_id")
    private MatePost matePost;

    @Column(nullable = false, name="user_id")
    private long userId; // 참여자 아이디 (임시로 long으로 설정)

    @Column(nullable = false, name="user_nickname")
    private String userNickname; // 참여자 닉네임

    private String position; // 포지션

    private String skillLevel; // 스킬 레벨

    @Column(nullable = false)
    private String applyStatus; // 수락 여부 "대기중", "수락", "거절"

    public Participant() {
    }

    @Builder
    public Participant(MatePost matePost, long userId, String userNickname, String position, String skillLevel, String applyStatus) {
        this.matePost = matePost;
        this.userId = userId;
        this.userNickname = userNickname;
        this.position = position;
        this.skillLevel = skillLevel;
        this.applyStatus = applyStatus;
    }
}
