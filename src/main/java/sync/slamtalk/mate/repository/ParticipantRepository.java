package sync.slamtalk.mate.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.mate.entity.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

}
