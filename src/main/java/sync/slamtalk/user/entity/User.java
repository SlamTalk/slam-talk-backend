package sync.slamtalk.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.user.dto.UserSignUpRequestDto;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users") // User가 예약어라서 users로 테이블이름 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
public class User extends BaseEntity implements UserDetails {
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

    /* 알람 기능 */
    @Column(name = "is_alarm_set")
    private String isAlarmSet;

    /* 레벨 시스템 기능 */
    @Column(name = "level_score", nullable = false)
    private Long levelScore = 0L;

    /* 연관 관계 매핑 */
/*
    // todo : 예지님 연관관계 매핑 부분
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();
*/

/*
    //todo : 동수님 연관관계 매핑 부분
    @OneToMany(mappedBy = "writerId", fetch = FetchType.LAZY)
    private List<MatePost> matePosts = new ArrayList<>();
*/

    /**
     * 비밀번호 암호화 메소드
     * @param passwordEncoder
     * */
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    /**
     * userSignUpDto 를 User로 변환
     * @param userSignUpDto 유저회원가입 dto
     * @return user 유저 entity
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

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }


    /* UserDetails 관련 메서드 */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.getRole().getKey()));
    }

    @Override
    public String getUsername() {
        return this.id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
