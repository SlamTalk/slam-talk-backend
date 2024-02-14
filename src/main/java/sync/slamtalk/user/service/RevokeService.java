package sync.slamtalk.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;

@Slf4j
@RequiredArgsConstructor
@Service
public class RevokeService {
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.naver.clientId}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.clientSecret}")
    private String naverClientSecret;

    public void deleteGoogleAccount(
            User user,
            String accessToken
    ) {
        ;
        // 유저 계정 삭제 처리

        String data = "token=" + user.getSocialId();

        sendRevokeRequest(data, SocialType.GOOGLE, accessToken);
    }

    public void deleteNaverAccount(
            User user,
            String accessToken
    ) {
        // 유저 계정 삭제 처리

        String data = "client_id=" + naverClientId +
                "&client_secret=" + naverClientSecret +
                "&access_token=" + user.getSocialId() +
                "&service_provider=NAVER" +
                "&grant_type=delete";

        sendRevokeRequest(data, SocialType.NAVER, null);
    }

    public void deleteKakaoAccount(
            User user,
            String accessToken
    ) {
        sendRevokeRequest(null, SocialType.KAKAO, accessToken);
    }

    /**
     * @param data        : revoke request의 body에 들어갈 데이터
     * @param socialType  : oauth2 업체
     * @param accessToken : 카카오의 경우 url이 아니라 헤더에 access token을 첨부해서 보내줘야 함
     */
    private void sendRevokeRequest(
            String data,
            SocialType socialType,
            String accessToken
    ) {
        String googleRevokeUrl = "https://accounts.google.com/o/oauth2/revoke";
        String naverRevokeUrl = "https://nid.naver.com/oauth2.0/token";
        String kakaoRevokeUrl = "https://kapi.kakao.com/v1/user/unlink";

        RestTemplate restTemplate = new RestTemplate();
        String revokeUrl = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(data, headers);

        switch (socialType) {
            case GOOGLE -> revokeUrl = googleRevokeUrl;
            case NAVER -> revokeUrl = naverRevokeUrl;
            case KAKAO -> {
                revokeUrl = kakaoRevokeUrl;
                headers.setBearerAuth(accessToken);
            }
        }

        restTemplate.exchange(revokeUrl, HttpMethod.POST, entity, String.class);
    }
}