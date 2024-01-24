package sync.slamtalk.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class  CorsConfig {
   @Bean
   public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      // 해당 경로로 부터 오는 요청들을 허용하겠다고 설정
      config.addAllowedOriginPattern("*");
      config.addAllowedOrigin("http://www.slam-talk.site:8080");
      config.addAllowedOrigin("http://localhost:3000");
      config.addAllowedOrigin("http://localhost:3001");
      config.addAllowedHeader("*");
      config.addAllowedMethod("*");

      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
   }
}
