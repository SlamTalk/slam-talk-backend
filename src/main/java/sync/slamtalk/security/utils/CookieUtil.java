package sync.slamtalk.security.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

@Slf4j
public class CookieUtil {

    private CookieUtil() {
    }

    /**
     * 특정 이름을 가진 쿠키 검색하는 메서드
     * @param request
     * @param name : 쿠키이름
     * @return Optional<Cookie>
     * */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 새로운 쿠키를 생성하고 HTTP 응답에 추가하는 메서드
     * @param response
     * @param name
     * @param value
     * @param maxAge
     * @param domain
     * */
    public static void addCookie(
            HttpServletResponse response,
            String name,
            String value,
            int maxAge,
            String domain
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, value) // 쿠키 이름과 값 설정
                .path("/") // 쿠키 전체 도메인으로 경로 설정
                .sameSite("None") // 쿠키가 같은 사이트 요청뿐만 아니라 크로스-사이트 요청에서도 전송될 수 있음
                .httpOnly(true) // XSS 공격으로부터 쿠키를 보호
                .secure(true) // HTTPS를 통해서만 전송
                .domain(domain) // 쿠키가 유효한 도메인을 지정
                .maxAge(maxAge) // 초단위로 쿠키 만료 지정
                .build();

        log.debug("cookie 값 설정 ={}", cookie.toString());

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * 요청된 쿠키를 찾아 삭제하는 메서드
     * @param request
     * @param response
     * @param name
     * @param domain
     * */
    public static void deleteCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String name,
            String domain
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    ResponseCookie rcookie = ResponseCookie.from(name, "")
                            .path("/")
                            .sameSite("Lax")
                            .httpOnly(true)
                            .secure(true)
                            .domain(domain)
                            .maxAge(0)
                            .build();

                     response.addHeader("Set-Cookie", rcookie.toString());
                }
            }
        }
    }
}