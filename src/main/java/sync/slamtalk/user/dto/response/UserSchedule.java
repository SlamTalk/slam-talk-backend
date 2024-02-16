package sync.slamtalk.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.mate.dto.MatePostToDto;
import sync.slamtalk.team.dto.ToTeamFormDTO;

import java.util.List;

/**
 * 임박한 유저 스케줄 반환하는 response dto
 * */
@AllArgsConstructor
@Getter
public class UserSchedule {
    List<ToTeamFormDTO> teamMatchingList;
    List<MatePostToDto> mateList;
}
