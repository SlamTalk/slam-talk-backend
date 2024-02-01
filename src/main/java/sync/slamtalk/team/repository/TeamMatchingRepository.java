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

    @EntityGraph(value = "TeamMatching.forEagerApplicants", type= EntityGraph.EntityGraphType.LOAD)
    @Query("select t from TeamMatching t where t.createdAt < :cursor order by t.createdAt desc")
    List<TeamMatching> findAllByCreatedAtBefore(@Param("cursor")LocalDateTime cursor, PageRequest request);
}
