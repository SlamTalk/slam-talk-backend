package sync.slamtalk.user.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.request.UserSignUpReq;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private final String email = "test@test.com";
    private final String nickname = "test";
    private final String password = "password";
    private final SocialType socialType = SocialType.LOCAL;
    private User user;
    @BeforeEach
    void beforeTest(){
        user = User.of(email, password, nickname);
    }

    @Test
    @DisplayName("email과 socialType으로 유저 조회")
    void findByEmailAndSocialType() {
        // given
        User saveUser = userRepository.save(user);

        // when
        User findUser = userRepository.findByEmailAndSocialType(email, socialType)
                .orElseThrow(() -> new IllegalArgumentException("Wrong MemberId:<" + saveUser.getId() + ">"));

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("nickname으로 유저 조회")
    void findByNickname() {
        // given
        User saveUser = userRepository.save(user);

        // when
        User findUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Wrong MemberId:<" + saveUser.getId() + ">"));

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("RefreshToken으로 유저 조회")
    void findByRefreshToken() {
        // given
        String refreshToken = "원래는 SERVICE 계층으로 주입됨";
        user.updateRefreshToken(refreshToken);
        User saveUser = userRepository.save(user);

        // when
        User findUser = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Wrong MemberId:<" + saveUser.getId() + ">"));

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("SocialType과 SocialId로 유저 조회")
    void findBySocialTypeAndSocialId() {
        // given
        User saveUser = userRepository.save(user);

        // when
        User findUser = userRepository.findBySocialTypeAndSocialId(socialType, null)
                .orElseThrow(() -> new IllegalArgumentException("Wrong MemberId:<" + saveUser.getId() + ">"));

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("email로 유저 조회")
    void findByEmail() {
        // given
        User saveUser = userRepository.save(user);

        // when
        User findUser = userRepository.findByEmail(saveUser.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Wrong MemberId:<" + saveUser.getId() + ">"));

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }

    @Test
    @DisplayName("email와 socialType으로 삭제된 유저 조회")
    void findUserByEmailAndSocialTypeIgnoringWhere() {
        // given
        user.delete();
        User saveUser = userRepository.save(user);

        // when
        User findUser = userRepository.findUserByEmailAndSocialTypeIgnoringWhere(saveUser.getEmail(), socialType.toString())
                .orElseThrow(() -> new IllegalArgumentException("Wrong MemberId:<" + saveUser.getId() + ">"));

        // then
        Assertions.assertThat(findUser).isEqualTo(user);
    }
}