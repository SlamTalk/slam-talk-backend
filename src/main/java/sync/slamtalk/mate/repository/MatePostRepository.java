package sync.slamtalk.mate.repository;

import io.lettuce.core.dynamic.annotation.Param;
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

    /* 레벨 시스템 : 모집상태가 완료된 사용자의 개수를 반환하는 메서드 */
    @Query("select count(*) " +
            "from MatePost m " +
            "join Participant p on m.matePostId = p.matePost.matePostId " +
            "where p.participantId = :userId " +
            "and m.recruitmentStatus = 'COMPLETED' " +
            "and p.applyStatus = 'ACCEPTED'")
    Long findMateCompleteParticipationCount(@Param("userId") Long userId);
}
