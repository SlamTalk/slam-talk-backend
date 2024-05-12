package sync.slamtalk.notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;

/**
 * 알림의 내용을 담고 있는 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationContent extends BaseEntity {

	@Id
	@Column(name = "notification_content_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 메세지
	private String message;

	// 링크
	private String uri;

	// userId
	private Long userId;

	// 알림 타입
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
}
