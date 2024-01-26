package sync.slamtalk.map.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.entity.BasketballCourt;

@Repository
public interface BasketballCourtRepository extends JpaRepository<BasketballCourt, Long> {

    // AdminStatus에 따른 농구장 목록 조회
    List<BasketballCourt> findByAdminStatus(AdminStatus adminStatus);
}