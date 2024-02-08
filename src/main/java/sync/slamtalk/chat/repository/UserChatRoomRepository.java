package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sync.slamtalk.chat.entity.UserChatRoom;

import java.util.List;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {

    // UserChatRoom 테이블에서 chatRoomId 로 데이터 가져오기
    List<UserChatRoom> findByChat_Id(Long chatRoomId);


    // 특정 User_Id에 해당하는 모든 UserChatRoom 엔터티를 찾는 메서드
    List<UserChatRoom> findByUser_Id(Long userId);


    // 특정 userId 와 특정 roomId 로 userChatRoom 엔터티 가져오기
    @Query("select m from UserChatRoom m where m.user.id=:userId and m.chat.id=:roomId")
    Optional<UserChatRoom>findByUserChatroom(Long userId, Long roomId);
}
