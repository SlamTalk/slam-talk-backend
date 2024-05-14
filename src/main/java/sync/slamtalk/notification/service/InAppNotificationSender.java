package sync.slamtalk.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sync.slamtalk.notification.NotificationRepository;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.ChatNotificationRequest;
import sync.slamtalk.notification.dto.request.NotificationRequest;

/**
 * 인앱 알림을 전송하는 역할을 하는 클래스
 *
 * <p>알림의 전송은 여러 모듈들로부터 호출되어야 하는 영역입니다.
 * 이러한 객체의 공개된 메서드가 너무 많다면 변경에 취약해질 수 있습니다.
 * 따라서 NotificationService와는 별개의 클래스를 만들어 분리하였습니다.
 */
@Component
@RequiredArgsConstructor
public class InAppNotificationSender implements NotificationSender {

	private final NotificationRepository notificationRepository;

	/**
	 * Notification을 전송합니다.
	 * @param request NotificationRequest
	 */
	@Override
	public void send(NotificationRequest request) {
		notificationRepository.insertNotifications(request.getMessage(), request.getUri(), request.getTargets(),request.getUserId(),request.getNotificationType());
	}

	/**
	 * Notification을 전송합니다.
	 * @param request ChatNotificationRequest
	 */
	@Override
	public void send(ChatNotificationRequest request) {
		notificationRepository.insertNotifications(request.getMessage(),request.getUri(),request.getTargets(),request.getChatRoomId(),request.getUserId(),request.getNotificationType());
	}
}
