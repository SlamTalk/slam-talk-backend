package sync.slamtalk.user.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.UserDetailsMyInfoResponseDto;
import sync.slamtalk.user.dto.UserSignUpRequestDto;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.repository.UserAttendanceRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MatePostRepository matePostRepository;
    @Mock
    private UserAttendanceRepository userAttendanceRepository;

    @InjectMocks
    private UserService userService;

    private Long loginUserId = 1L;
    private String email = "test@naver.com";
    private String password = "password";
    private String nickname = "하이";

    @Test
    @DisplayName("남의 페이지 마이페이지 조회하는 api 테스트")
    @Disabled
    void testGenerateNickname() {
        // given
        User user = new UserSignUpRequestDto(email, password, nickname).toEntity();

        //when
        Mockito.when(userRepository.findById(loginUserId))
                .thenReturn(Optional.of(user));
        Mockito.when(matePostRepository.findMateCompleteParticipationCount(loginUserId))
                .thenReturn(1L);
        Mockito.when(userAttendanceRepository.countUserAttendancesByUser(user))
                .thenReturn(Optional.of(100L));
//        UserDetailsMyInfoResponseDto userDetailsMyInfoResponseDto = userService.userDetailsInfo(this.loginUserId, this.loginUserId);
//
//        // then
//        assertThat(userDetailsMyInfoResponseDto.getNickname()).isEqualTo(nickname);
//        assertThat(userDetailsMyInfoResponseDto.getMateCompleteParticipationCount()).isEqualTo(1L);
//        assertThat(userDetailsMyInfoResponseDto.getLevel()).isEqualTo(2L);
//        assertThat(userDetailsMyInfoResponseDto.getLevelScore()).isEqualTo(105L);
    }


}