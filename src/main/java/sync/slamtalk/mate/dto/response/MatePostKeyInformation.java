package sync.slamtalk.mate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.mate.entity.RecruitmentStatusType;

import java.time.LocalTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class MatePostKeyInformation {
    long matePostId;
    String title;
    String locationDetail;
    LocalTime startTime;
    RecruitmentStatusType status;

    public static MatePostKeyInformation of(MatePostToDto post){
        return MatePostKeyInformation.of(
                post.getMatePostId(),
                post.getTitle(),
                post.getLocationDetail(),
                post.getStartTime(),
                post.getRecruitmentStatus()
        );
    }
}
