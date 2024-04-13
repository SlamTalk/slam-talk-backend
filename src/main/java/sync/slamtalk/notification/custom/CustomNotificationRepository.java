package sync.slamtalk.notification.custom;

import java.util.Set;

public interface CustomNotificationRepository {

	/**
	 * 알림 등록
	 * @param message 알림 메시지
	 * @param uri 알림 링크
	 * @param memberIds 알림 대상 회원 목록
	 */
	void insertNotifications(String message, String uri, Set<Long> memberIds);
}
