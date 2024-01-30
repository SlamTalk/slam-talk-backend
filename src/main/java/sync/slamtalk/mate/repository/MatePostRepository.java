package sync.slamtalk.mate.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.entity.MatePost;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatePostRepository extends JpaRepository<MatePost, Long> {

    //메이트찾기 목록 조회를 위한 메소드
    //todo : soft delete 된 글은 조회되지 않도록 한다.
    List<MatePost> findByCreatedAtLessThanAndIsDeletedOrderByCreatedAtDesc(LocalDateTime createdAt, boolean isDeleted, Pageable pageable);
}
