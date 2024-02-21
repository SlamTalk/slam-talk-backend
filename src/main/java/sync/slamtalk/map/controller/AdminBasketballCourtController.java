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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminBasketballCourtController {

    private final AdminBasketballCourtService adminBasketballCourtService;
    private final ReportBasketballCourtService reportBasketballCourtService;
    private final BasketballCourtMapper basketballCourtMapper;

    @GetMapping("/stand")
    @Operation(
            summary = "제보 농구장 정보 관리자 확인", // 기능 제목 입니다
            description = "이 기능은 제보된 농구장 목록을 확인하는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<List<BasketballCourtResponseDTO>> getAllCourtsWithStatusStand() {
        List<BasketballCourtResponseDTO> basketballCourtStandDto = adminBasketballCourtService.getAllCourtsWithStatusStand();
        return (ApiResponse.ok(basketballCourtStandDto, "대기중인 농구장 목록을 성공적으로 가져왔습니다."));
    }

    // 제보 받은 농구장 특정 필드 값 입력 및 수락 업데이트
    @PutMapping("/update/{courtId}")
    @Operation(
            summary = "제보 받은 특정 농구장 값 입력 및 수락 업데이트", // 기능 제목 입니다
            description = "이 기능은 이용자가 제보한 농구장 중 특정 필드 값을 입력하고 수락하는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtResponseDTO> updateBasketballCourt(@PathVariable Long courtId,
                                                                         @RequestBody BasketballCourtAdminRequestDTO basketballCourtAdminRequestDTO) {
        BasketballCourt updatedCourt = reportBasketballCourtService.updateCourt(courtId, basketballCourtAdminRequestDTO);
        return ApiResponse.ok(basketballCourtMapper.toFullDto(updatedCourt), "농구장 정보 업데이트 완료");
    }
}
