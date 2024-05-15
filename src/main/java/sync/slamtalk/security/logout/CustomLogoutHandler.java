package sync.slamtalk.security.logout;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.security.utils.CookieUtil;
import sync.slamtalk.user.entity.User;

@Slf4j
@Component
public class CustomLogoutHandler implements LogoutHandler {
    @Value("${jwt.refresh.header}")
    public String refreshAuthorizationCookieName;
    @Value("${jwt.domain}")
    private String domain;

    @Override
    @Transactional
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        log.debug("CustomLogoutHandler 동작");

        CookieUtil.deleteCookie(request, response, refreshAuthorizationCookieName, domain);
        SecurityContextHolder.clearContext();
    }
}
