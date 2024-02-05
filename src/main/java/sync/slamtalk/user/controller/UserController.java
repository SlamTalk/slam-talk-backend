package sync.slamtalk.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.user.dto.UserDetailsInfoResponseDto;
import sync.slamtalk.user.dto.UserUpdateNicknameRequestDto;
import sync.slamtalk.user.dto.UserUpdatePositionAndSkillRequestDto;
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
     * @param loginUserId 시큐리티를 통해 user 정보를 받아옴
     * @return UserDetailsInfoResponseDto 객체가 반환(개인정보 있는 버전, 없는 버전)
     * */
    @GetMapping("/user/{userId}/info")
    @Operation(
            summary = "(미완성) 유저 상세 정보 조회 api",
            description = "유저가 본인이 아닐경우 email을 제외하고 보내줍니다.",
            tags = {"유저 상세정보 조회"}
    )
    public ApiResponse<UserDetailsInfoResponseDto> userDetailsInfo(
            @PathVariable("userId") Long userId,
            @AuthenticationPrincipal Long loginUserId
    ) {
        UserDetailsInfoResponseDto userDetailsInfoResponseDto= userService.userDetailsInfo(userId, loginUserId);

        return ApiResponse.ok(userDetailsInfoResponseDto);
    }

    /**
     * 닉네임 변경을 위한 api
     *
     * @param userUpdateNicknameRequestDto 유저 닉네임 요청한 dto
     * @param userId 유저 엔티티
     * */
    @PatchMapping("/user/update/nickname")
    @Operation(
            summary = "닉네임 변경 api",
            description = "해당 유저의 닉네임(중복 가능) 닉네임 변경이 가능합니다.\n " +
                    "닉네임은 특수문자를 제외한 2~13자리여야 합니다",
            tags = {"유저 상세정보 조회"}
    )
    public ApiResponse<UserDetailsInfoResponseDto> userUpdateNickname(
            @Valid @RequestBody UserUpdateNicknameRequestDto userUpdateNicknameRequestDto,
            @AuthenticationPrincipal Long userId
    ) {
        userService.userUpdateNickname(userId, userUpdateNicknameRequestDto);

        return ApiResponse.ok();
    }

    /**
     * 유저 최초 정보 수집을 위한 api
     *
     * @param userUpdatePositionAndSkillRequestDto 유저 포지션과 스킬타입을 요청한 dto
     * @param userId user엔티티 객체
     * */
    @PatchMapping("/user/update/info")
    @Operation(
            summary = "최초 로그인 시 정보수집 요청 api",
            description = "해당 유저의 포지션, 실력을 업데이트 가능합니다.",
            tags = {"유저 상세정보 조회"}
    )
    public ApiResponse<UserDetailsInfoResponseDto> userUpdatePositionAndSkillLevel(
                @Valid @RequestBody UserUpdatePositionAndSkillRequestDto userUpdatePositionAndSkillRequestDto,
            @AuthenticationPrincipal Long userId
    ) {
        log.debug("UserUpdatePositionAndSkillRequestDto = {}", userUpdatePositionAndSkillRequestDto.toString());
        userService.userUpdatePositionAndSkillLevel(userId, userUpdatePositionAndSkillRequestDto);

        return ApiResponse.ok();
    }
}
