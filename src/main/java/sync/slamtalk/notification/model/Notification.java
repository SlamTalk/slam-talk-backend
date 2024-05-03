package sync.slamtalk.notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "notification_content_id")
	private NotificationContent content;

	// 읽음 여부
	@Column(columnDefinition = "tinyint")
	private boolean isRead;

	//알림 대상 사용자
	private Long userId;

	public void read() {
		this.isRead = true;
	}
}
