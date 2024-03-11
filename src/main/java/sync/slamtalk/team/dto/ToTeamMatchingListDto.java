package sync.slamtalk.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ToTeamMatchingListDto {

    List<ToTeamFormDTO> teamMatchingList;
    String nextCursor;
}
