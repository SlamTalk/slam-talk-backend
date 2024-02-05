package sync.slamtalk.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.common.BaseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users") // User가 예약어라서 users로 테이블이름 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
@Builder
public class User extends BaseEntity implements UserDetails {

    /* 레벨 시스템을 위한 상수 */
    public final static Long LEVEL_THRESHOLD = 50L;
    public final static Long MATE_LEVEL_SCORE = 5L;
    public final static Long TEAM_MATCHING_LEVEL_SCORE = 5L;
    public final static Long ATTEND_SCORE = 1L;
    public final static Long BASKETBALL_COURT_TIP_SCORE = 30L;

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String password;
    @Column(nullable = false, unique = true)
    private String nickname;
    @Column(nullable = false)
    private String email;
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
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

    /* 정보 수집 부분 */
    @Column(name = "first_login_check", nullable = false)
    private Boolean firstLoginCheck;
    @Column(name = "basketball_skill_level")
    @Enumerated(EnumType.STRING)
    private UserBasketballSkillLevelType basketballSkillLevel;
    @Column(name = "basketball_position")
    @Enumerated(EnumType.STRING)
    private UserBasketballPositionType basketballPosition;

    /* 알람 기능 */
    @Column(name = "is_alarm_set")
    private String isAlarmSet;

    /* 연관 관계 매핑 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

/*
    //todo : 동수님 연관관계 매핑 부분
    @OneToMany(mappedBy = "writerId", fetch = FetchType.LAZY)
    private List<MatePost> matePosts = new ArrayList<>();
*/

    /* 출석 관련 매핑 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserAttendance> userAttendances = new ArrayList<>();

    /**
     * 비밀번호 암호화 메소드
     * @param passwordEncoder
     * */
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    /**
     * 리프레쉬 토큰 update하는 메서드
     *
     * @param refreshToken
     * */
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    /**
     * 최초 로그인 상태를 false로 설정하는 메서드
     * */
    public void updateFirstLoginCheck(){
        this.firstLoginCheck = false;
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
