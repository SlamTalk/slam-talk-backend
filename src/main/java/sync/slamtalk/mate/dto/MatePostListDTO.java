package sync.slamtalk.mate.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MatePostListDTO {
    private List<MatePostDTO> matePostList;
    private String nextCursor;
}
