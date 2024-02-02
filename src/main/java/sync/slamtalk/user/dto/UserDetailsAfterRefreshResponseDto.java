package sync.slamtalk.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;

@Getter
@AllArgsConstructor
@Builder
public class UserDetailsAfterRefreshResponseDto {
    /* 개인 정보 관련 */
    private String email;
    private SocialType socialType;

    /* 공개되어도 상관없는 부분 */
    private Long id;
    private String nickname;
    private String imageUrl;
    private Boolean firstLoginCheck;

    /* 마이페이지 기능 */
    private String selfIntroduction;

    /* 정보 수집 부분 */
    private String basketballSkillLevel;
    private String basketballPosition;
    private Long level;
    private Long levelScore;
    private Long mateCompleteParticipationCount;
    private Long teamMatchingCompleteParticipationCount;

    public static UserDetailsAfterRefreshResponseDto from(
            User user,
            long levelScore,
            long mateCompleteParticipationCount,
            long teamMatchingCompleteParticipationCount
    ){
        return UserDetailsAfterRefreshResponseDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .firstLoginCheck(user.getFirstLoginCheck())
                .selfIntroduction(user.getSelfIntroduction())
                .basketballSkillLevel( user.getBasketballSkillLevel() == null? null: user.getBasketballSkillLevel().getLevel())
                .basketballPosition(user.getBasketballPosition() == null ?null:user.getBasketballPosition().getPosition())
                .level(levelScore/ User.LEVEL_THRESHOLD)
                .levelScore(levelScore)
                .mateCompleteParticipationCount(mateCompleteParticipationCount)
                .teamMatchingCompleteParticipationCount(teamMatchingCompleteParticipationCount)
                .email(user.getEmail())
                .socialType(user.getSocialType())
                .build();
    }
}
