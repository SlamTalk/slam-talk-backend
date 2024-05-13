package sync.slamtalk.mate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MyMateListRes {

    List<MatePostKeyInformation> authoredPost; // 내가 쓴 글
    List<MatePostKeyInformation> participatedPost; // 내가 참여한 글
}
