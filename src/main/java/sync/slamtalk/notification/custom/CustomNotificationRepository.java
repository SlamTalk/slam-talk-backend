package sync.slamtalk.notification.custom;

import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.notification.model.NotificationType;

import java.util.Set;

public interface CustomNotificationRepository {

	/**
	 * 알림 등록
	 * @param message 알림 메시지
	 * @param uri 알림 링크
	 * @param memberIds 알림 대상 회원 목록
	 */
	void insertNotifications(String message, String uri, Set<Long> memberIds, Long userId, NotificationType notificationType);

	/**
	 * 채팅 알림 등록
	 * @param message 알림 메세지
	 * @param uri 알림 링크
	 * @param memberIds 알림 대상 회원 목록
	 * @param chatRoomId 채팅방 아이디
	 */
	void insertNotifications(String message, String uri, Set<Long> memberIds ,Long chatRoomId,Long userId, NotificationType notificationType);
}
