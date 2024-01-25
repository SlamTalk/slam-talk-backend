package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.chat.entity.UserMock;

public interface UserMockRepository extends JpaRepository<UserMock,Long> {


}
