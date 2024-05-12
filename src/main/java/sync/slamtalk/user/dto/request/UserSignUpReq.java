package sync.slamtalk.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 시 받을 dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpReq {

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$",
            message = "이메일 형식에 맞지 않습니다."
    )
    private String email;
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[~!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>\\/?|\\\\])[A-Za-z0-9~!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>\\/?|\\\\]{8,16}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 각각 최소 하나 이상 포함하며, 8자 이상 16자 이하이어야 합니다.")
    private String password;
    @Pattern(
            regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,13}$",
            message = "닉네임은 특수문자를 제외한 2~13자리여야 합니다."
    )
    private String nickname;
}
