package sync.slamtalk.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.common.BaseEntity;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.user.utils.UserDefaultImageUrls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users") // User가 예약어라서 users로 테이블이름 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
@SQLDelete(sql = "UPDATE users SET is_deleted = true, refresh_token = null  WHERE id = ?")
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String password;
    @Column(nullable = false, unique = true)
    private String nickname;
    private String email;
    private String imageUrl;
    @Column
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    @Column
    private String socialId;

    /* 마이페이지 기능 */
    @Column
    private String selfIntroduction;
    @Column
    private String regionName;

    /* 정보 수집 부분 */
    @Column(nullable = false)
    private Boolean firstLoginCheck;
    @Column
    @Enumerated(EnumType.STRING)
    private UserBasketballSkillLevelType basketballSkillLevel;
    @Column
    @Enumerated(EnumType.STRING)
    private UserBasketballPositionType basketballPosition;

    /* 알람 기능 */
    @Column
    private String isAlarmSet;

    /* 연관 관계 매핑 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
    private List<MatePost> matePosts = new ArrayList<>();

    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
    private List<TeamMatching> teamMatchings = new ArrayList<>();

    @OneToMany(mappedBy = "opponent", fetch = FetchType.LAZY)
    private List<TeamMatching> opponentTeamMatchings = new ArrayList<>();

    /* 출석 관련 매핑 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserAttendance> userAttendances = new ArrayList<>();

    /**
     * 소셜 ID가 필요 없는 경우(예: 로컬 인증을 사용하는 경우)에 사용되는 오버로딩된 생성자입니다.
     * 이 메서드를 호출하면 소셜 로그인이 아닌, 일반 이메일과 비밀번호 기반으로 유저를 생성합니다.
     * 디폴트 이미지 URL을 사용하고, 소셜 타입은 'LOCAL'로 설정됩니다.
     * 소셜 ID는 null로 설정됩니다.
     *
     * @param email 유저의 이메일 주소입니다. 유저를 식별하는데 사용됩니다.
     * @param password 유저의 비밀번호입니다. 로그인 인증을 위해 사용됩니다.
     * @param nickname 유저의 닉네임입니다. 유저의 별칭으로 사용됩니다.
     * @return User 객체를 반환합니다. 해당 객체는 주어진 매개변수를 바탕으로 설정되어 있습니다.
     */
    public static User of(String email,
                          String password,
                          String nickname) {
        // socialId가 필요 없는 경우를 위한 오버로딩 메서드
        return of(email,
                password,
                nickname,
                SocialType.LOCAL,
                UserDefaultImageUrls.DEFAULT_IMAGE_URL,
                null);
    }

    /**
     * 사용자 객체를 생성하는 정적 메서드로, 주로 소셜 로그인 시 사용됩니다.
     * 이 메서드는 사용자의 이메일, 비밀번호, 닉네임, 소셜 로그인 타입, 프로필 이미지 URL 및 소셜 아이디를 매개변수로 받습니다.
     *
     * @param email      사용자 이메일. 고유해야 하며, 사용자 식별에 사용됩니다.
     * @param password   사용자 비밀번호. 소셜 로그인 시 일부 소셜 서비스는 사용하지 않을 수 있습니다.
     * @param nickname   사용자 닉네임. 사용자가 선택한 별명입니다.
     * @param socialType 소셜 로그인 타입. 이용 중인 소셜 로그인 서비스의 타입입니다.
     * @param imageUrl   프로필 이미지의 URL. 사용자가 선택한 프로필 이미지 주소입니다.
     * @param socialId   소셜 로그인을 위한 사용자의 소셜 아이디. 이 값은 `null`일 수 있습니다.
     * @return User 객체를 반환합니다. 생성된 사용자 객체에는 이메일, 비밀번호, 닉네임, 사용자 역할(기본값: USER),
     * 소셜 로그인 타입, 소셜 아이디, 이미지 URL이 포함됩니다.
     */
    public static User of(String email,
                          String password,
                          String nickname,
                          SocialType socialType,
                          String imageUrl,
                          String socialId) {
        // 실제 User 객체 생성 및 반환 로직
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(UserRole.USER) // 사용자 역할. 기본적으로 'USER'로 설정됩니다.
                .socialType(socialType)
                .socialId(socialId) // 이 파라미터는 null일 수 있음을 주의하세요.
                .imageUrl(imageUrl)
                .firstLoginCheck(true)
                .build();
    }

    public void userWithdrawal(String newNickname) {
        delete();
        this.nickname = newNickname;
        this.password = null;
        this.email = null;
        this.imageUrl = null;
        this.socialId = null;
        this.socialType = SocialType.NONE;
        this.refreshToken = null;
        this.selfIntroduction = null;
        this.basketballSkillLevel = null;
        this.basketballPosition = null;
    }

    /**
     * 리프레쉬 토큰 update하는 메서드
     *
     * @param refreshToken
     */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * 최초 로그인 상태를 false로 설정하는 메서드
     */
    public void disableFirstLogin() {
        this.firstLoginCheck = false;
    }

    /**
     * 유저 프로필 업데이트하는 메서드
     *
     * @param imageUrl 이미지 URL
     */
    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 유저 프로필 업데이트하는 메서드
     *
     * @param nickname 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 유저 자기소개 한마디 업데이트 로직
     *
     * @param selfIntroduction 자기 소개 한마디
     */
    public void updateSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    /**
     * 유저 포지션 업데이트 로직
     *
     * @param basketballPosition UserBasketballPositionType
     */
    public void updatePosition(UserBasketballPositionType basketballPosition) {
        this.basketballPosition = basketballPosition;
    }

    /**
     * 유저 자체 농구 실력 업데이트 로직
     *
     * @param basketballSkillLevel UserBasketballSkillLevelType
     */
    public void updateBasketballSkillLevel(UserBasketballSkillLevelType basketballSkillLevel) {
        this.basketballSkillLevel = basketballSkillLevel;
    }

    /**
     * 사용자 객체의 비밀번호를 업데이트합니다.
     * 이 메서드는 새로운 비밀번호가 이미 암호화되었을 때 사용됩니다.
     * 암호화된 비밀번호를 매개변수로 받아 객체의 비밀번호 필드를 업데이트 합니다.
     *
     * @param password 새로운 암호화된 비밀번호입니다.
     */
    public void updatePassword(String password) {
        this.password = password;
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