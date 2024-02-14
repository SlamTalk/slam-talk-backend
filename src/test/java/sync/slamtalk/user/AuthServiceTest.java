package sync.slamtalk.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.user.dto.request.UserLoginReq;
import sync.slamtalk.user.dto.request.UserSignUpReq;
import sync.slamtalk.user.service.AuthService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Slf4j
@SpringBootTest
class AuthServiceTest {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public AuthService authService;
    @Autowired
    public JwtTokenProvider jwtTokenProvider;

    public String email = "test1@naver.com";
    public String password = "123@password!";
    public String nickname = "nickname";
    public UserLoginReq userLoginReq = new UserLoginReq(email, password);

    @DisplayName("회원가입")
    @Test
    void signUp() {
        //given
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserSignUpReq userSignUpReq = new UserSignUpReq(email, password, nickname);

        // when
        authService.signUp(userSignUpReq, response);

    }

    @DisplayName("로그인")
    @Test
    void login() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        authService.login(userLoginReq, response);

        // then

    }


/*    @DisplayName("토큰 재발급")
    @Test
    @Disabled
    void refreshToken() {
        // given
        // 모의 객체 생성
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // 테스트에 사용할 쿠키 생성 및 설정
        User user = userRepository.findByEmailAndSocialType(this.email, SocialType.LOCAL)
                .orElseThrow(() -> new RuntimeException("유저를 찾지 못함"));

        Cookie[] cookies = new Cookie[]{
                new Cookie("Refresh_Authorization", user.getRefreshToken())
        };
        Mockito.when(request.getCookies()).thenReturn(cookies);

    }*/
}