package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import sync.slamtalk.chat.entity.Messages;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@EnableJpaRepositories
public interface MessagesRepository extends JpaRepository<Messages,Long> {

    // 특정 roomId 에 해당하는 Message 조회
     Optional<Messages> findByChatRoomId(Long chatRoomId);


    // 특정 채팅방의 가장 최근 메시지를 가져오는 쿼리

    // TODO 특정 keyword 를 포함하고 있는 chat 조회

}
