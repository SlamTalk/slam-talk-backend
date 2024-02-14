package sync.slamtalk.mate.dto;

import lombok.Getter;
import lombok.Setter;
import sync.slamtalk.mate.entity.MatePost;

import java.util.List;



@Getter
@Setter
public class MatePostListDTO {
    private List<MatePostToDto> matePostList;
    private String nextCursor;
}
