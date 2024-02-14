package sync.slamtalk.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.entity.UserBasketballPositionType;
import sync.slamtalk.user.entity.UserBasketballSkillLevelType;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where  u.email = :email and u.socialType = :social_type")
    Optional<User> findByEmailAndSocialType(
            @Param("email") String email,
            @Param("social_type") SocialType socialType
    );

    Optional<User> findByNickname(String nickname);

    @Query("select u from User u where u.refreshToken = :refresh_token")
    Optional<User> findByRefreshToken(@Param("refresh_token") String refreshToken);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String id);

    @Modifying
    @Query("update User u  " +
            "set u.nickname = :nickname " +
            "where u.id = :user_id")
    void updateUserNickname(
            @Param("user_id") Long userId,
            @Param("nickname") String nickname
    );

    @Modifying
    @Query("update User u  " +
            "set u.basketballSkillLevel = :basketball_skill_level " +
            ", u.basketballPosition = :basketball_position " +
            "where u.id = :user_id")
    void updateUserPositionAndSkillLevel(
            @Param("user_id") Long userId,
            @Param("basketball_skill_level") UserBasketballSkillLevelType basketballSkillLevel,
            @Param("basketball_position") UserBasketballPositionType basketballPosition
    );

    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE email = :email AND social_type = :socialType", nativeQuery = true)
    Optional<User> findUserByEmailAndSocialTypeIgnoringWhere(@Param("email") String email, @Param("socialType") String socialType);
}