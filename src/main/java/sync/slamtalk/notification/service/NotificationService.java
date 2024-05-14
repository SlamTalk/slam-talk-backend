package sync.slamtalk.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.ErrorResponseCode;
import sync.slamtalk.notification.NotificationRepository;
import sync.slamtalk.notification.dto.response.NotificationResponse;
import sync.slamtalk.notification.model.Notification;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	/**
	 * 특정 회원의 알림 목록 조회
	 * @param userId 회원 ID
	 * @return 알림 목록
	 */
	@Transactional(readOnly = true)
	public List<NotificationResponse> getNotificationsByMemberId(Long userId) {
		User user = findUser(userId);

		return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
			.map(NotificationResponse::of)
			.toList();
	}

	/**
	 * 알림을 읽음 처리합니다.
	 * @param notificationId 알림 ID
	 * @param userId 로그인한 사용자의 ID
	 */
	public void readNotification(Long notificationId, Long userId) {
		User user = findUser(userId);
		Notification notification = findNotification(notificationId);

		if (!notification.getUserId().equals(user.getId())) {
			throw new BaseException(ErrorResponseCode.UNAUTHORIZED);
		}

		notification.read();
	}

	/**
	 * 사용자의 알림을 일괄적으로 읽음 처리합니다.
	 * @param userId 로그인한 사용자의 ID
	 */
	public void readAllNotifications(Long userId) {
		User user = findUser(userId);

		notificationRepository.readAllNotificationsByUserId(user.getId());
	}

	/**
	 * 사용자의 알림을 개별 삭제합니다.
	 * @param notificationId 알림 ID
	 * @param userId 로그인한 사용자의 ID
	 */
	public void deleteNotification(Long notificationId, Long userId) {
		User user = findUser(userId);
		Notification notification = findNotification(notificationId);

		if (!notification.getUserId().equals(user.getId())) {
			throw new BaseException(ErrorResponseCode.UNAUTHORIZED);
		}

		notificationRepository.delete(notification);
	}

	/**
	 * 사용자의 알림을 일괄 삭제합니다.
	 * @param userId 로그인한 사용자의 ID
	 */
	public void deleteAllNotifications(Long userId) {
		User user = findUser(userId);
		notificationRepository.deleteAllByUserId(user.getId());
	}

	private User findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new BaseException(ErrorResponseCode.ENTITY_NOT_FOUND));
	}

	private Notification findNotification(Long notificationId) {
		return notificationRepository.findById(notificationId)
				.orElseThrow(() -> new BaseException(ErrorResponseCode.ENTITY_NOT_FOUND));
	}

	/**
	 * 유저의 특정 채팅방 알림 지우기
	 * @param userId
	 * @param chatRoomId
	 */
	public void deleteChatNotification(Long userId,Long chatRoomId){
		// 특정 회원의 특정 채팅방 모든 알림 조회
		List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

		// 연관 관계 해제
		for(Notification notification : notifications){
			UserChatRoom userChatRoom = notification.getUserChatRoom();
			if(userChatRoom != null){
				userChatRoom.removeNotification(notification); // 연관 관계 해제
			}
		}

		// 알림 삭제
		notificationRepository.deleteNotificationBy(userId,chatRoomId);
	}
}
