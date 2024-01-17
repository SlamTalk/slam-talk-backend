package sync.slamtalk.mate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.domain.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

}
