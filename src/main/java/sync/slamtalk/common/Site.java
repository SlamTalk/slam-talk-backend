package sync.slamtalk.common;

/**
 * 애플리케이션 사이트의 정보를 가지고 있는 정적 유틸 클래스입니다.
 */
public final class Site {

    private Site() {
        throw new AssertionError("Util class cannot be instantiated.");
    }
    private static final String DOMAIN_URL = "https://www.slam-talk.site";
    private static final String MATE_MATCHING_URL = DOMAIN_URL + "/matching/team-details/%d";

    public static String mateMatching(Long mateMatchingId) {
        return String.format(MATE_MATCHING_URL, mateMatchingId);
    }

}