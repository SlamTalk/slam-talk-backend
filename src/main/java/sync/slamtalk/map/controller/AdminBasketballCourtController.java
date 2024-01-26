package sync.slamtalk.map.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.map.dto.BasketballCourtDto;
import sync.slamtalk.map.service.AdminBasketballCourtService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminBasketballCourtController {

    private final AdminBasketballCourtService adminBasketballCourtService;

    @GetMapping("/stand")
    public ApiResponse<List<BasketballCourtDto>> getAllCourtsWithStatusStand() {
        List<BasketballCourtDto> basketballCourtStandDto = adminBasketballCourtService.getAllCourtsWithStatusStand();
        return (ApiResponse.ok(basketballCourtStandDto, "대기중인 농구장 목록을 성공적으로 가져왔습니다."));
    }
}
