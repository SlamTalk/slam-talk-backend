package sync.slamtalk.user;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import sync.slamtalk.security.jwt.JwtTokenProvider;
import sync.slamtalk.user.dto.UserLoginRequestDto;
import sync.slamtalk.user.dto.UserLoginResponseDto;
import sync.slamtalk.user.dto.UserSignUpRequestDto;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserService userService;
    @Autowired
    public JwtTokenProvider jwtTokenProvider;

    public String email = "test1@naver.com";
    public String password = "123@password!";
    public String nickname = "nickname";
    public UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto(email, password);

    @DisplayName("회원가입")
    @Test
    void signUp() {
        //given
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserSignUpRequestDto userSignUpRequestDto = new UserSignUpRequestDto(email, password, nickname);

        // when
        UserLoginResponseDto userLoginResponseDto = userService.signUp(userSignUpRequestDto, response);

        // then
        assertTrue(userLoginResponseDto.getFirstLoginCheck());
    }

    @DisplayName("로그인")
    @Test
    void login() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        UserLoginResponseDto userLoginResponseDto = userService.login(userLoginRequestDto, response);

        // then
        assertFalse(userLoginResponseDto.getFirstLoginCheck());
    }


    @DisplayName("토큰 재발급")
    @Test
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

        // when
        UserLoginResponseDto userLoginResponseDto = userService.refreshToken(request, response);

        // then
        assertFalse(userLoginResponseDto.getFirstLoginCheck());
    }
}