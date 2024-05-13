package sync.slamtalk.team.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.team.dto.ToTeamFormDTO;

import java.util.List;

/**
 * 나의 팀매칭 리스트 상세보기 reponse dto
 * */
@Getter
@AllArgsConstructor
public class MyTeamMatchingListRes {
    List<TeamMatchingKeyInformation> authoredPost; // 내가 쓴 글
    List<TeamMatchingKeyInformation> participatedPost; // 내가 참여한 글
}
