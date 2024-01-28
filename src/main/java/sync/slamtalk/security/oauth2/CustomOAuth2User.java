package sync.slamtalk.security.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.UserRole;

import java.util.Collection;
import java.util.Map;

/**
 * DefaultOAuth2User를 상속하고, email과 role 필드를 추가로 가진다.
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private UserRole userRole;
    private SocialType socialType;

    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     *
     * @param authorities      the authorities granted to the user
     * @param attributes       the attributes about the user
     * @param nameAttributeKey the key used to access the user's &quot;name&quot; from
     *                         {@link #getAttributes()}
     */
    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            String email,
            UserRole userRole,
            SocialType socialType
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.userRole = userRole;
        this.socialType = socialType;
    }
}
