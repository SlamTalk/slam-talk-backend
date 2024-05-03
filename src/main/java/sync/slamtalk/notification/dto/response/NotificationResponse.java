package sync.slamtalk.notification.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.notification.model.Notification;

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
	private LocalDateTime createdAt;

	public static NotificationResponse of(Notification notification) {
		NotificationResponse resp = new NotificationResponse();
		resp.notificationId = notification.getId();
		resp.message = notification.getContent().getMessage();
		resp.isRead = notification.isRead();
		resp.uri = notification.getContent().getUri();
		resp.createdAt = notification.getCreatedAt();
		return resp;
	}
}
