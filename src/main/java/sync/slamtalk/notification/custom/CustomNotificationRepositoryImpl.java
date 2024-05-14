package sync.slamtalk.notification.custom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.repository.UserChatRoomRepository;
import sync.slamtalk.notification.model.NotificationType;

import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository {

	private final JdbcTemplate jdbcTemplate;
	private final UserChatRoomRepository userChatRoomRepository;

	private static final String INSERT_NOTIFICATIONS_SQL = "INSERT INTO notification (notification_content_id, user_id, is_read, created_at, updated_at) VALUES (?, ?,false, NOW(), NOW())";
	private static final String INSERT_NOTIFICATION_CONTENT_SQL = "INSERT INTO notification_content (message, uri, user_id, notification_type, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
	private static final String INSERT_CHATNOTIFICATION_SQL = "INSERT INTO notification (notification_content_id, user_id, is_read, user_chat_room_id, created_at, updated_at) VALUES (?, ?, false, ?, NOW(), NOW())";


	/**
	 * 알림 등록
	 * <p>JdbcTemplate의 batchUpdate를 사용하여 여러 개의 알림을 한 번에 등록합니다.
	 *
	 * <p> <a href="https://hyos-dev-log.tistory.com/1">batchUpdate</a> batchUpdate는 DB Driver와
	 * 환경변수 속성에 따라 다르게 동작합니다. MySQL의 경우 rewriteBatchedStatements=true로 설정하면 여러행을 삽입하는
	 * SQL이 생성되며, 해당 속성이 없다면 각각의 SQL을 실행합니다. 이 때문에 성능상의 차이가 발생할 수 있습니다.
	 *
	 * @param message 알림 메시지
	 * @param uri 알림 링크
	 * @param memberIds 알림 대상 회원 목록
	 */
	@Override
	@Transactional
	public void insertNotifications(String message, String uri, Set<Long> memberIds, Long userId, NotificationType notificationType) {
		// 알림 내용 등록
		long notificationContentId = saveNotificationContent(message, uri,userId,notificationType);

		// batchUpdate를 사용하기 위해 파라미터 리스트 생성
		List<Object[]> parameters = new ArrayList<>();
		for (Long memberId : memberIds) {
			parameters.add(new Object[] {notificationContentId, memberId});
		}

		// batchUpdate를 사용하여 여러 개의 알림을 한 번에 등록
		jdbcTemplate.batchUpdate(INSERT_NOTIFICATIONS_SQL, parameters);
	}

	@Transactional
	@Override
	public void insertNotifications(String message,String uri,Set<Long> memberIds,Long chatRoomId, Long userId, NotificationType notificationType){
		// 알림 내용 등록
		long notificationContentId = saveNotificationContent(message, uri,userId,notificationType);

		// batchUpdate를 사용하기 위해 파라미터 리스트 생성
		List<Object[]> parameters = new ArrayList<>();
		for (Long memberId : memberIds) {
			parameters.add(new Object[] {notificationContentId, memberId, chatRoomId});
		}

		// batchUpdate를 사용하여 여러 개의 알림을 한 번에 등록
		jdbcTemplate.batchUpdate(INSERT_CHATNOTIFICATION_SQL, parameters);

	}

	/**
	 * Notification_Content 테이블에 알림 내용을 등록합니다.
	 * @param message 알림 메시지
	 * @param uri 알림 링크
	 * @return 생성된 알림 내용의 ID
	 */
	private long saveNotificationContent(String message, String uri,Long userId,NotificationType notificationType) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			var ps = connection.prepareStatement(INSERT_NOTIFICATION_CONTENT_SQL,
				Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, message);
			ps.setString(2, uri);
			if(userId==null){
				ps.setNull(3,Types.BIGINT);
			}else{
				ps.setLong(3,userId);
			}
			ps.setString(4,notificationType.getKey().toString());
			return ps;
		}, keyHolder);

		return Objects.requireNonNull(keyHolder.getKey()).longValue();
	}
}
