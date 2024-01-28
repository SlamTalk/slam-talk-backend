package sync.slamtalk.security.oauth2.handler;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.security.oauth2.CustomOAuth2User;
import sync.slamtalk.security.utils.CookieUtil;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    @Value("${jwt.access.header}")
    public String accessAuthorizationHeader;
    @Value("${jwt.refresh.header}")
    public String refreshAuthorizationCookieName;
    @Value("${jwt.refresh.expiration}")
    private int refreshTokenExpirationPeriod;
    @Value("${jwt.domain}")
    private String domain;

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        DefaultRedirectStrategy defaultRedirectStrategy = new DefaultRedirectStrategy();
        defaultRedirectStrategy.setStatusCode(HttpStatus.TEMPORARY_REDIRECT);

        log.debug("OAuth2 Login 성공!");
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
        Optional<User> optionalUser = userRepository.findByEmailAndSocialType(oAuth2User.getEmail(), oAuth2User.getSocialType());
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("유저 정보가 없습니다.");
        }

        User user = optionalUser.get();
        String refreshToken = jwtTokenProvider.createRefreshToken();
        user.updateRefreshToken(refreshToken);

        // HTTP 상태 코드 설정 - 301 Redirect
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);

        setRefreshTokenCookie(response, refreshToken);

        response.sendRedirect("http://localhost:3000/login-success");
    }


    /**
     * 쿠키에 리프레쉬 토큰을 저장하는 메서드
     *
     * @param response
     * @param refreshToken
     */
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
}
