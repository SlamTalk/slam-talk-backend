package sync.slamtalk.user.dto.response;

import lombok.*;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.entity.UserRole;
import sync.slamtalk.user.utils.UserLevelScore;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UserDetailsMyInfo {
    /* 개인 정보 관련 */
    private String email;
    private SocialType socialType;
    private UserRole role;

    /* 공개되어도 상관없는 부분 */
    private Long id;
    private String nickname;
    private String imageUrl;

    /* 마이페이지 기능 */
    private String selfIntroduction;

    /* 정보 수집 부분 */
    private String basketballSkillLevel;
    private String basketballPosition;
    private Long level;
    private Long levelScore;
    private Long mateCompleteParticipationCount;
    private Long teamMatchingCompleteParticipationCount;

    /**
     * 나의 프로필 조회 시 필요한 정보를 반환하는 생성자
     *
     * @param user                           db에서 조회한 user 객체
     * @param mateCompleteParticipationCount 메이트 참여완료 횟수
     * @return UserDetailsInfoResponseDto 개인정보 포함된 정보
     */
    public static UserDetailsMyInfo generateMyProfile(
            User user,
            long levelScore,
            long mateCompleteParticipationCount,
            long teamCompleteParticipationCount
    ) {
        return UserDetailsMyInfo.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .selfIntroduction(user.getSelfIntroduction())
                .basketballSkillLevel(user.getBasketballSkillLevel() == null ? null : user.getBasketballSkillLevel().getLevel())
                .basketballPosition(user.getBasketballPosition() == null ? null : user.getBasketballPosition().getPosition())
                .level(levelScore / UserLevelScore.LEVEL_THRESHOLD)
                .levelScore(levelScore)
                .mateCompleteParticipationCount(mateCompleteParticipationCount)
                .teamMatchingCompleteParticipationCount(teamCompleteParticipationCount)
                .email(user.getEmail())
                .socialType(user.getSocialType())
                .role(user.getRole())
                .build();
    }
}
