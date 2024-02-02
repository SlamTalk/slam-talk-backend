package sync.slamtalk.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.user.dto.*;
import sync.slamtalk.user.service.AuthService;

/**
 * 이 컨트롤러는 인증과 관련된 기능을 다루는 클래스입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    @Value("${jwt.access.header}")
    public String authorizationHeader;
    @Value("${jwt.refresh.header}")
    public String refreshAuthorizationHeader;
    private final AuthService authService;

    /**
     * 로그인 api
     * @param  userLoginDto
     * @param response
     *
     * @return  jwtTokenResponseDto
     * */
    @PostMapping("/login")
    @Operation(
            summary = "자체 로그인 기능",
            description = "자체 로그인 기능입니다.",
            tags = {"로그인/회원가입"}
    )
    public ApiResponse<UserLoginResponseDto> authorize(
            @Valid @RequestBody UserLoginRequestDto userLoginDto,
            HttpServletResponse response
    ) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UserLoginResponseDto userLoginResponseDto = authService.login(userLoginDto, response);

        return ApiResponse.ok(userLoginResponseDto);
    }

    /**
     * 회원 가입 api
     * @param  userSignUpDto
     *
     * @return  회원가입 성공
     * */
    @PostMapping("/sign-up")
    @Operation(
            summary = "자체 회원가입 기능",
            description = "자체 회원가입 기능입니다.",
            tags = {"로그인/회원가입"}
    )
    public ApiResponse<UserLoginResponseDto> signUp(
            @Valid @RequestBody UserSignUpRequestDto userSignUpDto,
            HttpServletResponse response) {
        UserLoginResponseDto userLoginResponseDto = authService.signUp(userSignUpDto, response);
        return ApiResponse.ok(userLoginResponseDto);
    }

    /**
     * 리프래쉬 토큰 재발급 api
     * @param request
     * @param response
     *
     * @return  JwtTokenResponseDto
     * */
    @PatchMapping("/tokens/refresh")
    @Operation(
            summary = "엑세스 및 리프레쉬 토큰 재발급",
            description = "리프레쉬 토큰은 httpOnly secure 쿠키로 보내주고 엑세스 토큰은 헤더와 파라미터에 넣어 보내줍니다.",
            tags = {"로그인/회원가입"}
    )
    public ApiResponse<UserDetailsAfterRefreshResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        UserDetailsAfterRefreshResponseDto refreshResponseDto = authService.refreshToken(request, response);
        return ApiResponse.ok(refreshResponseDto);
    }
}
