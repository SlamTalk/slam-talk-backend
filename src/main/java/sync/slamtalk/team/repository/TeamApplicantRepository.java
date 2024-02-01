package sync.slamtalk.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.team.entity.TeamApplicant;

public interface TeamApplicantRepository extends JpaRepository<TeamApplicant, Long> {

}
