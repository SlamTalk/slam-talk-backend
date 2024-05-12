package sync.slamtalk.user.dto.response;

import lombok.*;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.utils.UserLevelScore;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UserDetailsOtherInfo {
    /* 공개되어도 상관없는 부분 */
    private Long id;
    private String nickname;
    private String imageUrl;

    /* 마이페이지 기능 */
    private String selfIntroduction;

    /* 정보 수집 부분 */
    private String basketballSkillLevel;
    private String basketballPosition;
    private long level;
    private long levelScore;
    private long mateCompleteParticipationCount;
    private long teamMatchingCompleteParticipationCount;

    /**
     * 상대방 프로필 조회 시 필요한 정보를 반환하는 생성자
     *
     * @param user      db에서 조회한 user 객체
     * @param mateCount 메이트 총 개수
     * @param teamCount 팀 총 개수
     * @return UserDetailsInfoResponseDto 개인정보 제외된 정보
     */
    public static UserDetailsOtherInfo generateOtherUserProfile(
            User user,
            long levelScore,
            long mateCount,
            long teamCount
    ) {
        String basketballSkillLevel = user.getBasketballSkillLevel() != null ? user.getBasketballSkillLevel().getLevel() : null;
        String basketballPosition = user.getBasketballPosition() != null ? user.getBasketballPosition().getPosition() : null;
        long level = levelScore / UserLevelScore.LEVEL_THRESHOLD;

        return new UserDetailsOtherInfo(
                user.getId(),
                user.getNickname(),
                user.getImageUrl(),
                user.getSelfIntroduction(),
                basketballSkillLevel,
                basketballPosition,
                level,
                levelScore,
                mateCount,
                teamCount
        );
    }
}
