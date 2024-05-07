package sync.slamtalk.map.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sync.slamtalk.map.entity.RegistrationStatus;
import sync.slamtalk.map.entity.BasketballCourt;

public interface BasketballCourtRepository extends JpaRepository<BasketballCourt, Long> {

    /**
     * RegistrationStatus 따른 농구장 목록을 조회합니다.
     * @param registrationStatus 농구장 상태
     * @return 상태에 따른 농구장 목록
     */
    List<BasketballCourt> findByRegistrationStatus(RegistrationStatus registrationStatus);

    /**
     * RegistrationStatus 따른 특정 농구장을 조회합니다.
     * @param courtId 농구장 ID
     * @param registrationStatus 농구장 상태
     * @return 상태와 농구장 ID에 따른 농구장
     */
    Optional<BasketballCourt> findByIdAndRegistrationStatus(Long courtId, RegistrationStatus registrationStatus);

    // 내가 제보한 농구장이 승인된 개수 구하기
    Long countBasketballCourtByRegistrationStatusEqualsAndInformerId(
            RegistrationStatus registrationStatus,
            Long informerid
    );

    /**
     * 제보한 농구장 중 RegistrationStatus 따른 농구장을 조회합니다.
     * @param informerId 제보한 사용자 ID
     * @param registrationStatus 농구장 상태
     * @return 상태에 따른 농구장 목록
     */
    List<BasketballCourt> findByInformerIdAndRegistrationStatus(Long informerId, RegistrationStatus registrationStatus);

    /**
     * 이용자가 제보한 농구장 목록 중 상태에 따른 특정 농구장을 조회합니다.
     * @param courtId 농구장 ID®
     * @param informerId 제보한 사용자 ID
     * @param registrationStatus 농구장 상태
     * @return 상태와 ID에 따른 농구장
     */
    Optional<BasketballCourt> findByIdAndInformerIdAndRegistrationStatus(Long courtId, Long informerId, RegistrationStatus registrationStatus);


}