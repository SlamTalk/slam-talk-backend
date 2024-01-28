package sync.slamtalk.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;

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
}
