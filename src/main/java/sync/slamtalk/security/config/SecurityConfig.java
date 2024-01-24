package sync.slamtalk.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import sync.slamtalk.security.jwt.JwtAccessDeniedHandler;
import sync.slamtalk.security.jwt.JwtAuthenticationEntryPoint;
import sync.slamtalk.security.jwt.JwtFilter;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.user.entity.UserRole;

@Configuration
@EnableWebSecurity // Web 보안 활성화
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtTokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;

    /**
     * JwtFilter를 통해 Security 로직에 필터를 등록
     */
    public SecurityConfig(
            JwtTokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler,
            JwtFilter jwtFilter
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Password 인코더 설정
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 필터 체이닝
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                // exceptionHandler를 우리가 만든 클래스로 재정의
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // 토큰이 없는 상태에서 요청이 오는 정보들을 열어
                .authorizeHttpRequests(request -> {
                            request.requestMatchers(
                                    "/api/login",
                                    "/api/sign-up",
                                    "/api/tokens/refresh",
                                    "/ws/slamtalk/**",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/favicon.ico"
                            ).permitAll();
                            request.requestMatchers("/api/admin").hasRole(UserRole.ADMIN.toString());
                            request.anyRequest().authenticated();
                        }
                )

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        // JwtFilter를 addFiterBefore로 등록했던 JwtSecurityConfig 클래스도 적용
        return http.build();
    }

}