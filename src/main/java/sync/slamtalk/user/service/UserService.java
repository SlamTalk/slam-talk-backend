package sync.slamtalk.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.s3bucket.repository.AwsS3RepositoryImpl;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.team.repository.TeamMatchingRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.request.UpdateUserDetailInfoReq;
import sync.slamtalk.user.dto.request.UserUpdateNicknameReq;
import sync.slamtalk.user.dto.request.UserUpdatePositionAndSkillReq;
import sync.slamtalk.user.dto.response.UserDetailsMyInfo;
import sync.slamtalk.user.dto.response.UserDetailsOtherInfo;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.entity.UserAttendance;
import sync.slamtalk.user.error.UserErrorResponseCode;
import sync.slamtalk.user.repository.UserAttendanceRepository;

import java.time.LocalDate;

/**
 * 이 서비스는 유저의 crud 와 관련된 클래스입니다.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAttendanceRepository userAttendanceRepository;
    private final TeamMatchingRepository teamMatchingRepository;
    private final MatePostRepository matePostRepository;
    private final AwsS3RepositoryImpl awsS3Service;

    /**
     * 유저의 마이페이지 보기 조회시 사용되는 서비스
     * @param userId 찾고자하는 userId
     *
     * */
    public UserDetailsMyInfo userDetailsMyInfo(
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));

        // 레벨 score 계산하기
        long levelScore = 0L;

        // Mate 게시판 상태가 Complete
        long mateCount = getMateCount(userId, user);
        levelScore += mateCount * User.MATE_LEVEL_SCORE;

        // teamMatching 게시판 상태가 Complete
        long teamCount = getTeamCount(userId, user);
        levelScore += teamCount * User.TEAM_MATCHING_LEVEL_SCORE;

        // user 출석개수 반환
        long userAttendCount = userAttendanceRepository.countUserAttendancesByUser(user)
                .orElse(0L);
        levelScore += userAttendCount * User.ATTEND_SCORE;

        // 찾고자 하는 유저가 본인이 아닐경우(개인정보 제외하고 공개)
        return UserDetailsMyInfo.generateMyProfile(
                user,
                levelScore,
                mateCount,
                teamCount
        );
    }

    /**
     * 유저의 마이페이지 보기 조회시 사용되는 서비스
     *
     * @param userId 찾고자하는 userId
     */
    public UserDetailsOtherInfo userDetailsOtherInfo(
            Long userId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));

        // 레벨 score 계산하기
        long levelScore = 0L;

        // Mate 게시판 상태가 Complete
        long mateCount = getMateCount(userId, user);
        levelScore += mateCount * User.MATE_LEVEL_SCORE;

        // teamMatching 게시판 상태가 Complete
        long teamCount = getTeamCount(userId, user);
        levelScore += teamCount * User.TEAM_MATCHING_LEVEL_SCORE;

        // user 출석개수 반환
        long userAttendCount = userAttendanceRepository.countUserAttendancesByUser(user)
                .orElse(0L);
        levelScore += userAttendCount * User.ATTEND_SCORE;

        // 찾고자 하는 유저가 본인이 아닐경우(개인정보 제외하고 공개)
         return UserDetailsOtherInfo.generateOtherUserProfile(
                user,
                levelScore,
                mateCount,
                 teamCount
        );
    }

    /**
     * 팀매칭 완료된 곳에 참여한 개수 구하는 메서드
     * @param userId : 유저 아이디
     * @param user : 유저 객체
     *
     * @return count : 개수
     * */
    private long getTeamCount(Long userId, User user) {
        long count = 0L;
        count += teamMatchingRepository.countTeamMatchingByWriter(user);
        count += teamMatchingRepository.findTeamMatchingByCompleteParticipationCount(userId);
        return count;
    }

    /**
     * Mate매칭 완료된 곳에 참여한 개수 구하는 메서드
     * @param userId : 유저 아이디
     * @param user : 유저 객체
     *
     * @return count : 개수
     * */
    private long getMateCount(Long userId, User user) {
        long count = 0L;
        count += matePostRepository.findMateCompleteParticipationCount(userId);
        count += matePostRepository.countMatePostByWriter(user);
        return count;
    }

    /**
     * 유저 닉네임 변경 로직
     *
     * @param userId 유저아이디,
     * @param userUpdateNicknameReq 유저 닉네임 변경 request dto
     * */
    @Transactional
    public void userUpdateNickname(
            Long userId,
            UserUpdateNicknameReq userUpdateNicknameReq
    ) {
        log.debug("유저 아이디 "+ userId);
        checkNicknameExistence(userUpdateNicknameReq.getNickname());
        userRepository.updateUserNickname(userId, userUpdateNicknameReq.getNickname());
    }

    /**
     * 회원가입 시 중복 닉네임이 존재하는지 검사하는 메서드
     *
     * @param  nickname 유저 닉네임
     * */

    private void checkNicknameExistence(String nickname) {
        String lowercaseNickname = nickname.toLowerCase();
        if (userRepository.findByNickname(lowercaseNickname).isPresent()) {
            log.debug("이미 존재하는 닉네임입니다.");
            throw new BaseException(UserErrorResponseCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     *  유저의 스킬 레벨 타입과, 농구 포지션을 업데이트하는 서비스
     *
     * @param userId 유저 아이디
     * @param userUpdatePositionAndSkillReq 유저 레벨타입과, 농구포지션으로 요청이온 dto
     * */
    @Transactional
    public void userUpdatePositionAndSkillLevel(
            Long userId,
            UserUpdatePositionAndSkillReq userUpdatePositionAndSkillReq
    ) {
        userRepository.updateUserPositionAndSkillLevel(
                userId,
                userUpdatePositionAndSkillReq.getBasketballSkillLevel(),
                userUpdatePositionAndSkillReq.getBasketballPosition()
        );
    }

    /**
     * 유저 출석체크를 위한 api
     *
     * @param userId 유저 아이디
     * */
    @Transactional
    public void userAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));

        // 만약 이미 출석을 했다면 400 에러 반환
        if(userAttendanceRepository.existsByUserAndAttDate(user, LocalDate.now())){
            throw new BaseException(UserErrorResponseCode.ATTENDANCE_ALREADY_EXISTS);
        }

        UserAttendance saveAttendance = userAttendanceRepository.save(new UserAttendance(user, LocalDate.now()));
        saveAttendance.addUser(user);
    }

    /**
     * 유저 마이페이지 수정 api
     * @param userId 유저아이디
     * @param file MultipartFile 업로드
     * @param updateUserDetailInfoReq UpdateUserDetailInfoRequestDto
     * */
    @Transactional
    public void updateUserDetailInfo(
            Long userId,
            MultipartFile file,
            UpdateUserDetailInfoReq updateUserDetailInfoReq
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));
        // 닉네임 검증
        if(updateUserDetailInfoReq.getNickname() != null) {
            checkNicknameExistence(updateUserDetailInfoReq.getNickname());
            user.updateNickname(updateUserDetailInfoReq.getNickname());
        }

        // 이미지 파일이 존재한다면 업데이트
        if(file != null) {
            String fileUrl = awsS3Service.uploadFile(file);
            user.updateProfileUrl(fileUrl);
        }

        // 자기 소개 한마디
        if(updateUserDetailInfoReq.getSelfIntroduction() != null){
            user.updateSelfIntroduction(updateUserDetailInfoReq.getSelfIntroduction());
        }

        // 유저 포지션
        if(updateUserDetailInfoReq.getBasketballPosition() != null){
            user.updatePosition(updateUserDetailInfoReq.getBasketballPosition());
        }

        // 유저 스킬 레벨 업데이트
        if(updateUserDetailInfoReq.getBasketballSkillLevel() != null){
            user.updateBasketballSkillLevel(updateUserDetailInfoReq.getBasketballSkillLevel());
        }
    }
}