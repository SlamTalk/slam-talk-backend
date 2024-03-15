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

public interface MatePostRepository extends JpaRepository<MatePost, Long> {

    /* 레벨 시스템 : 모집상태가 완료된 사용자의 개수를 반환하는 메서드 */
    @Query("select count(*)"
            + " from MatePost m"
            + " join Participant p on m.id = p.matePost.id"
            + " where p.participantId = :userId"
            + " and m.recruitmentStatus = sync.slamtalk.mate.entity.RecruitmentStatusType.COMPLETED"
            + " and p.applyStatus = sync.slamtalk.mate.entity.ApplyStatusType.ACCEPTED"
            + " and m.isDeleted = false ")
    long findMateCompleteParticipationCount(@Param("userId") Long userId);

    @Query("select count(*) from MatePost m"
            + " where m.writer = :writer"
            + " and m.recruitmentStatus = sync.slamtalk.mate.entity.RecruitmentStatusType.COMPLETED"
            + " and m.isDeleted = false ")
    long countMatePostByWriter(@Param("writer") User writer);

    /* 내가 쓴 글 조회*/
    @EntityGraph(attributePaths = {"participants", "writer"})
    List<MatePost> findAllByWriterAndIsDeletedFalse(@Param("writer") User writer);

    /* 내가 참여한 글 조회*/
    @Query("select distinct m"
            + " from MatePost m"
            + " join fetch m.participants p"
            + " where p.participantId =:participantId"
            + " and m.isDeleted = false")
    List<MatePost> findAllByApplicationId(@Param("participantId") Long participantId);

}
