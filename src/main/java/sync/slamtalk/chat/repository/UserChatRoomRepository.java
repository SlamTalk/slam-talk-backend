package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.chat.entity.UserChatRoom;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
}
