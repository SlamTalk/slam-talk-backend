package sync.slamtalk.user.dto.response;

import lombok.*;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.utils.UserLevelScore;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
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
    private Long level = 0L;
    private Long levelScore = 0L;
    private Long mateCompleteParticipationCount = 0L;
    private Long teamMatchingCompleteParticipationCount = 0L;

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
        return UserDetailsOtherInfo.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .selfIntroduction(user.getSelfIntroduction())
                .basketballSkillLevel(user.getBasketballSkillLevel() == null ? null : user.getBasketballSkillLevel().getLevel())
                .basketballPosition(user.getBasketballPosition() == null ? null : user.getBasketballPosition().getPosition())
                .levelScore(levelScore)
                .level(levelScore / UserLevelScore.LEVEL_THRESHOLD)
                .mateCompleteParticipationCount(mateCount)
                .teamMatchingCompleteParticipationCount(teamCount)
                .build();
    }
}
