package sync.slamtalk.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.security.dto.JwtTokenResponseDto;
import sync.slamtalk.security.utils.CookieUtil;
import sync.slamtalk.user.UserService;
import sync.slamtalk.user.dto.UserLoginRequestDto;
import sync.slamtalk.user.dto.UserSignUpRequestDto;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    @Value("${jwt.access.header}")
    public String authorizationHeader;
    @Value("${jwt.refresh.header}")
    public String refreshAuthorizationHeader;
    @Value("${jwt.access.expiration}")
    private int accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpirationPeriod;
    @Value("${jwt.domain}")
    private String domain;
    private final UserService userService;

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
    public ApiResponse<JwtTokenResponseDto> authorize(
            @Valid @RequestBody UserLoginRequestDto userLoginDto,
            HttpServletResponse response
    ) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        JwtTokenResponseDto jwtTokenResponseDto = userService.login(userLoginDto, response);

        return ApiResponse.ok(jwtTokenResponseDto);
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
    public ApiResponse<String> signUp(@Valid @RequestBody UserSignUpRequestDto userSignUpDto) {
        userService.signUp(userSignUpDto);
        return ApiResponse.ok("회원가입 성공");
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
    public ApiResponse<JwtTokenResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        JwtTokenResponseDto jwtTokenResponseDto = userService.refreshToken(request, response);

        return ApiResponse.ok(jwtTokenResponseDto);
    }
}
