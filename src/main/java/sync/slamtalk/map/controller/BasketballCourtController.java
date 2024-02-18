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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.ApiResponse;
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


    //전체 농구장 간략 정보
    @GetMapping("/courts")
    @Operation(
            summary = "전체 농구장 간략 정보", // 기능 제목 입니다
            description = "이 기능은 마커에 띄울 전체 농구장의 간략 정보 응답을 보내는 기능입니다.", // 기능 설명
            tags = {"지도", "게스트"}
    )
    public ApiResponse<List<BasketballCourtSummaryDto>> getAllCourtSummaryInfo() {
        List<BasketballCourtSummaryDto> courtDetails = basketballCourtService.getAllCourtSummaryInfo();
        return (ApiResponse.ok(courtDetails, "농구장 목록을 성공적으로 가져왔습니다."));
    }


    //특정 농구장 전체 정보
    @GetMapping("/courts/{courtId}")
    @Operation(
            summary = "마커 클릭 농구장 전체 정보", // 기능 제목 입니다
            description = "이 기능은 클릭한 마커에 해당하는 농구장의 전체 정보 응답을 보내는 기능입니다.", // 기능 설명
            tags = {"지도", "게스트"}
    )
    public ApiResponse<BasketballCourtResponseDTO> getCourtFullInfoById(@PathVariable Long courtId) {

        BasketballCourtResponseDTO basketballCourtResponseDTO = basketballCourtService.getCourtFullInfoById(courtId);
        return ApiResponse.ok(basketballCourtResponseDTO, "농구장 상세 정보를 성공적으로 가져왔습니다.");

    }

    // 농구장 제보
    @PostMapping("/report")
    @Operation(
            summary = "제보 받은 농구장 정보 저장", // 기능 제목 입니다
            description = "이 기능은 이용자가 제보한 농구장 정보를 저장하는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtResponseDTO> reportBasketballCourt(
            @RequestPart(name = "data", required = false) BasketballCourtRequestDTO basketballCourtRequestDTO,
            @RequestPart(name = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userId) {

        BasketballCourt court = reportBasketballCourtService.reportCourt(basketballCourtRequestDTO, file, userId);
        return ApiResponse.ok(basketballCourtMapper.toFullDto(court), "제보 받은 농구장 정보를 저장하였습니다.");
    }

    // 제보한 농구장 조회
    @GetMapping("/report/courts")
    @Operation(
            summary = "이용자가 제보한 농구장 조회", // 기능 제목 입니다
            description = "이 기능은 이용자가 제보한 농구장 정보를 조회하는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<List<BasketballCourtReportSummaryDTO>> getUserReportedCourtSummaryInfo(@AuthenticationPrincipal Long userId) {

        List<BasketballCourtReportSummaryDTO> basketballCourtSummaryDtoList = basketballCourtService.getUserReportedCourtSummaryInfo(
                userId);
        return ApiResponse.ok(basketballCourtSummaryDtoList, "검토중인 농구장 목록을 성공적으로 가져왔습니다.");
    }

    // 제보한 특정 농구장 상세 조회
    @GetMapping("/report/courts/{courtId}")
    @Operation(
            summary = "이용자가 제보한 특정 농구장 상세 조회", // 기능 제목 입니다
            description = "이 기능은 이용자가 제보한 특정 농구장 정보를 상세 조회하는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtReportResponseDTO> getUserReportedCourtInfo(@PathVariable Long courtId,
                                                                                  @AuthenticationPrincipal Long userId) {

        BasketballCourtReportResponseDTO basketballCourt = basketballCourtService.getUserReportedCourtFullInfo(
                courtId, userId);
        return ApiResponse.ok(basketballCourt, "검토중인 농구장 상세 정보를 성공적으로 가져왔습니다.");
    }

    // 제보한 농구장 정보 수정
    @PatchMapping("/report/edit/{courtId}")
    @Operation(
            summary = "이용자가 제보한 농구장 정보 수정", // 기능 제목 입니다
            description = "이 기능은 이용자가 제보한 농구장 정보를 수정하는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtResponseDTO> editReportBasketballCourt(
            @PathVariable Long courtId,
            @RequestPart(name = "data", required = false) BasketballCourtRequestDTO basketballCourtRequestDTO,
            @RequestPart(name = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal Long userId) {

        System.out.println("controller");

        BasketballCourt court = reportBasketballCourtService.editReportCourt(courtId, basketballCourtRequestDTO, file,
                userId);
        return ApiResponse.ok(basketballCourtMapper.toFullDto(court), "제보 받은 농구장 정보를 수정하였습니다.");
    }
}
