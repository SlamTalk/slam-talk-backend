package sync.slamtalk.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.map.dto.BasketballCourtAdminRequestDTO;
import sync.slamtalk.map.dto.BasketballCourtResponseDTO;
import sync.slamtalk.map.entity.BasketballCourt;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.service.AdminBasketballCourtService;
import sync.slamtalk.map.service.ReportBasketballCourtService;

/**
 * 관리자가 농구장 정보를 관리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminBasketballCourtController {

    private static final String STANDING_COURT_LIST_SUCCESS_MSG = "대기중인 농구장 목록을 성공적으로 가져왔습니다.";
    private static final String UPDATE_COURT_SUCCESS_MSG = "농구장 정보 업데이트 완료";
    private static final String REJECT_COURT_SUCCESS_MSG = "농구장 거절 완료";

    private final AdminBasketballCourtService adminBasketballCourtService;
    private final ReportBasketballCourtService reportBasketballCourtService;
    private final BasketballCourtMapper basketballCourtMapper;

    /**
     * 관리자가 대기 상태인 농구장 목록을 조회합니다.
     *
     * @return 대기상태의 농구장 목록
     */
    @GetMapping("/stand")
    @Operation(
            summary = "제보 농구장 정보 관리자 확인",
            description = "이 기능은 관리자가 제보된 농구장 목록을 확인하는 기능입니다.",
            tags = {"지도"}
    )
    public ApiResponse<List<BasketballCourtResponseDTO>> getAllStandingBasketballCourts() {
        List<BasketballCourtResponseDTO> basketballCourtStandDto = adminBasketballCourtService.getStandingBasketballCourtReports();
        return ApiResponse.ok(basketballCourtStandDto, STANDING_COURT_LIST_SUCCESS_MSG);
    }

    /**
     * 대기 상태인 특정 농구장의 정보를 업데이트하고 승인 상태로 변경합니다.
     *
     * @param courtId                        농구장 ID
     * @param basketballCourtAdminRequestDTO 업데이트할 농구장 목록을 담은 DTO
     * @return 업데이트된 농구장 DTO
     */
    @PutMapping("/update/{courtId}")
    @Operation(
            summary = "제보 받은 특정 농구장 값 입력 및 수락 업데이트",
            description = "이 기능은 이용자가 제보한 농구장 중 특정 필드 값을 입력하고 수락하는 기능입니다.",
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtResponseDTO> approveAndUpdateBasketballCourt(@PathVariable Long courtId,
                                                                                   @RequestBody BasketballCourtAdminRequestDTO basketballCourtAdminRequestDTO) {
        BasketballCourt updatedCourt = reportBasketballCourtService.approveBasketballCourtInfoUpdate(courtId, basketballCourtAdminRequestDTO);
        return ApiResponse.ok(basketballCourtMapper.toFullDto(updatedCourt), UPDATE_COURT_SUCCESS_MSG);
    }

    @PutMapping("/reject/{courtId}")
    @Operation(
            summary = "제보 받은 특정 농구장 거절 업데이트",
            description = "이 기능은 이용자가 제보한 농구장을 거절하는 기능입니다.",
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtResponseDTO> rejectBasketballCourt(@PathVariable Long courtId) {
        BasketballCourt updatedCourt = reportBasketballCourtService.rejectBasketballCourUpdate(courtId);
        return ApiResponse.ok(basketballCourtMapper.toFullDto(updatedCourt), REJECT_COURT_SUCCESS_MSG);
    }
}
