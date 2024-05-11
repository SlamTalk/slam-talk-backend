package sync.slamtalk.security.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.filter.CorsFilter;
import sync.slamtalk.security.jwt.JwtAccessDeniedHandler;
import sync.slamtalk.security.jwt.JwtAuthenticationEntryPoint;
import sync.slamtalk.security.jwt.JwtFilter;
import sync.slamtalk.security.logout.CustomLogoutHandler;
import sync.slamtalk.security.oauth2.handler.OAuth2LoginFailureHandler;
import sync.slamtalk.security.oauth2.handler.OAuth2LoginSuccessHandler;
import sync.slamtalk.security.oauth2.service.CustomOAuth2UserService;
import sync.slamtalk.user.entity.UserRole;

@Configuration
@EnableWebSecurity // Web 보안 활성화
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLogoutHandler customLogoutHandler;

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
                .formLogin(AbstractHttpConfigurer::disable)

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
                                    "/api/test/sign-up",
                                    "/api/tokens/refresh",
                                    "/ws/slamtalk/**",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**",
                                    "/favicon.ico",
                                    "/api/send-mail",
                                    "/api/mail-check",
                                    "/api/user/password",
                                    "/api/user/temporary-passwords"
                            ).permitAll();

                            // 게스트 권환 설정
                            request.requestMatchers(HttpMethod.GET,
                                    "/api/map/courts/**",
                                    "/api/mate/read/**",
                                    "/api/mate/list",
                                    "/api/match/read/**",
                                    "/api/match/list",
                                    "/api/community/category/**",
                                    "/api/community/board/**",
                                    "/api/user/other-info/**"
                                    ).permitAll();
                            request.requestMatchers("/api/admin/**").hasAnyAuthority(UserRole.ADMIN.getKey());
                            request.anyRequest().authenticated();
                        }
                )

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout") // post mapping
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value())
                        )
                        .deleteCookies("JSESSIONID")
                )
                .addFilterBefore(jwtFilter, LogoutFilter.class);
        // JwtFilter를 addFiterBefore로 등록했던 JwtSecurityConfig 클래스도 적용
        return http.build();
    }

}