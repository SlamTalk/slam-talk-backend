package sync.slamtalk.map.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.map.dto.BasketballCourtDto;
import sync.slamtalk.map.service.BasketballCourtService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class BasketballCourtController {
    private final BasketballCourtService basketballCourtService;


    //전체 농구장 간략 정보
    @GetMapping
    public ResponseEntity<ApiResponse<List<BasketballCourtDto>>> getAllCourtSummaryInfo() {
        List<BasketballCourtDto> courtDetails = basketballCourtService.getAllCourtSummaryInfo();
        return ResponseEntity.ok(ApiResponse.ok(courtDetails, "농구장 목록을 성공적으로 가져왔습니다."));
    }


    //특정 농구장 전체 정보
    @GetMapping("/{courtId}")
    public ResponseEntity<ApiResponse<BasketballCourtDto>> getCourtFullInfoById(@PathVariable Long courtId) {
        Optional<BasketballCourtDto> basketballCourtDto = basketballCourtService.getCourtFullInfoById(courtId);
        return basketballCourtDto
                .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto, "농구장 상세 정보를 성공적으로 가져왔습니다.")))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.fail("농구장 정보를 찾을 수 없습니다.")));
    }
}
