package sync.slamtalk.security.oauth2.userinfo;

import java.util.Map;

/**
 * 소셜 타입별 유저 정보를 가지는 Oauth2UserInfo 추상클래스
 * */
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId(); //소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"

    public abstract String getNickname();

    public abstract String getImageUrl();

    public abstract String getEmail();
}
