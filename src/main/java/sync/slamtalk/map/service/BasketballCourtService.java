package sync.slamtalk.map.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.map.dto.BasketballCourtErrorResponse;
import sync.slamtalk.map.dto.BasketballCourtFullResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtReportResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtReportSummaryDTO;
import sync.slamtalk.map.dto.BasketballCourtSummaryDto;
import sync.slamtalk.map.entity.RegistrationStatus;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.repository.BasketballCourtRepository;

/**
 * 농구장 정보를 조회하는 클래스
 */
@Service
@RequiredArgsConstructor
public class BasketballCourtService {

    private final BasketballCourtRepository basketballCourtRepository;
    private final BasketballCourtMapper basketballCourtMapper;

    /**
     * 지도에 표시하기 위해 승인된 모든 농구장의 간략 정보를 반환합니다.
     *
     * <p>
     *     간략 정보는 다음 정보를 포함합니다:
     * </p>
     * <ul>
     *     <li>농구장 id</li>
     *     <li>농구장 이름</li>
     *     <li>농구장 주소</li>
     *     <li>농구장 위도, 경도</li>
     * </ul>
     *
     * @return {@link BasketballCourtSummaryDto}  승인된 농구장의 간략 정보를 담은 리스트
     */
    public List<BasketballCourtSummaryDto> getAllApprovedBasketballCourtSummaries() {
        return basketballCourtRepository.findByRegistrationStatus(RegistrationStatus.ACCEPT).stream() //수락 상태의 정보만 조회
                .map(basketballCourtMapper::toDto) // dto 변환
                .toList();
    }

    /**
     * ID에 해당되는 농구장의 전체 정보를 반환합니다.
     *
     * @param courtId 조회할 농구장 ID
     * @return {@link BasketballCourtFullResponseDTO} 해당 ID의 농구장 전체 정보
     * @throws BaseException ID에 해당하는 농구장이 존재하지 않을 때, 예외 발생
     */
    public BasketballCourtFullResponseDTO getApprovedBasketballCourtDetailsById(Long courtId) {
        return basketballCourtRepository.findByIdAndRegistrationStatus(courtId, RegistrationStatus.ACCEPT)
                .map(basketballCourtMapper::toFullChatDto)
                .orElseThrow(()->new BaseException(BasketballCourtErrorResponse.MAP_FAIL));
    }

    /**
     * 지도에 표시하기 위해 UserID에 해당하는 사용자에 의해 제보된, 대기 상태의 농구장의 간략 정보를 반환합니다.
     *
     * @param userId 제보자의 userId
     * @return {@link BasketballCourtReportSummaryDTO} 검토 중인 농구장의 간략 정보 리스트
     */
    public List<BasketballCourtReportSummaryDTO> getUserReportedBasketballCourtSummariesByUserId(Long userId) {
        return basketballCourtRepository.findByInformerIdAndRegistrationStatus(userId, RegistrationStatus.STAND).stream()
                .map(basketballCourtMapper::toStatusDto)
                .toList();
    }

    /**
     * UserID에 해당하는 사용자에 의해 제보된, 대기 상태의 농구장의 전체 정보를 반환합니다.
     *
     * @param courtId 조회할 농구장 ID
     * @param userId 제보자의 userID
     * @return {@link BasketballCourtReportResponseDTO} 해당 농구장 ID의 전체 정보
     * @throws BaseException 농구장 ID에 해당하는 농구장이 존재하지 않을 때, 예외 발생
     */
    public BasketballCourtReportResponseDTO getUserReportedBasketballCourtDetailsByIdAndUserId(Long courtId, Long userId) {
        return basketballCourtRepository.findByIdAndInformerIdAndRegistrationStatus(courtId, userId, RegistrationStatus.STAND)
                .map(basketballCourtMapper::toFullStatusDto)
                .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.MAP_FAIL));
    }

}
