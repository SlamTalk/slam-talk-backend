package sync.slamtalk.security.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {
    public static final List<String> EXCLUDE_URLS = List.of(
            "/api/login",
            "/api/sign-up",
            "/api/tokens/refresh",
            "/api/logout",
            "/ws/slamtalk",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/favicon.ico",
            "/api/map/courts",
            "/api/send-mail",
            "/api/mail-check",
            "/api/user/temporary-passwords",
            "/api/mate/read",
            "/api/mate/list",
            "/api/match/read",
            "/api/match/list",
            "/api/test/sign-up"
    );
}
