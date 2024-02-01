package sync.slamtalk.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ToTeamMatchingListDto {

    List<ToTeamFormDTO> teamMatchingList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime cursor;
}
