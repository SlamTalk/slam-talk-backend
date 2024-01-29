package sync.slamtalk.map.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sync.slamtalk.map.dto.BasketballCourtDto;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.entity.BasketballCourt;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.repository.BasketballCourtRepository;

@Service
@RequiredArgsConstructor
public class AdminBasketballCourtService {

    private final BasketballCourtRepository basketballCourtRepository;
    private final BasketballCourtMapper basketballCourtMapper;

    // 제보 받은 농구장 정보들 중 대기 상태의 정보만 조회
    public List<BasketballCourtDto> getAllCourtsWithStatusStand() {
        List<BasketballCourt> courts = basketballCourtRepository.findByAdminStatus(AdminStatus.STAND); // 대기 상태인 정보만 조회
        return courts.stream()
                .map(basketballCourtMapper::toFullDto)
                .collect(Collectors.toList());
    }
}
