package sync.slamtalk.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where  u.email = :email and u.socialType = :socialType")
    Optional<User> findByEmailAndSocialType(@Param("email")String email, @Param("socialType")SocialType socialType);

    Optional<User> findByNickname(String nickname);

    @Query("select u from User u where u.refreshToken = :refreshToken")
    Optional<User> findByRefreshToken(@Param("refreshToken")String refreshToken);

}
