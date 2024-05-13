package sync.slamtalk.mate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.mate.error.MateErrorResponseCode;

import java.time.LocalTime;

@Getter
@AllArgsConstructor(staticName = "of")
public class MatePostKeyInformation {
    long matePostId;
    String title;
    String location;
    LocalTime startTime;
    RecruitmentStatusType recruitmentStatusType;
    ApplyStatusType applyStatusType;

    public static MatePostKeyInformation ofMyPost(MatePost post){
        return MatePostKeyInformation.of(
                post.getMatePostId(),
                post.getTitle(),
                post.getLocation() +" "+ post.getLocationDetail(),
                post.getStartTime(),
                post.getRecruitmentStatus(),
                null
        );
    }

    public static MatePostKeyInformation ofParticipantPost(MatePost post, Long myUserId){
        return MatePostKeyInformation.of(
                post.getMatePostId(),
                post.getTitle(),
                post.getLocation() +" "+ post.getLocationDetail(),
                post.getStartTime(),
                post.getRecruitmentStatus(),
                post.getParticipants().stream()
                        .filter(m -> m.getParticipantId().equals(myUserId))
                        .findFirst()
                        .orElseThrow(() -> new BaseException(MateErrorResponseCode.MATE_POST_NOT_FOUND))
                        .getApplyStatus()
        );
    }

}
