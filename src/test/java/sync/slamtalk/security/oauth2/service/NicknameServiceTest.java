package sync.slamtalk.security.oauth2.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.service.NicknameService;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class NicknameServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NicknameService nicknameService;

    @Test
    @DisplayName("공백 제거 테스트")
    void testGenerateNickname() {
        String nickname = "테스트닉네임";
        Mockito.when(userRepository.findByNickname(nickname))
                .thenReturn(Optional.empty());

        // when
        String generatedNickname = nicknameService.createAvailableNickname(nickname);

        // then
        assertEquals(generatedNickname, nickname);
    }

    @Test
    @DisplayName("존재하는 유저가 있을 때 새로운 닉네임 생성되는지 테스트")
    void testAlreadyExistNicknameTest() {
        String userEmail = "test@naver.com";
        String password = "123";
        String nickname = "hello";

        User user = User.of(userEmail, password, nickname);

        Mockito.when(userRepository.findByNickname(nickname))
                .thenReturn(Optional.of(user));

        // when
        String generatedNickname = nicknameService.createAvailableNickname(nickname);

        assertNotEquals(generatedNickname, nickname); // 여기서 generatedNickname의 닉네임을 로그로 찍고 익명이라는 단어가 contains 되는지 확인하는 코드를 작성하고 싶어
        System.out.println(generatedNickname);

        assertTrue(generatedNickname.contains("익명"), "생성된 닉네임에 '익명'이 포함되어 있지 않습니다.");
    }

    @Test
    @DisplayName("닉네임이 13자 이상일 경우 익명으로 생성하는 테스트")
    void nicknameLongTest() {
        Mockito.when(userRepository.findByNickname(Mockito.any()))
                .thenReturn(Optional.empty());

        // when
        String nickname = "0123456789123";
        String generatedNickname = nicknameService.createAvailableNickname(nickname);

        // then
        assertNotEquals(generatedNickname, nickname);
        System.out.println(generatedNickname);

        assertTrue(generatedNickname.contains("익명"), "생성된 닉네임에 '익명'이 포함되어 있지 않습니다.");
    }

    @Test
    @DisplayName("기존 닉네임이 존재해서 새로 생성했는데 중복이 되어서 2번째로 생성하는 테스트")
    void secondNewNicknameTest() {
        // given
        String userEmail = "test@naver.com";
        String password = "123";
        String nickname = "hello";

        User user = User.of(userEmail, password, nickname);

        AtomicInteger counter = new AtomicInteger(0);

        // when
        Mockito.when(userRepository.findByNickname(Mockito.any()))
                .thenAnswer(invocation -> {
                    if (counter.getAndIncrement() < 2) {
                        return Optional.of(user);
                    } else {
                        return Optional.empty();
                    }
                });

        String generatedNickname = nicknameService.createAvailableNickname(nickname);


        // then
        assertNotEquals(generatedNickname, nickname);
        System.out.println(generatedNickname);

        assertTrue(generatedNickname.contains("익명"), "생성된 닉네임에 '익명'이 포함되어 있지 않습니다.");
    }
}