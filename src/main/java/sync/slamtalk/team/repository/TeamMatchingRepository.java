package sync.slamtalk.team.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TeamMatchingRepository extends JpaRepository<TeamMatching, Long> {

    /* 레벨 시스템 : 모집상태가 완료된 사용자의 개수를 반환하는 메서드 */
    @Query("select count(*) "
            + "from TeamMatching t "
            + "join TeamApplicant a on t.teamMatchingId = a.teamMatching.teamMatchingId "
            + "where a.applicantId = :userId "
            + "and t.recruitmentStatus = sync.slamtalk.mate.entity.RecruitmentStatusType.COMPLETED "
            + "and a.applyStatus = sync.slamtalk.mate.entity.ApplyStatusType.ACCEPTED "
            + "and t.isDeleted = false ")
    long findTeamMatchingByCompleteParticipationCount(@Param("userId") Long userId);

    @Query("select count(*) from TeamMatching t where t.writer = :writer and t.recruitmentStatus = 'COMPLETED' and t.isDeleted = false ")
    long countTeamMatchingByWriter(@Param("writer") User writer);

    /* 내가 작성한 팀매칭 리스트를 조회*/
    @EntityGraph(attributePaths = {"teamApplicants", "writer"})
    List<TeamMatching> findAllByWriterAndIsDeletedFalse(@Param("writer") User writer);

    /* 내가 지원한 팀매칭 리스트를 조회*/
    @Query("select distinct t "
            + "from TeamMatching t "
            + "join fetch t.teamApplicants a "
            + "where a.applicantId =:applicantId")
    List<TeamMatching> findAllByApplicationId(@Param("applicantId") Long applicantId);
}
