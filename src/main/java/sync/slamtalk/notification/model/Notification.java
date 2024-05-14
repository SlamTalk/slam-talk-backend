package sync.slamtalk.notification.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.chat.entity.UserChatRoom;
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

	// 유저의 채팅방
	@ManyToOne
	@JoinColumn(name = "user_chat_room_id")
	private UserChatRoom userChatRoom;


	/**
	 * userChatRoom 엔터티와 연관 관계 편의 메서드
	 * @param newUserChatRoom
	 */
	public void setUserChatRoom(UserChatRoom newUserChatRoom) {
		if (this.userChatRoom != null) {
			this.userChatRoom.getNotifications().remove(this);
		}
		this.userChatRoom = newUserChatRoom;
		if (newUserChatRoom != null && !newUserChatRoom.getNotifications().contains(this)) {
			newUserChatRoom.getNotifications().add(this);
		}
	}

	/**
	 * 읽음 처리
	 */
	public void read() {
		this.isRead = true;
	}


	@Builder
	public Notification(Long userId,NotificationContent notificationContent, UserChatRoom userChatRoom){
		this.userId = userId;
		this.content = notificationContent;
		this.userChatRoom = userChatRoom;
	}
}
