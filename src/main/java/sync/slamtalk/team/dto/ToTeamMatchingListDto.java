package sync.slamtalk.team.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ToTeamMatchingListDto {

    List<ToTeamFormDTO> teamMatchingList;

    String nextCursor;
}
