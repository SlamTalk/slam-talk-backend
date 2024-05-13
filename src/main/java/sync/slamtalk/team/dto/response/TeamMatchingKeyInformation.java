package sync.slamtalk.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.error.TeamErrorResponseCode;

import java.time.LocalTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class TeamMatchingKeyInformation {
    long teamMatchingId;
    String title;
    String location;
    LocalTime startTime;
    RecruitmentStatusType recruitmentStatusType;
    ApplyStatusType applyStatusType;



    public static TeamMatchingKeyInformation ofMyPost(TeamMatching teamMatching){
        return TeamMatchingKeyInformation.of(
                teamMatching.getTeamMatchingId(),
                teamMatching.getTitle(),
                teamMatching.getLocationDetail() + " " + teamMatching.getLocationDetail(),
                teamMatching.getStartTime(),
                teamMatching.getRecruitmentStatus(),
                null
        );
    }

    public static TeamMatchingKeyInformation ofParticipantPost(TeamMatching teamMatching, long myUserId){
        return TeamMatchingKeyInformation.of(
                teamMatching.getTeamMatchingId(),
                teamMatching.getTitle(),
                teamMatching.getLocationDetail() + " " + teamMatching.getLocationDetail(),
                teamMatching.getStartTime(),
                teamMatching.getRecruitmentStatus(),
                teamMatching.getTeamApplicants().stream()
                        .filter(t -> t.getApplicantId().equals(myUserId))
                        .findFirst()
                        .orElseThrow(() -> new BaseException(TeamErrorResponseCode.TEAM_POST_NOT_FOUND))
                        .getApplyStatus()
        );
    }
}
