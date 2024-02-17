package sync.slamtalk.team.entity;

import jakarta.persistence.*;
import lombok.*;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.PositionListDTO;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.SkillLevelType;
import sync.slamtalk.team.dto.ToApplicantDto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static sync.slamtalk.team.error.TeamErrorResponseCode.TEAM_POST_NOT_FOUND;

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
    private ApplyStatusType applyStatus; // WAITING, ACCEPTED, REJECTED, CANCELED
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



    public boolean checkCapabilities(List<String> requiredSkillLevel) {
        if(requiredSkillLevel.contains(this.skillLevel.getLevel()) == false){
            return false;
        }
        return true;
    }

    public ToApplicantDto makeDto(){
        ToApplicantDto dto = new ToApplicantDto();
        dto.setApplicantId(this.applicantId);
        dto.setApplicantNickname(this.applicantNickname);
        dto.setApplyStatusType(this.applyStatus);
        dto.setChatroomId(this.chatroomId);
        dto.setTeamName(this.teamName);
        dto.setTeamApplicantTableId(this.teamApplicantTableId);
        dto.setTeamMatchingId(this.teamMatching.getTeamMatchingId());
        dto.setSkillLevel(this.skillLevel);
        return dto;
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
                ", teamMatchingId=" + teamMatching.getTeamMatchingId() +
                ", teamName='" + teamName + '\'' +
                ", applicantId=" + applicantId +
                ", applicantNickname='" + applicantNickname + '\'' +
                ", isChatroomCreated=" + applyStatus +
                ", chatroomId=" + chatroomId +
                ", skillLevel=" + skillLevel +
                '}';
    }

    public void updateApplyStatus(ApplyStatusType applyStatus) {
        this.applyStatus = applyStatus;
    }

    public boolean isCorrespondTo(Long userId){
        return this.applicantId.equals(userId);
    }
}
