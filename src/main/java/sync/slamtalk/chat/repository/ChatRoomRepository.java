package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.chat.entity.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // BasketballId로 ChatRoom 검색
    Optional<ChatRoom> findByBasketBallId(Long basketballId);

    // TogetherId로 ChatRoom 검색
    Optional<ChatRoom> findByTogetherId(Long togetherId);

    // TeamMatchingId로 ChatRoom 검색
    Optional<ChatRoom> findByTeamMatchingId(Long teamMatchingId);

}
