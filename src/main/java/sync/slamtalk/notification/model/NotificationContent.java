package sync.slamtalk.notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림의 내용을 담고 있는 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationContent {

	@Id
	@Column(name = "notification_content_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String message;

	private String uri;
}
