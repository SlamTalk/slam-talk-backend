package sync.slamtalk.mate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.domain.MatePost;

@Repository
public interface MatePostRepository extends JpaRepository<MatePost, Long> {

}
