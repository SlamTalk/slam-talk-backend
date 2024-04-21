package sync.slamtalk.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.user.entity.SocialType;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.entity.UserRole;

/**
 * 회원가입 시 받을 dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpReq {

    private static final String DEFAULT_IMAGE_URL = "https://slamtalks3.s3.ap-northeast-2.amazonaws.com/userprofile-default_1706862413360.png";

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$",
            message = "이메일 형식에 맞지 않습니다."
    )
    private String email;
    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요."
    )
    private String password;
    @Pattern(
            regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,13}$",
            message = "닉네임은 특수문자를 제외한 2~13자리여야 합니다."
    )
    private String nickname;

    /**
     * userSignUpDto 를 User로 변환
     *
     * @return user 유저 entity
     */
    public User toEntity() {
        return User.builder()
                .email(this.getEmail())
                .password(this.getPassword())
                .nickname(this.getNickname())
                .imageUrl(DEFAULT_IMAGE_URL)
                .role(UserRole.USER)
                .socialType(SocialType.LOCAL)
                .firstLoginCheck(true)
                .build();
    }
}
