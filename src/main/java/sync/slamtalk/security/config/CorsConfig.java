package sync.slamtalk.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class  CorsConfig {
   @Bean
   public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      // 해당 경로로 부터 오는 요청들을 허용하겠다고 설정
      config.addAllowedOriginPattern("*");
      config.addAllowedOrigin("https://slam-talk.vercel.app");
      config.addAllowedOrigin("http://localhost:3000");
      config.addAllowedOrigin("http://localhost:3001");
      config.addAllowedHeader("*");
      config.addAllowedMethod("*");
      // 다른도메인에서 오는 인증정보 포함을 허용할 경우 true로 설정
      config.setAllowCredentials(true);

      // 클라이언트 response header 허용
      config.setExposedHeaders(Arrays.asList(
              "Authorization",
              "Set-Cookie"
      ));

      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
   }
}
