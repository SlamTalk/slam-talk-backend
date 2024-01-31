package sync.slamtalk.mate.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.entity.MatePost;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatePostRepository extends JpaRepository<MatePost, Long> {

    //메이트찾기 목록 조회를 위한 메소드
    List<MatePost> findByCreatedAtLessThanAndIsDeletedNotOrderByCreatedAtDesc(LocalDateTime createdAt, boolean isDeleted, Pageable pageable);
}
