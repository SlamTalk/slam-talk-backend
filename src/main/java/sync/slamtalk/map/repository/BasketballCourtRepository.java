package sync.slamtalk.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.map.entity.BasketballCourt;

@Repository
public interface BasketballCourtRepository extends JpaRepository<BasketballCourt, Long> {


}