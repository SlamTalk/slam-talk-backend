package sync.slamtalk.v2.participant.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.v2.mate.model.MateRecruitment;
import sync.slamtalk.v2.mate.model.Position;

@Entity
@Getter
@Table(name = "participant_v2")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mate_recruitment_id")
    private MateRecruitment mateRecruitment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    @Enumerated(EnumType.STRING)
    private Position position;

    public static Participation of(MateRecruitment mateRecruitment, User user, Position position) {
        Participation participation = new Participation();
        participation.mateRecruitment = mateRecruitment;
        participation.user = user;
        participation.status = ParticipationStatus.WAITING;
        participation.position = position;
        return participation;
    }
}
