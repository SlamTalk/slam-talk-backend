package sync.slamtalk.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.user.dto.UserSignUpRequestDto;

@Entity
@Table(name = "users") // User가 예약어라서 users로 테이블이름 명시
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User extends BaseEntity {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String password;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String email;
    @Column(name = "image_uri")
    private String imageUri;
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "social_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    @Column(name = "social_id")
    private String socialId;

    /* 마이페이지 기능 */
    @Column(name = "self_introduction")
    private String selfIntroduction;
    @Column(name = "region_name")
    private String regionName;
    @Column(name = "basketball_skill_level")
    @Enumerated(EnumType.STRING)
    private UserBasketballSkillLevelType basketballSkillLevel;
    @Column(name = "basketball_position")
    @Enumerated(EnumType.STRING)
    private UserBasketballPositionType basketballPosition;

    /* 알람 기능*/
    @Column(name = "is_alarm_set")
    private String isAlarmSet;

    /* 레벨 시스템 기능*/
    @Column(name = "level_score", nullable = false)
    private Long levelScore = 0L;

    /**
     * 비밀번호 암호화 메소드
     * @param passwordEncoder
     * */
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    /**
     * userSignUpDto 를 User로 변환
     * @param userSignUpDto
     * @return user
     * */
    public static User from(UserSignUpRequestDto userSignUpDto) {

        User user = new User();
        user.email = userSignUpDto.getEmail();
        user.password = userSignUpDto.getPassword();
        user.nickname = userSignUpDto.getNickname();
        user.role = UserRole.USER;
        user.levelScore = 0L;
        user.socialType = SocialType.LOCAL;
        return user;
    }
}
