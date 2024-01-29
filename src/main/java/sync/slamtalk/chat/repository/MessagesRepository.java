package sync.slamtalk.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import sync.slamtalk.chat.entity.Messages;

import java.util.List;

@EnableJpaRepositories
public interface MessagesRepository extends JpaRepository<Messages,Long> {

    // 1. 특정 roomId 에 해당하는 모든 Message 가져오기(과거~최근)
     List<Messages> findByChatRoomId(Long chatRoomId);


    // 2. 특정 채팅방의 가장 최근 메시지를 가져오기
    @Query("select m from Messages m where m.chatRoom.id =:chatroom_id order by m.creation_time desc ")
    Page<Messages> findLatestByChatRoomId(@Param("chatroom_id")Long chatRoomId, Pageable pageable);


    // 3. TODO 특정 roomId 에 해당하는 모든 Message 가져오기(최근~과거)
    // 2번 안되면 여기서 가장 위에거만 가져오면 되긴함
    @Query("select m from Messages m where m.chatRoom.id=:chatRoomId order by m.creation_time desc")
    List<Messages> findAllByChatRoom(@Param("chatRoomId")Long chatRoomId);


    // 메세지 아이디가 특정넘버 이상인 메세지들 가져오기
    List<Messages> findByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long messageId);

}
