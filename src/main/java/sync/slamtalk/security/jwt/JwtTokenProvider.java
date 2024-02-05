package sync.slamtalk.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.security.dto.JwtTokenDto;
import sync.slamtalk.security.utils.CookieUtil;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 토큰 생성 및 유효성 검증 하는 클래스
 */
@Slf4j
@Component
@Transactional(readOnly = true)
public class JwtTokenProvider implements InitializingBean {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String GRANT_TYPE = "Bearer";
    private final String secretKey;
    /* AccessToken 설정 */
    private final int accessTokenExpirationPeriod;
    /* RefreshToken 설정 */
    private final int refreshTokenExpirationPeriod;
    private final UserRepository userRepository;
    private SecretKey key;

    @Value("${jwt.access.header}")
    public String accessAuthorizationHeader;
    @Value("${jwt.refresh.header}")
    public String refreshAuthorizationCookieName;

    public JwtTokenProvider(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.access.expiration}") int accessTokenExpirationPeriod,
            @Value("${jwt.refresh.expiration}") int refreshTokenExpirationPeriod,
            UserRepository userRepository) {
        this.secretKey = secretKey;
        this.accessTokenExpirationPeriod = accessTokenExpirationPeriod * 1000;
        this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriod * 1000;
        this.userRepository = userRepository;
    }

    /**
     * 디코딩된 바이트 배열을 HMAC SHA 알고리즘을 사용하는 키로 변환하는 메서드
     */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // 문자열 형태의 Base64 디코딩하여 바이트 배열로 변환
        this.key = Keys.hmacShaKeyFor(keyBytes); // HMAC SHA 알고리즘을 사용하는 키로 생성
    }

    /**
     * Member 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
     *
     * @param user
     * @return String
     */
    @Transactional
    public JwtTokenDto createToken(User user) {

        // 권한 정보 가져오기
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.debug("authorities = {}", authorities);

        String accessToken = createAccessToken(user, authorities);
        String refreshToken = createRefreshToken();

        user.updateRefreshToken(refreshToken);

        return new JwtTokenDto(GRANT_TYPE, accessToken, refreshToken);
    }

    /**
     * User 정보와 권환정보를 이용해서 AccessToken 발급 하는 메서드
     *
     * @param user        해당하는 유저
     * @param authorities 권한정보(UserRole)
     * @return accessToken
     */
    public String createAccessToken(User user, String authorities) {
        long now = (new Date()).getTime();
        Date accessTokenValidity = new Date(now + this.accessTokenExpirationPeriod);

        return Jwts.builder()
                .subject(String.valueOf(user.getId())) // 사용자이름 이름을 클레임으로 저장.
                .claim(AUTHORITIES_KEY, authorities) // 권한 정보를 저장
                .expiration(accessTokenValidity) // 토큰 만료 시간 저장
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * refreshToken 발급 하는 메서드
     *
     * @return refreshToken
     */
    public String createRefreshToken() {
        long now = (new Date()).getTime();
        Date refreshTokenValidity = new Date(now + this.refreshTokenExpirationPeriod);

        return Jwts.builder()
                .expiration(refreshTokenValidity) // 토큰 만료 시간 저장
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * accessToken을 복호화 해서 userId를 얻어오는 메서드
     *
     * @param accessToken
     * @return Authentication
     */
    public Authentication getAuthentication(String accessToken) {

        // Jwt 토큰 복호화
        Claims claims = getClaimsFromAccessToken(accessToken);

        Long userId = Long.valueOf(claims.getSubject());

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());


        return new UsernamePasswordAuthenticationToken(userId, accessToken, authorities);
    }

    /**
     * 웹소켓 STOMP 사용시 accessToken에서 userId 추출하는 메서드
     * @param accessToken
     * @return long userid
     * */
    public Long stompExtractUserIdFromToken(String accessToken){
        // 웹 소켓에서 오는 Bearer 키워드 제거하기
        String token = resolveToken(accessToken);

        // Jwt 토큰 복호화
        Claims claims = getClaimsFromAccessToken(token);

        return Long.valueOf(claims.getSubject());
    }

    /**
     *  accessToken에서 서명 검증 및 Claims 반환하는 메서드
     *
     * @param accessToken 엑세스 토큰
     * @return Claims 사용자에 대한 정보
     * */
    private Claims getClaimsFromAccessToken(String accessToken) {
        Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(key) // 서명 검증
                .build()
                .parseSignedClaims(accessToken);

        Claims claims = claimsJws.getPayload();

        if (claims.get(AUTHORITIES_KEY) == null) {
            log.info("권한 정보가 없는 토큰입니다");
            throw new BaseException(UserErrorResponseCode.INVALID_TOKEN);
        }
        return claims;
    }

    /**
     * 토큰 검증하는 메서드
     *
     * @return true 검증 성공 / false 검증 실패
     */
    public boolean validateToken(String token) {
        try {
            // Jwt 토큰 복호화
            Jwts
                    .parser()
                    .verifyWith(key) // 서명 검증
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.debug("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.debug("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }


    /**
     * RefreshToken으로 AccessToken 재발급하는 메서드
     * @param refreshToken
     * @return Optional<JwtTokenResponseDto>
     * */
    @Transactional
    public Optional<JwtTokenDto> generateNewAccessToken(String refreshToken){
        log.debug("엑세스 토큰 재발급");
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElse(null);

        if(user != null) {
            return Optional.of(createToken(user));
        }
        return Optional.empty();
    }

    /**
     * request 헤더에서 AccessToken 추출하는 메서드
     * @param request
     * @return String : AccessToken
     * */
    public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(accessAuthorizationHeader);

        return resolveToken(bearerToken);
    }

    /**
     * Token의 Bearer유형의 토큰 접두사 를 제거하는 메서드
     * @param bearerToken
     * @return String : RefreshToken
     * */
    private String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * request 쿠키에서 RefreshToken 추출하는 메서드
     * @param request
     * @return String
     * */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        /* Cookie 에서 Token 정보 가져오는 로직 */
        Optional<Cookie> optionalAccessTokenCookie = CookieUtil.getCookie(request, refreshAuthorizationCookieName);

        if(optionalAccessTokenCookie.isPresent()){
            return optionalAccessTokenCookie.get().getValue();
        }

        return "";
    }
}
