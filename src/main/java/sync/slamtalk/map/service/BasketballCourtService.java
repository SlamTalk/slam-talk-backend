package sync.slamtalk.map.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sync.slamtalk.map.dto.BasketballCourtDto;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.repository.BasketballCourtRepository;

@Service
@RequiredArgsConstructor
public class BasketballCourtService {

    private final BasketballCourtRepository basketballCourtRepository;
    private final BasketballCourtMapper basketballCourtMapper;

    // 저장된 모든 농구장의 간략 정보
    public List<BasketballCourtDto> getAllCourtSummaryInfo() {
        return basketballCourtRepository.findAll().stream()
                .filter(court -> court.getAdminStatus() == AdminStatus.ACCEPT) // ACCEPT 상태 필터링
                .map(basketballCourtMapper::toDto) // dto 변환 mapper 호출
                .collect(Collectors.toList());
    }

    //특정 농구장 전체 정보
    public Optional<BasketballCourtDto> getCourtFullInfoById(Long courtId) {
        return basketballCourtRepository.findById(courtId)
                .filter(court -> court.getAdminStatus() == AdminStatus.ACCEPT) // ACCEPT 상태 필터링
                .map(basketballCourtMapper::toFullDto); // dto 변환 mapper 호출
    }

}
