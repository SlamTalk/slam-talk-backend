package sync.slamtalk.user.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.s3bucket.repository.AwsS3RepositoryImpl;
import sync.slamtalk.community.repository.CommunityRepository;
import sync.slamtalk.map.repository.BasketballCourtRepository;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.repository.TeamMatchingRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.request.UpdateUserDetailInfoReq;
import sync.slamtalk.user.dto.request.UserUpdateNicknameReq;
import sync.slamtalk.user.dto.request.UserUpdatePositionAndSkillReq;
import sync.slamtalk.user.dto.response.UserDetailsMyInfo;
import sync.slamtalk.user.dto.response.UserDetailsOtherInfo;
import sync.slamtalk.user.dto.response.UserSchedule;
import sync.slamtalk.user.entity.*;
import sync.slamtalk.user.repository.UserAttendanceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Mock
    private AwsS3RepositoryImpl awsS3Repository;
    @Mock
    private EntityToDtoMapper entityToDtoMapper;


    @InjectMocks
    private UserService userService;

    private User user;
    private UserAttendance userAttendance;
    private UserUpdateNicknameReq userUpdateNicknameReq;
    private UserUpdatePositionAndSkillReq userUpdatePositionAndSkillReq;
    private UpdateUserDetailInfoReq updateUserDetailInfoReq;
    private List<TeamMatching> teamMatchingAllByWriter;
    private List<TeamMatching> teamMatchingAllByApplications;
    private List<MatePost> matePostAllByWriter;
    private List<MatePost> matePostAllByApplications;

    @BeforeEach
    void setUp() {
        userUpdateNicknameReq = new UserUpdateNicknameReq("새로운 닉네임");

        user = User.builder().nickname("닉네임").email("test@test.com").imageUrl("url").role(UserRole.USER).firstLoginCheck(true).userAttendances(new ArrayList<>()).build();

        userUpdatePositionAndSkillReq = new UserUpdatePositionAndSkillReq(UserBasketballSkillLevelType.HIGH, UserBasketballPositionType.UNDEFINED);

        userAttendance = new UserAttendance(user, LocalDate.now());

        updateUserDetailInfoReq = new UpdateUserDetailInfoReq("새로운닉네임", "새로운 자기소개", UserBasketballSkillLevelType.HIGH, UserBasketballPositionType.UNDEFINED);

        teamMatchingAllByWriter = new ArrayList<>();
        teamMatchingAllByApplications = new ArrayList<>();
        matePostAllByWriter = new ArrayList<>();
        matePostAllByApplications = new ArrayList<>();
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
        assertThat(userDetailsMyInfo.getLevelScore()).isEqualTo(10L);
        assertThat(userDetailsMyInfo.getMateCompleteParticipationCount()).isZero();
        assertThat(userDetailsMyInfo.getTeamMatchingCompleteParticipationCount()).isZero();
        assertThat(userDetailsMyInfo.getLevel()).isEqualTo(1L);
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
        assertThat(userDetailsOtherInfo.getLevelScore()).isEqualTo(10L);
        assertThat(userDetailsOtherInfo.getMateCompleteParticipationCount()).isZero();
        assertThat(userDetailsOtherInfo.getTeamMatchingCompleteParticipationCount()).isZero();
        assertThat(userDetailsOtherInfo.getLevel()).isEqualTo(1L);
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
    @DisplayName("[성공] 유저 포지션 및 스킬 레벨 업데이트")
    void userUpdatePositionAndSkillLevel() {

        //when
        userService.userUpdatePositionAndSkillLevel(user.getId(), userUpdatePositionAndSkillReq);

        //then
        verify(userRepository, times(1)).updateUserPositionAndSkillLevel(user.getId(), userUpdatePositionAndSkillReq.getBasketballSkillLevel(), userUpdatePositionAndSkillReq.getBasketballPosition());
    }

    @Test
    @DisplayName("[성공] 유저 출석체크 완료")
    void userAttendance() {

        //given
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userAttendanceRepository.save(any())).thenReturn(userAttendance);

        //when
        userService.userAttendance(user.getId());

        //then
        verify(userAttendanceRepository, times(1)).save(any(UserAttendance.class));
    }

    @Test
    @DisplayName("[성공] 유저 마이페이지 업데이트")
    void updateUserDetailInfo() {

        //given
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(awsS3Repository.uploadFile(any())).thenReturn("fileURL");

        //when
        userService.updateUserDetailInfo(user.getId(), file, updateUserDetailInfoReq);

        //then
        // 닉네임, 자기소개, 등급, 타입
        verify(awsS3Repository, times(1)).uploadFile(any(MultipartFile.class));
        assertThat(user.getNickname()).isEqualTo(updateUserDetailInfoReq.getNickname());
        assertThat(user.getSelfIntroduction()).isEqualTo(updateUserDetailInfoReq.getSelfIntroduction());
        assertThat(user.getBasketballPosition()).isEqualTo(updateUserDetailInfoReq.getBasketballPosition());
        assertThat(user.getBasketballSkillLevel()).isEqualTo(updateUserDetailInfoReq.getBasketballSkillLevel());
    }

    @Test
    @DisplayName("[성공]유저 스케줄 리스트 반환")
    void userMyScheduleList() {
        //given
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(teamMatchingRepository.findAllByWriterAndIsDeletedFalse(user)).thenReturn(teamMatchingAllByWriter);
        when(teamMatchingRepository.findAllByApplicationId(user.getId())).thenReturn(teamMatchingAllByApplications);
        when(matePostRepository.findAllByWriterAndIsDeletedFalse(user)).thenReturn(matePostAllByWriter);
        when(matePostRepository.findAllByApplicationId(user.getId())).thenReturn(matePostAllByApplications);

        //when
        UserSchedule userSchedule = userService.userMyScheduleList(user.getId());

        assertThat(userSchedule.getMateList()).isEmpty();
        assertThat(userSchedule.getTeamMatchingList()).isEmpty();
    }
}