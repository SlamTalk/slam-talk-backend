package sync.slamtalk.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.s3bucket.repository.AwsS3RepositoryImpl;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.UpdateUserDetailInfoRequestDto;
import sync.slamtalk.user.dto.UserDetailsInfoResponseDto;
import sync.slamtalk.user.dto.UserUpdateNicknameRequestDto;
import sync.slamtalk.user.dto.UserUpdatePositionAndSkillRequestDto;
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
    private final MatePostRepository matePostRepository;
    private final AwsS3RepositoryImpl awsS3Service;

    /**
     * 유저의 마이페이지 보기 조회시 사용되는 서비스
     * @param userId 찾고자하는 userId
     * @param loginUserId  로그인한 userId
     *
     * */
    public UserDetailsInfoResponseDto userDetailsInfo(
            Long userId,
            Long loginUserId
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));

        // 레벨 score 계산하기
        long levelScore = 0L;

        // Mate 게시판 상태가 Complete
        long mateCompleteParticipationCount = matePostRepository.findMateCompleteParticipationCount(userId);
        levelScore += mateCompleteParticipationCount * User.MATE_LEVEL_SCORE;

        // todo : teamMatchingCompleteParticipationCount 팀매칭이 완료된 경우의 개수 세기

        Long userAttendCount = userAttendanceRepository.countUserAttendancesByUser(user)
                .orElse(0L);
        levelScore += userAttendCount * User.ATTEND_SCORE;

        // 찾고자 하는 유저가 본인일 경우(상세한 개인정보 까지 공개)
        if(loginUserId.equals(user.getId())){
            return UserDetailsInfoResponseDto.generateMyProfile(
                    user,
                    levelScore,
                    mateCompleteParticipationCount
            );
        }

        // 찾고자 하는 유저가 본인이 아닐경우(개인정보 제외하고 공개)
        else return UserDetailsInfoResponseDto.generateOtherUserProfile(
                user,
                levelScore,
                mateCompleteParticipationCount
        );
    }

    /**
     * 유저 닉네임 변경 로직
     *
     * @param userId 유저아이디,
     * @param userUpdateNicknameRequestDto 유저 닉네임 변경 request dto
     * */
    @Transactional
    public void userUpdateNickname(
            Long userId,
            UserUpdateNicknameRequestDto userUpdateNicknameRequestDto
    ) {
        log.debug("유저 아이디 "+ userId);
        checkNicknameExistence(userUpdateNicknameRequestDto.getNickname());
        userRepository.updateUserNickname(userId, userUpdateNicknameRequestDto.getNickname());
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
     * @param userUpdatePositionAndSkillRequestDto 유저 레벨타입과, 농구포지션으로 요청이온 dto
     * */
    @Transactional
    public void userUpdatePositionAndSkillLevel(
            Long userId,
            UserUpdatePositionAndSkillRequestDto userUpdatePositionAndSkillRequestDto
    ) {
        userRepository.updateUserPositionAndSkillLevel(
                userId,
                userUpdatePositionAndSkillRequestDto.getBasketballSkillLevel(),
                userUpdatePositionAndSkillRequestDto.getBasketballPosition()
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
     * @param updateUserDetailInfoRequestDto UpdateUserDetailInfoRequestDto
     * */
    @Transactional
    public void updateUserDetailInfo(
            Long userId,
            MultipartFile file,
            UpdateUserDetailInfoRequestDto updateUserDetailInfoRequestDto
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));
        // 닉네임 검증
        if(updateUserDetailInfoRequestDto.getNickname() != null) {
            checkNicknameExistence(updateUserDetailInfoRequestDto.getNickname());
            user.updateNickname(updateUserDetailInfoRequestDto.getNickname());
        }

        // 이미지 파일이 존재한다면 업데이트
        if(file != null) {
            String fileUrl = awsS3Service.uploadFile(file);
            user.updateProfileUrl(fileUrl);
        }

        // 자기 소개 한마디
        if(updateUserDetailInfoRequestDto.getSelfIntroduction() != null){
            user.updateSelfIntroduction(updateUserDetailInfoRequestDto.getSelfIntroduction());
        }

        // 유저 포지션
        if(updateUserDetailInfoRequestDto.getBasketballPosition() != null){
            user.updatePosition(updateUserDetailInfoRequestDto.getBasketballPosition());
        }

        // 유저 스킬 레벨 업데이트
        if(updateUserDetailInfoRequestDto.getBasketballSkillLevel() != null){
            user.updateBasketballSkillLevel(updateUserDetailInfoRequestDto.getBasketballSkillLevel());
        }
    }
}