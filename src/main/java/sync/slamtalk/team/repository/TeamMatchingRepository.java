package sync.slamtalk.team.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.entity.RecruitmentStatusType;
import sync.slamtalk.team.entity.TeamMatching;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeamMatchingRepository extends JpaRepository<TeamMatching, Long> {

    //    @Query("select t from TeamMatching t left join fetch t.teamApplicants where t.recruitmentStatus != :recruitmentStatus and t.createdAt < :cursor order by t.createdAt desc")
//    List<TeamMatching> findAllOrderByCreatedAtDesc(@Param("recruitmentStatus") RecruitmentStatusType recruitmentStatus, @Param("cursor")LocalDateTime cursor, @Param("request")PageRequest request);
    @Query("SELECT tm.teamMatchingId FROM TeamMatching tm WHERE tm.recruitmentStatus != :recruitmentStatus AND tm.createdAt < :cursor ORDER BY tm.createdAt DESC")
    Page<Long> findTeamMatchingIds(@Param("recruitmentStatus") RecruitmentStatusType recruitmentStatus, @Param("cursor") LocalDateTime cursor, PageRequest request);

    @Query("SELECT tm FROM TeamMatching tm LEFT JOIN FETCH tm.teamApplicants WHERE tm.teamMatchingId IN :ids")
    List<TeamMatching> findTeamMatchingsWithApplicants(@Param("ids") List<Long> ids);

    @EntityGraph(value = "TeamMatching.forEagerApplicants", type= EntityGraph.EntityGraphType.LOAD)
    @Query("select t from TeamMatching t where t.createdAt < :cursor order by t.createdAt desc")
    List<TeamMatching> findAllByCreatedAtBefore(@Param("cursor")LocalDateTime cursor, PageRequest request);
}
