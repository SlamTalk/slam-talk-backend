package sync.slamtalk.user.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sync.slamtalk.community.repository.CommunityRepository;
import sync.slamtalk.map.repository.BasketballCourtRepository;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.team.repository.TeamMatchingRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.request.UserUpdateNicknameReq;
import sync.slamtalk.user.dto.response.UserDetailsMyInfo;
import sync.slamtalk.user.dto.response.UserDetailsOtherInfo;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.entity.UserRole;
import sync.slamtalk.user.repository.UserAttendanceRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private MatePostRepository matePostRepository;
    @Mock
    private UserAttendanceRepository userAttendanceRepository;
    @Mock
    private TeamMatchingRepository teamMatchingRepository;
    @Mock
    private BasketballCourtRepository basketballCourtRepository;
    @Mock
    private CommunityRepository communityRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserUpdateNicknameReq userUpdateNicknameReq;

    @BeforeEach
    void setUp() {
        userUpdateNicknameReq = new UserUpdateNicknameReq("새로운 닉네임");

        user = User.builder()
                .nickname("닉네임")
                .email("test@test.com")
                .imageUrl("url")
                .role(UserRole.USER)
                .firstLoginCheck(true)
                .build();
    }

    @Test
    @DisplayName("[성공] 나의 마이페이지 조회 - 이메일 까지 반환됨")
    void userDetailsMyInfo() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(teamMatchingRepository.countTeamMatchingByWriter(any())).thenReturn(0L);
        when(teamMatchingRepository.findTeamMatchingByCompleteParticipationCount(any())).thenReturn(0L);
        when(basketballCourtRepository.countBasketballCourtByAdminStatusEqualsAndInformerId(any(), any())).thenReturn(0L);
        when(communityRepository.countAllByUserAndIsDeletedFalse(any())).thenReturn(0L);

        // when
        UserDetailsMyInfo userDetailsMyInfo = userService.userDetailsMyInfo(any());

        // then
        assertThat(userDetailsMyInfo.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("[성공] 다른 사용자 마이페이지")
    void userDetailsOtherInfo() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(teamMatchingRepository.countTeamMatchingByWriter(any())).thenReturn(0L);
        when(teamMatchingRepository.findTeamMatchingByCompleteParticipationCount(any())).thenReturn(0L);
        when(basketballCourtRepository.countBasketballCourtByAdminStatusEqualsAndInformerId(any(), any())).thenReturn(0L);
        when(communityRepository.countAllByUserAndIsDeletedFalse(any())).thenReturn(0L);

        // when
        UserDetailsOtherInfo userDetailsOtherInfo = userService.userDetailsOtherInfo(any());

        // then
        assertThat(userDetailsOtherInfo.getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("[성공] 자기자신 닉네임 업데이트")
    void userUpdateNickname() {
        // when
        userService.userUpdateNickname(user.getId(), userUpdateNicknameReq);

        // then
        verify(userRepository).updateUserNickname(user.getId(), userUpdateNicknameReq.getNickname());
    }

    @Test
    void userUpdatePositionAndSkillLevel() {
    }

    @Test
    void userAttendance() {
    }

    @Test
    void updateUserDetailInfo() {
    }

    @Test
    void userMyScheduleList() {
    }
}