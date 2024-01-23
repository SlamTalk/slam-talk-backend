package sync.slamtalk.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sync.slamtalk.security.dto.JwtTokenResponseDto;
import sync.slamtalk.security.utils.CookieUtil;

import java.io.IOException;
import java.util.Optional;

/**
 * Jwt를 위한 커스텀 필터
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { // BaseAuthoriFilter // permitAll 관련
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

   private final JwtTokenProvider tokenProvider;

   /**
    * Jwt 토큰의 인증정보를 SecurityContext에 저장하는 역할을 수행
    * 실제 필터링 로직은 여기에 작성
    * */
   @Override
   protected void doFilterInternal(
           HttpServletRequest httpServletRequest,
           HttpServletResponse httpServletResponse,
           FilterChain filterChain
   ) throws ServletException, IOException {

      String accessToken = resolveCookie(httpServletRequest, this.authorizationHeader);
      String refreshToken = resolveCookie(httpServletRequest, this.refreshAuthorizationHeader);

      String requestURI = httpServletRequest.getRequestURI();

      // 1. Request Cookie accessToken 토큰 추출 및 유효성 검사
      if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
         authenticateFromToken(accessToken, requestURI);
      }

      // 2. Request Header에서 refreshToken 추출 및 유효성 검사
      else if(StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)){
         Optional<JwtTokenResponseDto> optionalJwtTokenResponseDto = tokenProvider.generateNewAccessToken(refreshToken);
         if(optionalJwtTokenResponseDto.isPresent()){
            JwtTokenResponseDto jwtTokenResponseDto = optionalJwtTokenResponseDto.get();

            CookieUtil.addCookie(
                    httpServletResponse,
                    authorizationHeader,
                    jwtTokenResponseDto.getAccessToken(),
                    accessTokenExpirationPeriod,
                    domain
            );

            CookieUtil.addCookie(
                    httpServletResponse,
                    refreshAuthorizationHeader,
                    jwtTokenResponseDto.getRefreshToken(),
                    refreshTokenExpirationPeriod,
                    domain
            );

            log.debug("토큰이 재발급되었습니다.");

            authenticateFromToken(jwtTokenResponseDto.getAccessToken(), requestURI);
         }
      }

      filterChain.doFilter(httpServletRequest, httpServletResponse);
   }

   /**
    * 토큰에서 Authentication 객체를 추출하여 SecurityContextHolder에 저장하는 메서드
    * @param accessToken
    * @param requestURI
    * */
   private void authenticateFromToken(String accessToken, String requestURI) {
      Authentication authentication = tokenProvider.getAuthentication(accessToken);

      // 요청이 들어오는 순간 SecurityContextHolder에 authentication 가 저장됨
      // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
   }

   /**
    * request 쿠키에서 token 추출하는 메서드
    * @param request
    * @param cookieHeaderName
    * @return String
    * */
   private String resolveCookie(HttpServletRequest request, String cookieHeaderName) {
      /* Cookie 에서 Token 정보 가져오는 로직 */
      Optional<Cookie> optionalAccessTokenCookie = CookieUtil.getCookie(request, cookieHeaderName);

      if(optionalAccessTokenCookie.isPresent()){
         return optionalAccessTokenCookie.get().getValue();
      }

      return "";
   }

}
