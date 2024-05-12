package sync.slamtalk.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

import java.io.IOException;
import java.util.List;

/**
 * Jwt를 위한 커스텀 필터
 * */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter { // BaseAuthoriFilter // permitAll 관련
   @Value("${jwt.access.header}")
   public String authorizationHeader;
   @Value("${app.exception-paths}")
   private List<String> EXCEPTION_PATHS;
   @Value("${jwt.refresh.header}")
   public String refreshAuthorizationHeader;
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

      String accessToken = tokenProvider.resolveAccessToken(httpServletRequest);

      String requestURI = httpServletRequest.getRequestURI();

      // EXCEPTION_PATHS 경로에 있는 것들은 토큰 검사 안함.
      for(String exceptionPath : EXCEPTION_PATHS){
         if(httpServletRequest.getRequestURI().contains(exceptionPath)){
            log.trace("이 페이지는 검사 안함 : {}", httpServletRequest.getRequestURI());
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
         }
      }


      // TODO : 로그인, 회원가입, 리프레쉬, 로그아웃 시 URL 안거치게 하는 로직 추가하기

      // 1. Request header accessToken 토큰 추출 및 유효성 검사
      if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
         authenticateFromToken(accessToken, requestURI);
      }

      // 2. Request header accessToken 토큰 추출 결과가 false 일 경우
      // 엑세스 토큰이 만료되었다고 401 에러를 던져야 함.
      else if(StringUtils.hasText(accessToken) && !tokenProvider.validateToken(accessToken)){
         log.debug("토큰이 만료 되었습니다!");
         httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러 설정
         httpServletResponse.getWriter().write("Unauthorized: Access is denied due to invalid credentials");
         httpServletResponse.getWriter().flush();
         httpServletResponse.getWriter().close();
         return;
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
}
