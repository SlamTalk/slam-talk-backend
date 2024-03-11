package sync.slamtalk.team.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TeamApplicant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamApplicantTableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_matching_id")
    private TeamMatching teamMatching;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private Long applicantId;

    @Column(nullable = false)
    private String applicantNickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplyStatusType applyStatus; // WAITING, ACCEPTED, REJECTED

    private Long chatroomId;

    @Enumerated(EnumType.STRING)
    private SkillLevelType skillLevel;

    public void connectTeamMatching(TeamMatching teamMatching) {
        teamMatching.connectApplicant(this);
        this.teamMatching = teamMatching;
    }

    public void disconnectTeamMatching() {
        this.teamMatching.getTeamApplicants().remove(this);
        this.teamMatching = null;
    }

    @Override
    public String toString() {
        return "TeamApplicant{" +
                "teamApplicantTableId=" + teamApplicantTableId +
                ", teamMatchingId=" + teamMatching.getTeamMatchingId() +
                ", teamName='" + teamName + '\'' +
                ", applicantId=" + applicantId +
                ", applicantNickname='" + applicantNickname + '\'' +
                ", isChatroomCreated=" + applyStatus +
                ", skillLevel=" + skillLevel +
                '}';
    }

    public void updateApplyStatus(ApplyStatusType applyStatus) {
        this.applyStatus = applyStatus;
    }

    public boolean isCorrespondTo(Long userId) {
        return this.applicantId.equals(userId);
    }
}
