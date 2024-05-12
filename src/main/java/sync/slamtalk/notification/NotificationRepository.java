package sync.slamtalk.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sync.slamtalk.notification.custom.CustomNotificationRepository;
import sync.slamtalk.notification.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotificationRepository {

	/**
	 * 특정 회원의 알림 목록 조회
	 * @param userId 회원 ID
	 * @return 알림 목록
	 */
	@Query("select n from Notification n"
		+ " join fetch n.content"
		+ " where n.userId = :userId"
		+ " order by n.createdAt desc")
	List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

	/**
	 * 특정 회원의 모든 알림을 읽음 처리
	 * @param userId 회원 ID
	 */
	@Modifying
	@Query("update Notification n set n.isRead = true where n.userId = :userId")
	void readAllNotificationsByUserId(Long userId);

	/**
	 * 특정 회원의 알림 목록 삭제
	 * @param userId 회원 ID
	 */
	void deleteAllByUserId(Long userId);

	@Query("delete from Notification where userChatRoom.id = :chatRoomId and userId=:userId")
	@Modifying
	void deleteNotificationBy(Long userId, Long chatRoomId);
}
