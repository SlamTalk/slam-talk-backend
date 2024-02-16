package sync.slamtalk.mate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.mate.dto.MatePostToDto;

import java.util.List;

@AllArgsConstructor
@Getter
public class MyMateListRes {

    List<MatePostToDto> authoredPost; // 내가 쓴 글
    List<MatePostToDto> participatedPost; // 내가 참여한 글
}
