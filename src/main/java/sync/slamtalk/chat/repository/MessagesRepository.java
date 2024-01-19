package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.chat.entity.Messages;

public interface MessagesRepository extends JpaRepository<Messages,Long> {
}
