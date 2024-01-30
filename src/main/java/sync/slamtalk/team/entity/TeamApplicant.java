package sync.slamtalk.team.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamApplicant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long teamApplicantTableId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_matching_id")
    private TeamMatching teamMatchingId;
    @Column(nullable = false)
    private long applicantId;
    @Column(nullable = false)
    private String applicantNickname;
    @Column(nullable = false)
    private boolean isChatroomCreated;
    private long chatroomId;

    public void connectTeamMatching(TeamMatching teamMatching) {
        this.teamMatchingId = teamMatching;
        teamMatching.getTeamApplicants().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamApplicant that = (TeamApplicant) o;
        return teamApplicantTableId == that.teamApplicantTableId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamApplicantTableId);
    }

    @Override
    public String toString() {
        return "TeamApplicant{" +
                "teamApplicantTableId=" + teamApplicantTableId +
                ", teamMatchingId=" + teamMatchingId +
                ", applicantId=" + applicantId +
                ", applicantNickname='" + applicantNickname + '\'' +
                ", isChatroomCreated=" + isChatroomCreated +
                ", chatroomId=" + chatroomId +
                '}';
    }

}
