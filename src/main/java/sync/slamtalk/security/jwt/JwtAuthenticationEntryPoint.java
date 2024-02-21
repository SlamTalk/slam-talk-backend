package sync.slamtalk.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 유효한 자격증명을 제공하지 않고 접근하려 할때 401 Unauthroized 에러를 리턴할 클래스
 * */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
   @Override
   public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
      // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
      log.debug("JwtAuthenticationEntryPoint 동작");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
   }
}
