package sync.slamtalk.user.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 레벨 시스템 상수를 저장하는 클래스 입니다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLevelScore {
    /* 레벨 시스템을 위한 상수 */
    public static final Long LEVEL_THRESHOLD = 10L;
    public static final Long MATE_LEVEL_SCORE = 5L;
    public static final Long TEAM_MATCHING_LEVEL_SCORE = 5L;
    public static final Long COMMUNITY_SCORE = 3L;
    public static final Long ATTEND_SCORE = 1L;
    public static final Long BASKETBALL_COURT_TIP_SCORE = 30L;
}
