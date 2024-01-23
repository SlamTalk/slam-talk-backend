package sync.slamtalk.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.map.dto.BasketballCourtDto;
import sync.slamtalk.map.entity.BasketballCourt;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.repository.BasketballCourtRepository;

@Service
@RequiredArgsConstructor
public class ReportBasketballCourtService {
    private final BasketballCourtRepository basketballCourtRepository;
    private final BasketballCourtMapper basketballCourtMapper;

    @Transactional
    public BasketballCourt reportCourt(BasketballCourtDto basketballCourtDto) {
        BasketballCourt court = basketballCourtMapper.toEntity(basketballCourtDto);
        return basketballCourtRepository.save(court);
    }
}
