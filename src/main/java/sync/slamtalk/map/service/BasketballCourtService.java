package sync.slamtalk.map.service;

import java.util.List;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.map.dto.BasketballCourtErrorResponse;
import sync.slamtalk.map.dto.BasketballCourtReportResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtReportSummaryDTO;
import sync.slamtalk.map.dto.BasketballCourtResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtSummaryDto;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.repository.BasketballCourtRepository;

@Service
@RequiredArgsConstructor
public class BasketballCourtService {

    private final BasketballCourtRepository basketballCourtRepository;
    private final BasketballCourtMapper basketballCourtMapper;

    // 저장된 모든 농구장의 간략 정보
    public List<BasketballCourtSummaryDto> getAllCourtSummaryInfo() {
        return basketballCourtRepository.findByAdminStatus(AdminStatus.ACCEPT).stream() //수락 상태의 정보만 조회
                .map(basketballCourtMapper::toDto) // dto 변환
                .collect(Collectors.toList());
    }

    //특정 농구장 전체 정보
    public BasketballCourtResponseDTO getCourtFullInfoById(Long courtId) {
        return basketballCourtRepository.findByCourtIdAndAdminStatus(courtId, AdminStatus.ACCEPT)
                .map(basketballCourtMapper::toFullDto)
                .orElseThrow(()->new BaseException(BasketballCourtErrorResponse.MAP_FAIL));
    }

    // 검토중인 농구장 간략 정보
    public List<BasketballCourtReportSummaryDTO> getUserReportedCourtSummaryInfo(Long userId) {
        return basketballCourtRepository.findByInformerIdAndAdminStatus(userId, AdminStatus.STAND).stream()
                .map(basketballCourtMapper::toStatusDto)
                .collect(Collectors.toList());
    }

    // 검토중인 농구장 전체 정보
    public BasketballCourtReportResponseDTO getUserReportedCourtFullInfo(Long courtId, Long userId) {
        return basketballCourtRepository.findByCourtIdAndInformerIdAndAdminStatus(courtId, userId, AdminStatus.STAND)
                .map(basketballCourtMapper::toFullStatusDto)
                .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.MAP_FAIL));
    }

}
