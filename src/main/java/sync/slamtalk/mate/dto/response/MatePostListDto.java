package sync.slamtalk.mate.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
public class MatePostListDto {
    private List<MatePostToDto> matePostList;
    private String nextCursor;
}
