package sync.slamtalk.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.map.dto.BasketballCourtFullResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtReportResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtReportSummaryDTO;
import sync.slamtalk.map.dto.BasketballCourtRequestDTO;
import sync.slamtalk.map.dto.BasketballCourtResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtSummaryDto;
import sync.slamtalk.map.entity.BasketballCourt;
import sync.slamtalk.map.mapper.BasketballCourtMapper;
import sync.slamtalk.map.service.BasketballCourtService;
import sync.slamtalk.map.service.ReportBasketballCourtService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/map")
public class BasketballCourtController {
    private final BasketballCourtService basketballCourtService;
    private final ReportBasketballCourtService reportBasketballCourtService;
    private final BasketballCourtMapper basketballCourtMapper;

    /**
     * 승인된 모든 농구장의 요약 정보를 조회합니다.
     * @return 승인된 모든 농구장 리스트
     */
    @GetMapping("/courts")
    @Operation(
            summary = "전체 농구장 간략 정보",
            description = "이 기능은 마커에 띄울 전체 농구장의 간략 정보 응답을 보내는 기능입니다.",
            tags = {"지도", "게스트"}
    )
    public ApiResponse<List<BasketballCourtSummaryDto>> getListApprovedBasketballCourtSummaries() {
        List<BasketballCourtSummaryDto> courtDetails = basketballCourtService.getAllApprovedBasketballCourtSummaries();
        return (ApiResponse.ok(courtDetails, "농구장 목록을 성공적으로 가져왔습니다."));
    }

    /**
     * 특정 농구장의 상세 정보를 조회합니다.
     * @param courtId 농구장 ID
     * @return 농구장 ID에 해당하는 농구장의 상세 정보
     */
    @GetMapping("/courts/{courtId}")
    @Operation(
            summary = "마커 클릭 농구장 전체 정보",
            description = "이 기능은 클릭한 마커에 해당하는 농구장의 전체 정보 응답을 보내는 기능입니다.",
            tags = {"지도", "게스트"}
    )
    public ApiResponse<BasketballCourtFullResponseDTO> getDetailedBasketballCourtInfo(@PathVariable Long courtId) {

        BasketballCourtFullResponseDTO basketballCourtResponseDTO = basketballCourtService.getApprovedBasketballCourtDetailsById(courtId);
        return ApiResponse.ok(basketballCourtResponseDTO, "농구장 상세 정보를 성공적으로 가져왔습니다.");

    }

    /**
     * 사용자가 제보한 농구장 정보를 저장합니다.
     * @param basketballCourtRequestDTO 사용자가 제보한 농구장 정보
     * @param file 농구장 사진
     * @param userId 제보한 사용자 ID
     * @return 저장된 농구장 DTO
     */
    @PostMapping("/report")
    @Operation(
            summary = "제보 받은 농구장 정보 저장",
            description = "이 기능은 이용자가 제보한 농구장 정보를 저장하는 기능입니다.",
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtResponseDTO> saveUserReportedBasketballCourt(
            @RequestPart(name = "data", required = false) BasketballCourtRequestDTO basketballCourtRequestDTO,
            @RequestPart(name = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userId) {

        BasketballCourt court = reportBasketballCourtService.createBasketballCourtReport(basketballCourtRequestDTO, file, userId);
        return ApiResponse.ok(basketballCourtMapper.toFullDto(court), "제보 받은 농구장 정보를 저장하였습니다.");
    }

    /**
     * 사용자가 제보한 농구장 중 대기 상태인 농구장을 조회합니다.
     * @param userId 사용자 ID
     * @return 사용자가 제보한 농구장 중 대기 상태인 농구장 목록
     */
    @GetMapping("/report/courts")
    @Operation(
            summary = "이용자가 제보한 농구장 중 대기 상태인 농구장 조회",
            description = "이 기능은 이용자가 제보한 농구장 중 대기 상태인 농구장 목록을 조회하는 기능입니다.",
            tags = {"지도"}
    )
    public ApiResponse<List<BasketballCourtReportSummaryDTO>> getListUserReportedStandingBasketballCourts(@AuthenticationPrincipal Long userId) {

        List<BasketballCourtReportSummaryDTO> basketballCourtSummaryDtoList = basketballCourtService.getUserReportedBasketballCourtSummariesByUserId(
                userId);
        return ApiResponse.ok(basketballCourtSummaryDtoList, "검토중인 농구장 목록을 성공적으로 가져왔습니다.");
    }

    /**
     * 사용자가 제보한 농구장 중 대기 상태인 특정 농구장의 상세 정보를 조회합니다.
     * @param courtId 농구장 ID
     * @param userId 사용자 ID
     * @return 제보한 농구장 중 대기 상태인 특정 농구장의 상세 정보
     */
    @GetMapping("/report/courts/{courtId}")
    @Operation(
            summary = "이용자가 제보한 농구장 중 대기 상태인 특정 농구장의 상세 정보 조회",
            description = "이 기능은 이용자가 제보한 농구장 중 대기 상태인 특정 농구장의 상세 정보를 조회하는 기능입니다.",
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtReportResponseDTO> getStandingBasketballCourtDetail(@PathVariable Long courtId,
                                                                                          @AuthenticationPrincipal Long userId) {

        BasketballCourtReportResponseDTO basketballCourt = basketballCourtService.getUserReportedBasketballCourtDetailsByIdAndUserId(
                courtId, userId);
        return ApiResponse.ok(basketballCourt, "검토중인 농구장 상세 정보를 성공적으로 가져왔습니다.");
    }

    /**
     * 사용자가 제보한 농구장 정보를 수정합니다.
     * @param courtId 농구장 ID
     * @param basketballCourtRequestDTO 수정할 농구장 정보
     * @param file 농구장 사진
     * @param userId 제보한 사용자 ID
     * @return 수정된 농구장 DTO
     */
    @PatchMapping("/report/edit/{courtId}")
    @Operation(
            summary = "이용자가 제보한 농구장 정보 수정",
            description = "이 기능은 이용자가 제보한 농구장 정보를 수정하는 기능입니다.",
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtResponseDTO> updateUserReportedBasketballCourt(
            @PathVariable Long courtId,
            @RequestPart(name = "data", required = false) BasketballCourtRequestDTO basketballCourtRequestDTO,
            @RequestPart(name = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userId) {

        BasketballCourt court = reportBasketballCourtService.updateSubmittedBasketballCourtReport(courtId, basketballCourtRequestDTO, file,
                userId);
        return ApiResponse.ok(basketballCourtMapper.toFullDto(court), "제보 받은 농구장 정보를 수정하였습니다.");
    }
}
