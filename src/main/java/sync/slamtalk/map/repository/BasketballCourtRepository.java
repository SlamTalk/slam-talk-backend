package sync.slamtalk.map.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.entity.BasketballCourt;

@Repository
public interface BasketballCourtRepository extends JpaRepository<BasketballCourt, Long> {

    // AdminStatus에 따른 농구장 목록 조회
    List<BasketballCourt> findByAdminStatus(AdminStatus adminStatus);

    // AdminStatus에 따른 특정 농구장 조회
    Optional<BasketballCourt> findByIdAndAdminStatus(Long courtId, AdminStatus adminStatus);

    // 내가 제보한 농구장이 승인된 개수 구하기
    Long countBasketballCourtByAdminStatusEqualsAndInformerId(
            AdminStatus adminStatus,
            Long informerid
    );

    // 제보한 농구장 중 검토중인 농구장 조회
    List<BasketballCourt> findByInformerIdAndAdminStatus(Long informerId, AdminStatus adminStatus);

    // 이용자가 제보한 검토중인 특정 농구장 검색
    Optional<BasketballCourt> findByIdAndInformerIdAndAdminStatus(Long courtId, Long informerId, AdminStatus adminStatus);


}