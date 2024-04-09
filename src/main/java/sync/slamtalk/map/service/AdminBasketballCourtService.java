package sync.slamtalk.map.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sync.slamtalk.map.dto.BasketballCourtResponseDTO;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.entity.BasketballCourt;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.repository.BasketballCourtRepository;

/**
 * 관리자가 제보된 농구장 정보를 관리하도록 하는 클래스
 */
@Service
@RequiredArgsConstructor
public class AdminBasketballCourtService {

    private final BasketballCourtRepository basketballCourtRepository;
    private final BasketballCourtMapper basketballCourtMapper;

    /**
     *  제보 받은 농구장 정보들 중 대기 상태의 정보만 반환한다.
     *  <p>
     *      관리자가 제보받은 농구장 정보를 관리할 수 있도록, 대기 상태의 농구장 정보만 반환한다.
     *  </p>
     *
     * @return {@link BasketballCourtResponseDTO} 대기 상태의 농구장 정보 리스트
     */
    public List<BasketballCourtResponseDTO> getAllCourtsWithStatusStand() {
        List<BasketballCourt> courts = basketballCourtRepository.findByAdminStatus(AdminStatus.STAND);
        return courts.stream()
                .map(basketballCourtMapper::toFullDto)
                .toList();
    }
}
