package sync.slamtalk.map.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.entity.BasketballCourt;

public interface BasketballCourtRepository extends JpaRepository<BasketballCourt, Long> {

    /**
     * AdminStatus에 따른 농구장 목록을 조회합니다.
     * @param adminStatus 농구장 상태
     * @return 상태에 따른 농구장 목록
     */
    List<BasketballCourt> findByAdminStatus(AdminStatus adminStatus);

    /**
     * AdminStatus에 따른 특정 농구장을 조회합니다.
     * @param courtId 농구장 ID
     * @param adminStatus 농구장 상태
     * @return 상태와 농구장 ID에 따른 농구장
     */
    Optional<BasketballCourt> findByIdAndAdminStatus(Long courtId, AdminStatus adminStatus);

    // 내가 제보한 농구장이 승인된 개수 구하기
    Long countBasketballCourtByAdminStatusEqualsAndInformerId(
            AdminStatus adminStatus,
            Long informerid
    );

    /**
     * 제보한 농구장 중 AdminStatus에 따른 농구장을 조회합니다.
     * @param informerId 제보한 사용자 ID
     * @param adminStatus 농구장 상태
     * @return 상태에 따른 농구장 목록
     */
    List<BasketballCourt> findByInformerIdAndAdminStatus(Long informerId, AdminStatus adminStatus);

    /**
     * 이용자가 제보한 농구장 목록 중 상태에 따른 특정 농구장을 조회합니다.
     * @param courtId 농구장 ID
     * @param informerId 제보한 사용자 ID
     * @param adminStatus 농구장 상태
     * @return 상태와 ID에 따른 농구장
     */
    Optional<BasketballCourt> findByIdAndInformerIdAndAdminStatus(Long courtId, Long informerId, AdminStatus adminStatus);


}