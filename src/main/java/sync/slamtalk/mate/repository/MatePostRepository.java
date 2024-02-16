package sync.slamtalk.mate.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.user.entity.User;

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
    long findMateCompleteParticipationCount(@Param("userId") Long userId);

    @Query("select count(*) from MatePost m where m.writer = :writer and m.recruitmentStatus = 'COMPLETED'")
    long countMatePostByWriter(@Param("writer") User writer);

    /* 내가 쓴 글 조회*/
    @EntityGraph(attributePaths = {"participants", "writer"})
    List<MatePost> findAllByWriter(@Param("writer") User writer);

    /* 내가 참여한 글 조회*/
    @Query("select distinct m " +
            "from MatePost m " +
            "join fetch m.participants p " +
            "where p.participantId =:participantId")
    List<MatePost> findAllByApplicationId(@Param("participantId") Long participantId);

}
