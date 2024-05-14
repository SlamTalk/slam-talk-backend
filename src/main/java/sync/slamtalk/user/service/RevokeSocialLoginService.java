package sync.slamtalk.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;


@Slf4j
@RequiredArgsConstructor
@Service
public class RevokeSocialLoginService {

    public static final String googleRevokeUrl = "https://accounts.google.com/o/oauth2/revoke";
    public static final String naverRevokeUrl = "https://nid.naver.com/oauth2.0/token";
    public static final String kakaoRevokeUrl = "https://kapi.kakao.com/v1/user/unlink";

    @Value("${spring.security.oauth2.client.registration.naver.clientId}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.clientSecret}")
    private String naverClientSecret;

    /**
     * 사용자의 Google 계정을 삭제합니다.
     *
     * 이 메서드는 사용자의 OAuth2 액세스 토큰을 사용하여 Google 계정 삭제 요청을 보냅니다.
     * 계정 삭제 요청은 {@link #sendRevokeRequest(String, SocialType, Callback)} 메서드를 통해 전송됩니다.
     *
     * @param user 삭제할 Google 계정의 사용자 정보를 담고 있는 {@link User} 객체입니다.
     *             필요한 정보: OAuth2 액세스 토큰.
     */
    public void deleteGoogleAccount(User user) {
        // 사용자의 OAuth2 액세스 토큰을 이용해 삭제 요청 데이터를 구성합니다.
        String data = "token=" + user.getOauth2AccessToken();

        // 실제로 Google 계정 삭제 요청을 보내는 메서드를 호출합니다.
        sendRevokeRequest(data, SocialType.GOOGLE, null);
    }

    /**
     * 사용자의 Naver 계정을 삭제합니다.
     *
     * 이 메서드는 사용자의 OAuth2 액세스 토큰과 Naver 클라이언트 ID, 클라이언트 비밀을 사용하여 Naver 계정 삭제 요청을 보냅니다.
     * 계정 삭제 요청은 {@link #sendRevokeRequest(String, SocialType, Callback)} 메서드를 통해 전송됩니다.
     *
     * @param user 삭제할 Naver 계정의 사용자 정보를 담고 있는 {@link User} 객체입니다.
     *             필요한 정보: OAuth2 액세스 토큰.
     */
    public void deleteNaverAccount(User user) {
        // 사용자의 OAuth2 액세스 토큰과 Naver 클라이언트 ID, 클라이언트 비밀을 이용해 삭제 요청 데이터를 구성합니다.
        String data = "client_id=" + naverClientId +
                "&client_secret=" + naverClientSecret +
                "&access_token=Bearer " + user.getOauth2AccessToken() +
                "&grant_type=delete";

        // 실제로 Naver 계정 삭제 요청을 보내는 메서드를 호출합니다.
        sendRevokeRequest(data, SocialType.NAVER, null);
    }

    /**
     * 사용자의 카카오 계정과 관련된 OAuth2 액세스 토큰을 사용하여 카카오 로그인 연동 해제를 요청하는 메소드입니다.
     * 이 메소드는 사용자가 소셜 로그인 연동을 해제하고자 할 때 호출되며, 카카오 계정의 연동 해제를 위한 HTTP 요청을 전송합니다.
     * 내부적으로 `sendRevokeRequest` 메소드를 호출하여 실제 연동 해제 요청을 처리합니다.
     *
     * @param user 연동 해제를 요청하는 사용자 객체입니다. 이 객체에서 카카오 OAuth2 액세스 토큰을 추출하여 사용합니다.
     *             사용자 객체는 null이 아니어야 하며, 유효한 카카오 OAuth2 액세스 토큰을 포함하고 있어야 합니다.
     *             만약 사용자 객체가 null이거나 유효하지 않은 액세스 토큰을 포함하고 있을 경우, 연동 해제 요청은 실패할 것입니다.
     *
     * @return 이 메소드는 반환값이 없습니다(void).
     */
    public void deleteKakaoAccount(User user) {

        sendRevokeRequest(null, SocialType.KAKAO, user.getOauth2AccessToken());
    }


    /**
     * 소셜 미디어 계정 연결 해제를 위한 요청을 전송하는 메서드입니다.
     * Google, Naver, Kakao 등 지원하는 소셜 미디어 플랫폼에 따라 적절한 API를 호출하여 사용자의 소셜 미디어 계정과의 연결을 해제합니다.
     * 이 메서드는 HTTP POST 요청을 사용하여 소셜 미디어 서비스의 연결 해제(endpoints)에 요청을 보냅니다.
     * 요청이 성공하면, 해당 소셜 미디어 계정과의 연결이 해제되며, 실패할 경우 사용자는 다시 로그인해야 할 수 있습니다.
     *
     * @param data 전송할 데이터입니다. 이 데이터는 소셜 미디어 플랫폼에 따라 다를 수 있으며,
     *             일반적으로 사용자 식별 정보나 토큰 취소 요청에 필요한 정보를 포함합니다.
     * @param socialType 소셜 미디어의 종류를 나타내는 {@code SocialType} 열거형입니다.
     *                   GOOGLE, NAVER, KAKAO 중 하나를 지정할 수 있습니다.
     * @param accessToken 사용자의 액세스 토큰입니다. Kakao의 경우, Bearer 토큰 인증 방식으로 사용되어
     *                    요청 헤더에 추가됩니다.
     *
     * 이 메서드는 반환 값이 없습니다(void). 하지만 내부적으로 요청의 성공 여부를 확인하고,
     * 요청이 실패하면 BaseException을 던집니다. 이는 요청 처리 중 발생한 문제를 호출자에게 알리는 방법입니다.
     * 성공적으로 요청이 처리되면, debug 로그를 통해 응답 상태 코드와 응답 본문을 기록합니다.
     *
     * 주의: 이 메서드는 private 접근 제한자를 사용하여 클래스 내부에서만 사용할 수 있도록 설계되었습니다.
     * 외부에서 직접 호출할 수 없으며, 클래스 내 다른 메서드를 통해 간접적으로 사용됩니다.
     */
    private void sendRevokeRequest(
            String data,
            SocialType socialType,
            String accessToken
    ) {

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
            default -> throw new BaseException(UserErrorResponseCode.SOCIAL_TYPE_DOESNT_EXIST);
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(revokeUrl, HttpMethod.POST, entity, String.class);

        // 응답 상태 코드와 본문을 가져옵니다.
        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();

        if (!statusCode.equals(HttpStatus.OK)) throw new BaseException(UserErrorResponseCode.NEED_TO_LOG_IN_AGAIN);

        log.debug("소셜 회원 연결 해체 요청 결과, Status Code: " + statusCode + ", Response : " + responseBody);

    }

}
