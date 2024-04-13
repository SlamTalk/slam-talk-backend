package sync.slamtalk.notification.util;

/**
 * 문자열을 자르는 유틸리티 클래스
 * <p>알림에 들어갈 문자열이 너무 긴 경우에 문자열을 자르기 위해 사용합니다.
 */
public class StringSlicer {

	private static final int SLICE_LENGTH = 20;

	private StringSlicer() {
		throw new AssertionError("인스턴스화 할 수 없는 유틸리티 클래스입니다.");
	}

	/**
	 * 문자열을 자릅니다. 파라미터로 길이가 제공되지 않는 경우 SLICE_LENGTH 길이로 자릅니다.
	 * @param str 자를 문자열
	 * @return 자른 문자열
	 */
	public static String slice(String str) {
		return slice(str, SLICE_LENGTH);
	}

	/**
	 * 문자열을 자릅니다.
	 * @param str 자를 문자열
	 * @param length 자를 길이
	 * @return 자른 문자열
	 */
	public static String slice(String str, int length) {
		if (str == null) {
			throw new IllegalArgumentException("문자열이 null입니다.");
		}
		return str.length() > length ? str.substring(0, length) + "..." : str;
	}
}
