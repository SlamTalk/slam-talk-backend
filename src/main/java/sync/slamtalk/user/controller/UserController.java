package sync.slamtalk.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.user.dto.UserDetailsInfoResponseDto;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.service.UserService;

/**
 * 이 컨트롤러는 유저의 crud 와 관련된 클래스입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 유저의 상세 정보 조회하는 api
     * @param userId Pathvariable로 유저 id 받아오기
     * @param user 시큐리티를 통해 user 정보를 받아옴
     * @return UserDetailsInfoResponseDto 객체가 반환(개인정보 있는 버전, 없는 버전)
     * */
    @GetMapping("/user/{userId}/info")
    @Operation(
            summary = "유저 상세 정보 조회 api",
            description = "유저가 본인이 아닐경우 email을 제외하고 보내줍니다.",
            tags = {"유저 상세정보 조회"}
    )
    public ApiResponse<UserDetailsInfoResponseDto> userDetailsInfo(
            @PathVariable("userId") Long userId,
            @AuthenticationPrincipal User user
    ) {
        UserDetailsInfoResponseDto userDetailsInfoResponseDto= userService.userDetailsInfo(userId, user);

        return ApiResponse.ok(userDetailsInfoResponseDto);
    }
}
