package sync.slamtalk.user;

import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.security.dto.JwtTokenResponseDto;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.security.utils.CookieUtil;
import sync.slamtalk.user.dto.UserLoginRequestDto;
import sync.slamtalk.user.dto.UserSignUpRequestDto;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;

import java.security.DrbgParameters;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;
    @Value("${jwt.access.header}")
    public String accessAuthorizationHeader;
    @Value("${jwt.refresh.header}")
    public String refreshAuthorizationCookieName;
    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpirationPeriod;
    @Value("${jwt.domain}")
    private String domain;


    /**
     * 로그인 시 검증 및 액세스 토큰 리프래쉬 토큰 발급 로직
     *
     * @param userLoginDto
     * @param response
     * @return JwtTokenDto
     */
    @Transactional
    public JwtTokenResponseDto login(
            UserLoginRequestDto userLoginDto,
            HttpServletResponse response
    ) {
        try {
            // 1. email + password 를 기반으로 Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDto.getEmail(), userLoginDto.getPassword());

            // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.debug("authenticationToken ={}", authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성)
            JwtTokenResponseDto jwtTokenResponseDto = tokenProvider.createToken((User) authentication.getPrincipal());

            response.addHeader(accessAuthorizationHeader, jwtTokenResponseDto.getAccessToken());
            setRefreshTokenCookie(response, jwtTokenResponseDto);

            return jwtTokenResponseDto;
        } catch (Exception e) {
            throw new BaseException(UserErrorResponseCode.BAD_CREDENTIALS);
        }
    }

    /**
     * 회원가입 검증 및 회원가입 로직
     *
     * @param userSignUpDto
     */
    @Transactional
    public void signUp(UserSignUpRequestDto userSignUpDto) {

        if (userRepository.findByEmailAndSocialType(userSignUpDto.getEmail(), SocialType.LOCAL).isPresent()) {
            log.debug("이미 존재하는 유저 이메일입니다.");
            throw new BaseException(UserErrorResponseCode.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            log.debug("이미 존재하는 닉네임입니다.");
            throw new BaseException(UserErrorResponseCode.NICKNAME_ALREADY_EXISTS);
        }

        User user = User.from(userSignUpDto);
        user.passwordEncode(passwordEncoder);

        userRepository.save(user);
    }

    /**
     * 쿠키에서 리프래쉬 토큰을 추출하여 엑세스 토큰과 리프래쉬 토큰을 재발급하는 메서드
     *
     * @param request
     * @param response
     * @return JwtTokenResponseDto
     */
    @Transactional
    public JwtTokenResponseDto refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = tokenProvider.getRefreshTokenFromCookie(request);

        Optional<JwtTokenResponseDto> optionalJwtTokenResponseDto = tokenProvider.generateNewAccessToken(refreshToken);

        if (optionalJwtTokenResponseDto.isEmpty()) {
            throw new BaseException(UserErrorResponseCode.INVALID_TOKEN);
        }

        JwtTokenResponseDto jwtTokenResponseDto = optionalJwtTokenResponseDto.get();

        /* 엑세스 토큰및 리프레쉬 토큰 저장하는 로직 */
        response.addHeader(accessAuthorizationHeader, jwtTokenResponseDto.getAccessToken());
        setRefreshTokenCookie(response, jwtTokenResponseDto);
        return jwtTokenResponseDto;
    }

    /**
     * 쿠키에 리프레쉬 토큰을 저장하는 메서드
     * @param response
     * @param jwtTokenResponseDto
     * */
    private void setRefreshTokenCookie(
            HttpServletResponse response,
            JwtTokenResponseDto jwtTokenResponseDto
    ) {
        CookieUtil.addCookie(
                response,
                refreshAuthorizationCookieName,
                jwtTokenResponseDto.getRefreshToken(),
                refreshTokenExpirationPeriod,
                domain
        );

        jwtTokenResponseDto.clearRefreshToken();
    }
}
