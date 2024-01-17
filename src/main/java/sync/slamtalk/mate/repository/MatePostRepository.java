package sync.slamtalk.mate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatePostRepository extends JpaRepository<MatePostRepository, Long> {

}
