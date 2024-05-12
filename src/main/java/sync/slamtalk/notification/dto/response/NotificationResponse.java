package sync.slamtalk.notification.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.notification.model.Notification;
import sync.slamtalk.notification.model.NotificationType;

import java.time.LocalDateTime;

/**
 * 알림 조회 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationResponse {

	private Long notificationId;
	private String message;
	private boolean isRead; // 읽음 여부
	private String uri; // 알림 클릭 시 이동할 URI
	private Long userId; // 유저
	private NotificationType notificationType; // 알림 타입
	private LocalDateTime createdAt;

	public static NotificationResponse of(Notification notification) {
		NotificationResponse resp = new NotificationResponse();
		resp.notificationId = notification.getId();
		resp.message = notification.getContent().getMessage();
		resp.isRead = notification.isRead();
		resp.uri = notification.getContent().getUri();
		resp.userId = notification.getContent().getUserId();
		resp.notificationType = notification.getContent().getNotificationType();
		resp.createdAt = notification.getCreatedAt();
		return resp;
	}
}
