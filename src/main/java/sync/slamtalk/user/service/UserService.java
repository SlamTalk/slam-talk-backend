package sync.slamtalk.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.UserDetailsInfoResponseDto;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;

/**
 * 이 서비스는 유저의 crud 와 관련된 클래스입니다.
 * */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    /**
     * 유저의 마이페이지 보기 조회시 사용되는 서비스
     *
     *
     * */
    public UserDetailsInfoResponseDto userDetailsInfo(Long userId, User user) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));


        // 찾고자 하는 유저가 본인일 경우(상세한 개인정보 까지 공개)
        if(user.getId().equals(userId)) return UserDetailsInfoResponseDto.generateMyProfile(user);
        // 찾고자 하는 유저가 본인이 아닐경우(개인정보 제외하고 공개)
        else return UserDetailsInfoResponseDto.generateOtherUserProfile(user);
    }
}
