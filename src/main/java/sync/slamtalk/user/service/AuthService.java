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
import sync.slamtalk.chat.redis.RedisService;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.security.dto.JwtTokenDto;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.security.utils.CookieUtil;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.request.UserChangePasswordReq;
import sync.slamtalk.user.dto.request.UserLoginReq;
import sync.slamtalk.user.dto.request.UserSignUpReq;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;

import java.util.Optional;

import static sync.slamtalk.user.error.UserErrorResponseCode.ALREADY_CANCEL_USER;

/**
 * 이 컨트롤러는 유저의 인증과 관련된 기능을 다루는 클래스입니다.
 * */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;
    private final RevokeService revokeService;
    @Value("${jwt.access.header}")
    public String accessAuthorizationHeader;
    @Value("${jwt.access.expiration}")
    public int accessTokenExpirationPeriod;
    @Value("${jwt.refresh.header}")
    public String refreshAuthorizationCookieName;
    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpirationPeriod;
    @Value("${jwt.domain}")
    private String domain;


    /**
     * 로그인 시 검증 및 액세스 토큰 리프래쉬 토큰 발급 로직
     *
     * @param userLoginReqDto 유저로그인 dto
     * @param response HttpServletResponse를 받음
     * @return JwtTokenDto
     */
    @Transactional
    public void login(
            UserLoginReq userLoginReqDto,
            HttpServletResponse response
    ) {
        try {
            Authentication authentication = getAuthentication(userLoginReqDto.getEmail(), userLoginReqDto.getPassword());

            User user = (User) authentication.getPrincipal();

            // 3. 인증 정보를 기반으로 JWT 토큰 생성)
            JwtTokenDto jwtTokenDto = tokenProvider.createToken(user);

            response.addHeader(accessAuthorizationHeader, jwtTokenDto.getAccessToken());
            setRefreshTokenCookie(response, jwtTokenDto.getRefreshToken());
            user.updateRefreshToken(jwtTokenDto.getRefreshToken());

            // 최초 정보수집을 위해 jwtTokenResponseDto의 firstLoginCheck은 true 로 반환, 이후는 false 로 반환하기 위한 로직
            if(Boolean.TRUE.equals(user.getFirstLoginCheck())) user.updateFirstLoginCheck();

        } catch (Exception e) {
            throw new BaseException(UserErrorResponseCode.BAD_CREDENTIALS);
        }
    }

    /**
     * 회원가입 검증 및 회원가입 로직
     *
     * @param userSignUpReqDto UserSignUpRequestDto
     * @param response HttpServletResponse
     * @return JwtTokenResponseDto
     */
    @Transactional
    public void signUp(
            UserSignUpReq userSignUpReqDto,
            HttpServletResponse response
    ) {

        // 이메일 인증된 사용자인지 판별 하는 로직
        String isAuth = redisService.getData(userSignUpReqDto.getEmail());
        if(isAuth == null || !isAuth.equals("OK")){
            log.debug("이메일 인증을 하지 않았습니다!");
            throw new BaseException(UserErrorResponseCode.UNVERIFIED_EMAIL);
        }
        // 삭제된 유저인지 검증
        checkAlreadyCancelUser(userSignUpReqDto);
        // 중복 이메일 검증
        checkEmailExistence(userSignUpReqDto);
        // 중복 닉네임 검증
        checkNicknameExistence(userSignUpReqDto.getNickname());

        User user = userSignUpReqDto.toEntity();
        user.passwordEncode(passwordEncoder);

        userRepository.save(user);

        // 레디스 이메일 인증한 유저 삭제하기
        redisService.deleteData(userSignUpReqDto.getEmail());

        login(new UserLoginReq(userSignUpReqDto.getEmail(), userSignUpReqDto.getPassword()), response);
    }

    /**
     * 로컬 개발용 회원가입 검증 및 회원가입 로직
     *
     * @param userSignUpReqDto UserSignUpRequestDto
     * @param response HttpServletResponse
     * @return JwtTokenResponseDto
     */
    @Transactional
    public void testSignUp(
            UserSignUpReq userSignUpReqDto,
            HttpServletResponse response
    ) {
        User user = userSignUpReqDto.toEntity();
        user.passwordEncode(passwordEncoder);

        userRepository.save(user);

        login(new UserLoginReq(userSignUpReqDto.getEmail(), userSignUpReqDto.getPassword()), response);
    }

    private void checkAlreadyCancelUser(UserSignUpReq userSignUpReqDto) {
        if(userRepository.findUserByEmailAndSocialTypeIgnoringWhere(userSignUpReqDto.getEmail(), SocialType.LOCAL.toString()).isPresent()){
            throw new BaseException(ALREADY_CANCEL_USER);
        }
    }

    /**
     * 쿠키에서 리프래쉬 토큰을 추출하여 엑세스 토큰과 리프래쉬 토큰을 재발급하는 메서드
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return JwtTokenResponseDto
     */
    @Transactional
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String refreshToken = tokenProvider.getTokenFromCookie(request, refreshAuthorizationCookieName);

        log.debug("[엑세스 토큰 재발급] refreshToken = {}", refreshToken);
        // 리프래쉬 토큰 만료 검사
        if(!tokenProvider.validateToken(refreshToken)){
            log.debug("[엑세스 토큰 재발급] 토큰 만료가 됨 1");
            throw new BaseException(UserErrorResponseCode.INVALID_TOKEN);
        }

        // 엑세스 토큰 만료가 되었다면 재발급 하기
        Optional<JwtTokenDto> optionalJwtTokenResponseDto = tokenProvider.generateNewAccessToken(refreshToken);
        if (optionalJwtTokenResponseDto.isEmpty()) {
            log.debug("[엑세스 토큰 재발급] 토큰 만료가 됨 2");
            throw new BaseException(UserErrorResponseCode.INVALID_TOKEN);
        }

        JwtTokenDto jwtTokenDto = optionalJwtTokenResponseDto.get();
        log.debug("[엑세스 토큰 재발급] jwtTokenDto.getAccessToken() = {}", jwtTokenDto.getAccessToken());

        /* 엑세스 토큰 헤더에 저장*/
        response.addHeader(accessAuthorizationHeader, jwtTokenDto.getAccessToken());
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
    private void checkEmailExistence(UserSignUpReq userSignUpReq) {
        if (userRepository.findByEmailAndSocialType(userSignUpReq.getEmail(), SocialType.LOCAL).isPresent()) {
            log.debug("이미 존재하는 유저 이메일입니다.");
            throw new BaseException(UserErrorResponseCode.EMAIL_ALREADY_EXISTS);
        }
    }

    /**
     * 쿠키에 리프레쉬 토큰을 저장하는 메서드
     * @param response HttpServletResponse
     * @param refreshToken refreshToken
     * */
    private void setRefreshTokenCookie(
            HttpServletResponse response,
            String refreshToken
    ) {
        CookieUtil.addCookie(
                response,
                refreshAuthorizationCookieName,
                refreshToken,
                refreshTokenExpirationPeriod,
                domain
        );
    }

    /**
     * 회원 탈퇴
     * @param userId : 탈퇴하고자 하는 USER
     * */
    @Transactional
    public void cancelUser(
            Long userId
    ) {
        // todo : 소셜 로그인일 경우 별도의 처리가 필요하다!
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));
        SocialType socialType = user.getSocialType();

  /*      if(socialType.equals(SocialType.KAKAO)){
            revokeService.deleteKakaoAccount(user);
        }*/ /*else if (socialType.equals(SocialType.GOOGLE)){
            revokeService.deleteGoogleAccount(user, accessToken);
        } else if (socialType.equals(SocialType.NAVER)) {
            revokeService.deleteNaverAccount(user, accessToken);
        }*/

        userRepository.deleteById(userId);
        // todo : 게시판, 채팅, 팀매칭, 상대팀 매칭 모든 글의 softDelete 처리를 해줘야함.
        // todo : 댓글 및 좋아요도 해줘야한다.
        // todo : 출석 테이블도 삭제 처리해야함.
        // todo : 이후 스케줄러를 통해 매번 1번씩 soft 처리된 모든 필드를 삭제하는 로직을 가져가야할 것 같다.

    }

    /**
     * 비밀번호 변경 메서드
     * @param userChangePasswordReq : 변경하고자하는 이메일 및 비밀번호를 담은 dto
     * */
    @Transactional
    public void userChangePassword(UserChangePasswordReq userChangePasswordReq) {

        // 레디스 서버에서 인증했는지 확인하기
        String isAuth = redisService.getData(userChangePasswordReq.getEmail());
        if(isAuth == null || !isAuth.equals("OK")){
            throw new BaseException(UserErrorResponseCode.UNVERIFIED_EMAIL);
        }

        User user = userRepository.findByEmail(userChangePasswordReq.getEmail())
                        .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));
        user.updatePasswordAndEnCoding(passwordEncoder, userChangePasswordReq.getPassword());
        redisService.deleteData(userChangePasswordReq.getEmail());
    }
}
