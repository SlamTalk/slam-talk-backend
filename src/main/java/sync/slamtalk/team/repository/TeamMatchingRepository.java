package sync.slamtalk.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.team.entity.TeamMatching;

@Repository
public interface TeamMatchingRepository extends JpaRepository<TeamMatching, Long> {
}
