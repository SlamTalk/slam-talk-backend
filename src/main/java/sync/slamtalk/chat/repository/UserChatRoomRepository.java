package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.UserChatRoom;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

    // UserChatRoom 테이블에서 chatRoomId 로 데이터 가져오기
    Optional<UserChatRoom> findByChat_Id(Long chatRoomId);


    // 특정 User_Id에 해당하는 모든 UserChatRoom 엔터티를 찾는 메서드
    List<UserChatRoom> findByUser_Id(Long userId);
}
