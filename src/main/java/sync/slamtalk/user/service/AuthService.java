package sync.slamtalk.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.security.dto.JwtTokenDto;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.security.utils.CookieUtil;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.UserDetailsAfterRefreshResponseDto;
import sync.slamtalk.user.dto.UserLoginRequestDto;
import sync.slamtalk.user.dto.UserLoginResponseDto;
import sync.slamtalk.user.dto.UserSignUpRequestDto;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;

import java.util.Optional;

/**
 * 이 컨트롤러는 유저의 인증과 관련된 기능을 다루는 클래스입니다.
 * */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MatePostRepository matePostRepository;
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
     * @param userLoginDto 유저로그인 dto
     * @param response HttpServletResponse를 받음
     * @return JwtTokenDto
     */
    @Transactional
    public UserLoginResponseDto login(
            UserLoginRequestDto userLoginDto,
            HttpServletResponse response
    ) {
        try {
            Authentication authentication = getAuthentication(userLoginDto.getEmail(), userLoginDto.getPassword());

            User user = (User) authentication.getPrincipal();

            // 3. 인증 정보를 기반으로 JWT 토큰 생성)
            JwtTokenDto jwtTokenDto = tokenProvider.createToken(user);

            response.addHeader(accessAuthorizationHeader, jwtTokenDto.getAccessToken());
            setRefreshTokenCookie(response, jwtTokenDto);

            UserLoginResponseDto userLoginResponseDto = new UserLoginResponseDto(
                    user.getId(),
                    user.getNickname(),
                    user.getImageUrl(),
                    user.getFirstLoginCheck()
            );

            // 최초 정보수집을 위해 jwtTokenResponseDto의 firstLoginCheck은 true 로 반환, 이후는 false 로 반환하기 위한 로직
            if(Boolean.TRUE.equals(user.getFirstLoginCheck())) user.updateFirstLoginCheck();

            return userLoginResponseDto;

        } catch (Exception e) {
            throw new BaseException(UserErrorResponseCode.BAD_CREDENTIALS);
        }
    }

    /**
     * 회원가입 검증 및 회원가입 로직
     *
     * @param userSignUpDto UserSignUpRequestDto
     * @param response HttpServletResponse
     * @return JwtTokenResponseDto
     */
    @Transactional
    public UserLoginResponseDto signUp(
            UserSignUpRequestDto userSignUpDto,
            HttpServletResponse response
    ) {
        // 중복 이메일 검증
        checkEmailExistence(userSignUpDto);
        // 중복 닉네임 검증
        checkNicknameExistence(userSignUpDto.getNickname());

        User user = userSignUpDto.toEntity();
        user.passwordEncode(passwordEncoder);

        userRepository.save(user);

        return login(new UserLoginRequestDto(userSignUpDto.getEmail(), userSignUpDto.getPassword()), response);
    }

    /**
     * 쿠키에서 리프래쉬 토큰을 추출하여 엑세스 토큰과 리프래쉬 토큰을 재발급하는 메서드
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return JwtTokenResponseDto
     */
    @Transactional
    public UserDetailsAfterRefreshResponseDto refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = tokenProvider.getRefreshTokenFromCookie(request);

        // 토큰 만료 검사
        if(!tokenProvider.validateToken(refreshToken)){
            throw new BaseException(UserErrorResponseCode.INVALID_TOKEN);
        }

        Optional<JwtTokenDto> optionalJwtTokenResponseDto = tokenProvider.generateNewAccessToken(refreshToken);

        if (optionalJwtTokenResponseDto.isEmpty()) {
            throw new BaseException(UserErrorResponseCode.INVALID_TOKEN);
        }

        JwtTokenDto jwtTokenDto = optionalJwtTokenResponseDto.get();

        /* 엑세스 토큰 헤더에 저장 및 리프레쉬 토큰 쿠키에 저장하는 로직 */
        response.addHeader(accessAuthorizationHeader, jwtTokenDto.getAccessToken());
        setRefreshTokenCookie(response, jwtTokenDto);


        // UserLoginResponseDto 생성 로직.
        User user = userRepository.findByRefreshToken(jwtTokenDto.getRefreshToken())
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.INVALID_TOKEN));

        // 레벨 score 계산하기
        long levelScore = 0L;

        // Mate 게시판 상태가 Complete
        long mateCompleteParticipationCount = matePostRepository.findMateCompleteParticipationCount(user.getId());
        levelScore += mateCompleteParticipationCount * User.MATE_LEVEL_SCORE;

        // todo : teamMatchingCompleteParticipationCount 팀매칭이 완료된 경우의 개수 세기
        long teamMatchingCompleteParticipationCount = 0L;
        // todo : 출석부 개수 counting 하기


        UserDetailsAfterRefreshResponseDto refreshResponseDto =
                UserDetailsAfterRefreshResponseDto.from(
                        user, levelScore, mateCompleteParticipationCount, teamMatchingCompleteParticipationCount);

        return refreshResponseDto;
    }

    /**
     * UsernamePasswordAuthenticationToken를 이용해서 email과 password를 통해
     * SecurityContextHolder에서 Authentication을 추출하는 메서드
     *
     * @param  email 이메일
     * @param  password 패스워드
     * @return Authentication
     * */
    private Authentication getAuthentication(
            String email,
            String password
    ) {
        // 1. email + password 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.debug("authenticationToken ={}", authenticationToken);

        return authentication;
    }

    /**
     * 회원가입 시 중복 닉네임이 존재하는지 검사하는 메서드
     *
     * @param nickname 유저 닉네임
     * */
    private void checkNicknameExistence(String nickname) {
        String lowercaseNickname = nickname.toLowerCase();
        if (userRepository.findByNickname(lowercaseNickname).isPresent()) {
            log.debug("이미 존재하는 닉네임입니다.");
            throw new BaseException(UserErrorResponseCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     * 회원가입 시 중복 이메일이 존재하는지 검사하는 메서드
     * */
    private void checkEmailExistence(UserSignUpRequestDto userSignUpDto) {
        if (userRepository.findByEmailAndSocialType(userSignUpDto.getEmail(), SocialType.LOCAL).isPresent()) {
            log.debug("이미 존재하는 유저 이메일입니다.");
            throw new BaseException(UserErrorResponseCode.EMAIL_ALREADY_EXISTS);
        }
    }

    /**
     * 쿠키에 리프레쉬 토큰을 저장하는 메서드
     * @param response HttpServletResponse
     * @param jwtTokenDto JwtTokenDto
     * */
    private void setRefreshTokenCookie(
            HttpServletResponse response,
            JwtTokenDto jwtTokenDto
    ) {
        CookieUtil.addCookie(
                response,
                refreshAuthorizationCookieName,
                jwtTokenDto.getRefreshToken(),
                refreshTokenExpirationPeriod,
                domain
        );
    }
}
